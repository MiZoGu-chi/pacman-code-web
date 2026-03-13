<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-sRIl4kxILFvY47J16cr9ZwB07vP4J8+LH7qKQnuqkuIAvNWLzeN8tE5YBujZqJLB" crossorigin="anonymous">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js" integrity="sha384-FKyoEForCGlyvwx9Hj09JcYn3nv7wiPVlz7YYwJrWVcXK/BmnVDxM+D2scQbITxI" crossorigin="anonymous"></script>
    <title>Pacman - Game ${gameId}</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            background-color: #000;
            color: #fff;
            font-family: 'Press Start 2P', 'Courier New', monospace;
            display: flex;
            flex-direction: column;
            align-items: center;
            min-height: 100vh;
            padding: 20px;
        }
        
        h1 {
            color: #ffff00;
            margin-bottom: 10px;
            font-size: 24px;
            text-shadow: 2px 2px 4px #ff0000;
        }
        
        .game-info {
            display: flex;
            gap: 30px;
            margin-bottom: 15px;
            font-size: 14px;
        }
        
        .game-info span {
            color: #00ffff;
        }
        
        #gameCanvas {
            border: 3px solid #0000ff;
            background-color: #000;
        }
        
        .controls {
            margin-top: 20px;
            display: flex;
            flex-direction: column;
            align-items: center;
            gap: 5px;
        }
        
        .controls-row {
            display: flex;
            gap: 5px;
        }
        
        .control-btn {
            width: 50px;
            height: 50px;
            font-size: 20px;
            background-color: #333;
            color: #fff;
            border: 2px solid #666;
            border-radius: 8px;
            cursor: pointer;
            transition: all 0.1s;
        }
        
        .control-btn:hover {
            background-color: #555;
        }
        
        .control-btn:active {
            background-color: #777;
            transform: scale(0.95);
        }
        
        .game-buttons {
            margin-top: 15px;
            display: flex;
            gap: 10px;
        }
        
        .game-btn {
            padding: 10px 20px;
            font-size: 12px;
            font-family: inherit;
            background-color: #0066cc;
            color: #fff;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            transition: background-color 0.2s;
        }
        
        .game-btn:hover {
            background-color: #0088ff;
        }
        
        .game-btn.start { background-color: #00aa00; }
        .game-btn.start:hover { background-color: #00cc00; }
        .game-btn.pause { background-color: #cc6600; }
        .game-btn.pause:hover { background-color: #ff8800; }
        
        .status {
            margin-top: 10px;
            font-size: 12px;
            color: #888;
        }
        
        .status.connected { color: #00ff00; }
        .status.disconnected { color: #ff0000; }
        
        .players-info {
            margin-top: 15px;
            font-size: 11px;
            color: #aaa;
        }
        
        .message {
            position: fixed;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            background-color: rgba(0, 0, 0, 0.9);
            padding: 30px 50px;
            border: 3px solid #ffff00;
            border-radius: 10px;
            font-size: 24px;
            text-align: center;
            display: none;
            z-index: 100;
        }
        
        .message.show {
            display: block;
        }
        
        .message.victory {
            color: #00ff00;
            border-color: #00ff00;
        }
        
        .message.gameover {
            color: #ff0000;
            border-color: #ff0000;
        }
    </style>
</head>
<body>
    <h1>PACMAN</h1>
    <p style="color: #888; font-size: 10px; margin-bottom: 10px;">Game: ${gameId} | Player: ${playerId}</p>
    
    <div class="game-info">
        <div>SCORE: <span id="score">0</span></div>
        <div>LIVES: <span id="lives">3</span></div>
        <div>TURN: <span id="turn">0</span></div>
    </div>
    <!-- Button trigger modal -->
<button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#historyModal">
  View history
</button>

<!-- Modal -->
<div class="modal fade" id="historyModal" tabindex="-1" aria-labelledby="historyModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
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
    <div class="players-info" id="playersInfo"></div>
    
    <div class="message" id="gameMessage"></div>
    
    <script>
        // Configuration
        const CELL_SIZE = 20;
        const gameId = '${gameId}';
        const playerId = '${playerId}';
        const wsUrl = '${wsUrl}';
        
        // Canvas setup
        const canvas = document.getElementById('gameCanvas');
        const ctx = canvas.getContext('2d');
        
        // Game state
        let gameState = null;
        let ws = null;
        let myPacmanIndex = 0;
        
        // Colors
        const COLORS = {
            wall: '#0000ff',
            food: '#ffff00',
            capsule: '#ffb851',
            pacman: '#ffff00',
            pacmanInvincible: '#ffffff',
            ghost: ['#ff0000', '#ffb8ff', '#00ffff', '#ffb851'],
            ghostScared: '#0000ff',
            background: '#000000'
        };
        
        // Connect to WebSocket
        function connect() {
            ws = new WebSocket(wsUrl);
            
            ws.onopen = function() {
                document.getElementById('connectionStatus').textContent = 'Connected';
                document.getElementById('connectionStatus').className = 'status connected';
                console.log('WebSocket connected');
            };
            
            ws.onmessage = function(event) {
                const data = JSON.parse(event.data);
                handleMessage(data);
            };
            
            ws.onclose = function() {
                document.getElementById('connectionStatus').textContent = 'Disconnected - Reconnecting...';
                document.getElementById('connectionStatus').className = 'status disconnected';
                console.log('WebSocket disconnected');
                // Reconnect after 2 seconds
                setTimeout(connect, 2000);
            };
            
            ws.onerror = function(error) {
                console.error('WebSocket error:', error);
            };
        }
        
        // Handle incoming messages
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
                    
                case 'playerJoined':
                    console.log('Player joined:', data.playerId);
                    break;
                    
                case 'playerLeft':
                    console.log('Player left:', data.playerId);
                    break;
                    
                case 'gameStarted':
                    hideMessage();
                    break;
                    
                case 'gamePaused':
                    showMessage('PAUSED', '');
                    break;
                    
                case 'gameResumed':
                    hideMessage();
                    break;
            }
        }
        
        // Resize canvas based on maze size
        function resizeCanvas() {
            if (gameState) {
                canvas.width = gameState.mazeWidth * CELL_SIZE;
                canvas.height = gameState.mazeHeight * CELL_SIZE;
            }
        }
        
        // Update UI elements
        function updateUI() {
            if (gameState) {
                document.getElementById('score').textContent = gameState.score;
                document.getElementById('lives').textContent = gameState.lives;
                document.getElementById('turn').textContent = gameState.turn;
                
                // Check for game over or victory
                const alivePacmans = gameState.pacmans.filter(p => p.alive);
                if (alivePacmans.length === 0 && gameState.turn > 0) {
                    showMessage('GAME OVER', 'gameover');
                }
                
                if (gameState.food && gameState.food.length === 0 && gameState.turn > 0) {
                    showMessage('VICTORY!', 'victory');
                }
            }
        }
        
        // Render the game
        function render() {
            if (!gameState) return;
            
            // Clear canvas
            ctx.fillStyle = COLORS.background;
            ctx.fillRect(0, 0, canvas.width, canvas.height);
            
            // Draw walls
            ctx.fillStyle = COLORS.wall;
            if (gameState.walls) {
                gameState.walls.forEach(wall => {
                    ctx.fillRect(wall.x * CELL_SIZE, wall.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                });
            }
            
            // Draw food
            ctx.fillStyle = COLORS.food;
            if (gameState.food) {
                gameState.food.forEach(f => {
                    ctx.beginPath();
                    ctx.arc(
                        f.x * CELL_SIZE + CELL_SIZE / 2,
                        f.y * CELL_SIZE + CELL_SIZE / 2,
                        3, 0, Math.PI * 2
                    );
                    ctx.fill();
                });
            }
            
            // Draw capsules
            ctx.fillStyle = COLORS.capsule;
            if (gameState.capsules) {
                gameState.capsules.forEach(c => {
                    ctx.beginPath();
                    ctx.arc(
                        c.x * CELL_SIZE + CELL_SIZE / 2,
                        c.y * CELL_SIZE + CELL_SIZE / 2,
                        7, 0, Math.PI * 2
                    );
                    ctx.fill();
                });
            }
            
            // Draw ghosts
            if (gameState.ghosts) {
                gameState.ghosts.forEach((ghost, index) => {
                    if (ghost.alive) {
                        // Check if any pacman is invincible (ghosts are scared)
                        const anyInvincible = gameState.pacmans.some(p => p.invincible);
                        ctx.fillStyle = anyInvincible ? COLORS.ghostScared : COLORS.ghost[index % 4];
                        drawGhost(ghost.x, ghost.y);
                    }
                });
            }
            
            // Draw pacmans
            if (gameState.pacmans) {
                gameState.pacmans.forEach((pacman, index) => {
                    if (pacman.alive) {
                        ctx.fillStyle = pacman.invincible ? COLORS.pacmanInvincible : COLORS.pacman;
                        // Highlight current player's pacman
                        if (index === myPacmanIndex) {
                            ctx.shadowColor = '#ffff00';
                            ctx.shadowBlur = 10;
                        }
                        drawPacman(pacman.x, pacman.y, pacman.dir);
                        ctx.shadowBlur = 0;
                    }
                });
            }
        }
        
        // Draw pacman shape
        function drawPacman(x, y, dir) {
            const cx = x * CELL_SIZE + CELL_SIZE / 2;
            const cy = y * CELL_SIZE + CELL_SIZE / 2;
            const radius = CELL_SIZE / 2 - 2;
            
            // Mouth angle based on direction
            let startAngle, endAngle;
            const mouthAngle = 0.3;
            
            switch (dir) {
                case 0: // North
                    startAngle = -Math.PI / 2 + mouthAngle;
                    endAngle = -Math.PI / 2 - mouthAngle + Math.PI * 2;
                    break;
                case 1: // South
                    startAngle = Math.PI / 2 + mouthAngle;
                    endAngle = Math.PI / 2 - mouthAngle + Math.PI * 2;
                    break;
                case 2: // East
                    startAngle = mouthAngle;
                    endAngle = -mouthAngle + Math.PI * 2;
                    break;
                case 3: // West
                    startAngle = Math.PI + mouthAngle;
                    endAngle = Math.PI - mouthAngle + Math.PI * 2;
                    break;
                default:
                    startAngle = mouthAngle;
                    endAngle = -mouthAngle + Math.PI * 2;
            }
            
            ctx.beginPath();
            ctx.moveTo(cx, cy);
            ctx.arc(cx, cy, radius, startAngle, endAngle);
            ctx.closePath();
            ctx.fill();
        }
        
        // Draw ghost shape
        function drawGhost(x, y) {
            const cx = x * CELL_SIZE + CELL_SIZE / 2;
            const cy = y * CELL_SIZE + CELL_SIZE / 2;
            const radius = CELL_SIZE / 2 - 2;
            
            ctx.beginPath();
            // Top half circle
            ctx.arc(cx, cy, radius, Math.PI, 0);
            // Bottom wavy part
            const waveHeight = 3;
            const waveWidth = radius / 2;
            ctx.lineTo(cx + radius, cy + radius);
            for (let i = 0; i < 4; i++) {
                const wx = cx + radius - (i + 0.5) * waveWidth;
                const wy = cy + radius + (i % 2 === 0 ? waveHeight : -waveHeight);
                ctx.lineTo(wx, wy);
            }
            ctx.lineTo(cx - radius, cy + radius);
            ctx.closePath();
            ctx.fill();
            
            // Eyes
            ctx.fillStyle = '#fff';
            ctx.beginPath();
            ctx.arc(cx - 4, cy - 2, 3, 0, Math.PI * 2);
            ctx.arc(cx + 4, cy - 2, 3, 0, Math.PI * 2);
            ctx.fill();
            
            ctx.fillStyle = '#000';
            ctx.beginPath();
            ctx.arc(cx - 3, cy - 2, 1.5, 0, Math.PI * 2);
            ctx.arc(cx + 5, cy - 2, 1.5, 0, Math.PI * 2);
            ctx.fill();
        }
        
        // Send move command
        function move(direction) {
            if (ws && ws.readyState === WebSocket.OPEN) {
                ws.send(JSON.stringify({
                    action: 'move',
                    direction: direction
                }));
            }
        }
        
        // Start game
        function startGame() {
            if (ws && ws.readyState === WebSocket.OPEN) {
                ws.send(JSON.stringify({ action: 'start' }));
            }
        }
        
        // Pause game
        function pauseGame() {
            if (ws && ws.readyState === WebSocket.OPEN) {
                ws.send(JSON.stringify({ action: 'pause' }));
            }
        }
        
        // Resume game
        function resumeGame() {
            if (ws && ws.readyState === WebSocket.OPEN) {
                ws.send(JSON.stringify({ action: 'resume' }));
            }
        }
        
        // Show message overlay
        function showMessage(text, type) {
            const msg = document.getElementById('gameMessage');
            msg.textContent = text;
            msg.className = 'message show ' + type;
        }
        
        // Hide message overlay
        function hideMessage() {
            document.getElementById('gameMessage').className = 'message';
        }
        
        // Keyboard controls
        document.addEventListener('keydown', function(event) {
            switch (event.key) {
                case 'ArrowUp':
                case 'w':
                case 'W':
                    move('UP');
                    event.preventDefault();
                    break;
                case 'ArrowDown':
                case 's':
                case 'S':
                    move('DOWN');
                    event.preventDefault();
                    break;
                case 'ArrowLeft':
                case 'a':
                case 'A':
                    move('LEFT');
                    event.preventDefault();
                    break;
                case 'ArrowRight':
                case 'd':
                case 'D':
                    move('RIGHT');
                    event.preventDefault();
                    break;
                case ' ':
                    startGame();
                    event.preventDefault();
                    break;
                case 'p':
                case 'P':
                    pauseGame();
                    event.preventDefault();
                    break;
            }
        });
        
        // Initialize
        connect();
    </script>
</body>
</html>
