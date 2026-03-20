<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <title>Pacman - Game ${gameId}</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/game.css">
</head>
<body>
    <h1>PACMAN</h1>
    <p style="color: #888; font-size: 10px; margin-bottom: 10px;">Game: ${gameId} | Player: ${playerId}</p>
    
    <div class="game-info">
        <div>SCORE: <span id="score">0</span></div>
        <div>LIVES: <span id="lives">3</span></div>
        <div>TURN: <span id="turn">0</span></div>
    </div>

    <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#historyModal">
      View history
    </button>

    <div class="modal fade" id="historyModal" tabindex="-1" aria-labelledby="historyModalLabel" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content text-dark"> <div class="modal-header">
            <h1 class="modal-title fs-5" id="historyModalLabel">Game History</h1>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body" id="historyContent">
            </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
          </div>
        </div>
      </div>
    </div>

    <canvas id="gameCanvas" width="560" height="620"></canvas>
    
    <div class="game-buttons">
        <button class="game-btn start" onclick="startGame()">START</button>
        <button class="game-btn pause" onclick="pauseGame()">PAUSE</button>
        <button class="game-btn" onclick="resumeGame()">RESUME</button>
    </div>
    
    <div class="controls">
        <div class="controls-row">
            <button class="control-btn" onclick="move('UP')">↑</button>
        </div>
        <div class="controls-row">
            <button class="control-btn" onclick="move('LEFT')">←</button>
            <button class="control-btn" onclick="move('DOWN')">↓</button>
            <button class="control-btn" onclick="move('RIGHT')">→</button>
        </div>
    </div>
    
    <div class="status" id="connectionStatus">Connecting...</div>
    <div class="message" id="gameMessage"></div>
    
    <script>
        // Configuration transmise par le contrôleur (GameControllerServlet)
        const CELL_SIZE = 20;
        const gameId = '${gameId}';
        const playerId = '${playerId}';
        const wsUrl = '${wsUrl}';
        
        const canvas = document.getElementById('gameCanvas');
        const ctx = canvas.getContext('2d');
        let gameState = null;
        let ws = null;
        let myPacmanIndex = 0;

        const COLORS = {
            wall: '#0000ff', food: '#ffff00', capsule: '#ffb851',
            pacman: '#ffff00', pacmanInvincible: '#ffffff',
            ghost: ['#ff0000', '#ffb8ff', '#00ffff', '#ffb851'],
            ghostScared: '#0000ff', background: '#000000'
        };

        // --- LOGIQUE DE RÉCUPÉRATION DES SCORES (MVC : Appel au Contrôleur /getScores) ---
        function loadScores() {
            const historyContent = document.getElementById('historyContent');
            historyContent.innerHTML = '<p>Loading scores...</p>';

            fetch('${pageContext.request.contextPath}/getScores')
                .then(response => {
                    if (!response.ok) throw new Error('Network error');
                    return response.json();
                })
                .then(data => {
                    if (data.length === 0) {
                        historyContent.innerHTML = "<p>No scores recorded yet.</p>";
                        return;
                    }
                    let html = '<table class="table"><thead><tr><th>Player</th><th>Score</th></tr></thead><tbody>';
                    data.forEach(s => {
                        html += '<tr><td>' + s.playerName + '</td><td>' + s.score + '</td></tr>';
                    });
                    html += '</tbody></table>';
                    historyContent.innerHTML = html;
                })
                .catch(error => {
                    console.error('Error:', error);
                    historyContent.innerHTML = '<div class="alert alert-danger">Failed to load scores.</div>';
                });
        }

        // Liaison de l'événement au bouton du modal
        document.querySelector('[data-bs-target="#historyModal"]').addEventListener('click', loadScores);

        // --- LOGIQUE WEBSOCKET ET RENDU (Logique de Vue Temps Réel) ---
        function connect() {
            ws = new WebSocket(wsUrl);
            ws.onopen = () => {
                document.getElementById('connectionStatus').textContent = 'Connected';
                document.getElementById('connectionStatus').className = 'status connected';
            };
            ws.onmessage = (event) => handleMessage(JSON.parse(event.data));
            ws.onclose = () => {
                document.getElementById('connectionStatus').textContent = 'Disconnected - Reconnecting...';
                setTimeout(connect, 2000);
            };
        }

        function handleMessage(data) {
            switch (data.type) {
                case 'init': 
                    myPacmanIndex = data.pacmanIndex;
                    gameState = data.state;
                    resizeCanvas();
                    render();
                    break;
                case 'state':
                    gameState = data.state;
                    updateUI();
                    render();
                    break;
                case 'gamePaused': showMessage('PAUSED', ''); break;
                case 'gameResumed': hideMessage(); break;
                case 'gameStarted': hideMessage(); break;
            }
        }

        function resizeCanvas() {
            if (gameState) {
                canvas.width = gameState.mazeWidth * CELL_SIZE;
                canvas.height = gameState.mazeHeight * CELL_SIZE;
            }
        }

        function updateUI() {
            if (gameState) {
                document.getElementById('score').textContent = gameState.score;
                document.getElementById('lives').textContent = gameState.lives;
                document.getElementById('turn').textContent = gameState.turn;
                
                const alivePacmans = gameState.pacmans.filter(p => p.alive);
                if (alivePacmans.length === 0 && gameState.turn > 0) showMessage('GAME OVER', 'gameover');
                if (gameState.food && gameState.food.length === 0 && gameState.turn > 0) showMessage('VICTORY!', 'victory');
            }
        }

        function render() {
        	if (!gameState) return;
            ctx.fillStyle = COLORS.background;
            ctx.fillRect(0, 0, canvas.width, canvas.height);
            
            if (gameState.walls) {
                gameState.walls.forEach(w => {
                    // Carré extérieur (Cyan / wallColor2)
                    ctx.fillStyle = '#00ffff'; 
                    ctx.fillRect(w.x * CELL_SIZE, w.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    
                    // Carré intérieur (Bleu / wallColor)
                    ctx.fillStyle = '#0000ff';
                    const padding = CELL_SIZE * 0.25;
                    ctx.fillRect(w.x * CELL_SIZE + padding, w.y * CELL_SIZE + padding, 
                                 CELL_SIZE - 2*padding, CELL_SIZE - 2*padding);
                });
            }
            
            if (gameState.food) {
                ctx.fillStyle = COLORS.food;
                gameState.food.forEach(f => {
                    ctx.beginPath();
                    ctx.arc(f.x * CELL_SIZE + CELL_SIZE/2, f.y * CELL_SIZE + CELL_SIZE/2, 3, 0, Math.PI*2);
                    ctx.fill();
                });
            }
            if (gameState.ghosts) {
                gameState.ghosts.forEach((g, i) => {
                    if (g.alive) {
                        const scared = gameState.pacmans.some(p => p.invincible);
                        ctx.fillStyle = scared ? COLORS.ghostScared : COLORS.ghost[i % 4];
                        drawGhost(g.x, g.y);
                    }
                });
            }
            if (gameState.pacmans) {
                gameState.pacmans.forEach((p, i) => {
                    if (p.alive) {
                        ctx.fillStyle = p.invincible ? COLORS.pacmanInvincible : COLORS.pacman;
                        if (i === myPacmanIndex) { ctx.shadowColor = '#ffff00'; ctx.shadowBlur = 10; }
                        drawPacman(p.x, p.y, p.dir);
                        ctx.shadowBlur = 0;
                    }
                });
            }
        }

        function drawPacman(x, y, dir) {
            const cx = x * CELL_SIZE + CELL_SIZE / 2;
            const cy = y * CELL_SIZE + CELL_SIZE / 2;
            const radius = CELL_SIZE / 2 - 2;
            let startAngle, endAngle;
            const mouth = 0.3;
            switch (dir) {
                case 0: startAngle = -Math.PI/2 + mouth; endAngle = -Math.PI/2 - mouth; break;
                case 1: startAngle = Math.PI/2 + mouth; endAngle = Math.PI/2 - mouth; break;
                case 2: startAngle = mouth; endAngle = -mouth; break;
                case 3: startAngle = Math.PI + mouth; endAngle = Math.PI - mouth; break;
                default: startAngle = mouth; endAngle = -mouth;
            }
            ctx.beginPath();
            ctx.moveTo(cx, cy);
            ctx.arc(cx, cy, radius, startAngle, endAngle + (dir === 2 || dir === 3 || dir === 1 || dir === 0 ? Math.PI*2 : 0));
            ctx.closePath();
            ctx.fill();
        }

        function drawGhost(x, y) {
            const cx = x * CELL_SIZE + CELL_SIZE / 2, cy = y * CELL_SIZE + CELL_SIZE / 2, r = CELL_SIZE / 2 - 2;
            ctx.beginPath();
            ctx.arc(cx, cy, r, Math.PI, 0);
            ctx.lineTo(cx + r, cy + r);
            for (let i = 0; i < 4; i++) {
                ctx.lineTo(cx + r - (i + 0.5) * (r/2), cy + r + (i % 2 === 0 ? 3 : -3));
            }
            ctx.lineTo(cx - r, cy + r);
            ctx.fill();
            ctx.fillStyle = '#fff';
            ctx.beginPath(); ctx.arc(cx - 4, cy - 2, 3, 0, Math.PI*2); ctx.arc(cx + 4, cy - 2, 3, 0, Math.PI*2); ctx.fill();
            ctx.fillStyle = '#000';
            ctx.beginPath(); ctx.arc(cx - 3, cy - 2, 1.5, 0, Math.PI*2); ctx.arc(cx + 5, cy - 2, 1.5, 0, Math.PI*2); ctx.fill();
        }

        function move(dir) { if (ws?.readyState === 1) ws.send(JSON.stringify({action: 'move', direction: dir})); }
        function startGame() { if (ws?.readyState === 1) ws.send(JSON.stringify({action: 'start'})); }
        function pauseGame() { if (ws?.readyState === 1) ws.send(JSON.stringify({action: 'pause'})); }
        function resumeGame() { if (ws?.readyState === 1) ws.send(JSON.stringify({action: 'resume'})); }
        function showMessage(text, type) { const m = document.getElementById('gameMessage'); m.textContent = text; m.className = 'message show ' + type; }
        function hideMessage() { document.getElementById('gameMessage').className = 'message'; }

        document.addEventListener('keydown', (e) => {
            const keys = { 'ArrowUp': 'UP', 'w': 'UP', 'ArrowDown': 'DOWN', 's': 'DOWN', 'ArrowLeft': 'LEFT', 'a': 'LEFT', 'ArrowRight': 'RIGHT', 'd': 'RIGHT' };
            if (keys[e.key]) { move(keys[e.key]); e.preventDefault(); }
            if (e.key === ' ') { startGame(); e.preventDefault(); }
            if (e.key.toLowerCase() === 'p') pauseGame();
        });

        connect();
    </script>
</body>
</html>