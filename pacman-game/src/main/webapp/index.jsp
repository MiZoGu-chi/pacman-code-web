<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Pacman Multiplayer</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            background-color: #000;
            color: #fff;
            font-family: 'Courier New', monospace;
            min-height: 100vh;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            padding: 20px;
        }
        
        h1 {
            color: #ffff00;
            font-size: 48px;
            text-shadow: 3px 3px 6px #ff0000;
            margin-bottom: 40px;
            animation: pulse 2s infinite;
        }
        
        @keyframes pulse {
            0%, 100% { opacity: 1; }
            50% { opacity: 0.7; }
        }
        
        .menu {
            display: flex;
            flex-direction: column;
            gap: 20px;
            align-items: center;
        }
        
        .menu-btn {
            padding: 15px 40px;
            font-size: 18px;
            font-family: inherit;
            background-color: #0066cc;
            color: #fff;
            border: 3px solid #0088ff;
            border-radius: 10px;
            cursor: pointer;
            transition: all 0.2s;
            text-decoration: none;
            min-width: 250px;
            text-align: center;
        }
        
        .menu-btn:hover {
            background-color: #0088ff;
            transform: scale(1.05);
        }
        
        .menu-btn.new-game {
            background-color: #00aa00;
            border-color: #00cc00;
        }
        
        .menu-btn.new-game:hover {
            background-color: #00cc00;
        }
        
        .join-form {
            margin-top: 30px;
            display: flex;
            flex-direction: column;
            gap: 15px;
            align-items: center;
        }
        
        .join-form input {
            padding: 10px 20px;
            font-size: 16px;
            font-family: inherit;
            background-color: #222;
            color: #fff;
            border: 2px solid #444;
            border-radius: 5px;
            text-align: center;
        }
        
        .join-form input:focus {
            outline: none;
            border-color: #0088ff;
        }
        
        .layout-select {
            margin-top: 20px;
            display: flex;
            flex-direction: column;
            gap: 10px;
            align-items: center;
        }
        
        .layout-select select {
            padding: 10px 20px;
            font-size: 14px;
            background-color: #222;
            color: #fff;
            border: 2px solid #444;
            border-radius: 5px;
        }
        
        .pacman-icon {
            margin-right: 15px;
        }
        
        .pacman-icon img {
            width: 60px;
            height: 60px;
        }
        
        .title-row {
            display: flex;
            align-items: center;
            margin-bottom: 40px;
        }
        
        .title-row h1 {
            margin-bottom: 0;
        }
        
        .footer {
            margin-top: 50px;
            color: #666;
            font-size: 12px;
        }
    </style>
</head>
<body>
    <div class="title-row">
        <div class="pacman-icon"><img src="images/pacman.png" alt="Pacman"></div>
        <h1>PACMAN</h1>
    </div>
    <p style="color: #888; margin-bottom: 30px;">Multiplayer Edition</p>
    
    <div class="menu">
        <form action="${pageContext.request.contextPath}/game/create" method="get" class="layout-select">
            <label for="layout" style="color: #aaa;">Select Map:</label>
            <select name="layout" id="layout">
                <option value="mediumClassic.lay">Medium Classic</option>
                <option value="smallClassic.lay">Small Classic</option>
                <option value="originalClassic.lay">Original Classic</option>
                <option value="mediumClassic_twoPacmans.lay">Medium - 2 Players</option>
                <option value="mediumClassic_fivePacmans.lay">Medium - 5 Players</option>
                <option value="originalClassic_twoPacmans.lay">Original - 2 Players</option>
            </select>
            <button type="submit" class="menu-btn new-game">CREATE NEW GAME</button>
        </form>
        
        <div style="color: #666; margin: 20px 0;">- OR -</div>
        
        <form action="${pageContext.request.contextPath}/game/join" method="get" class="join-form">
            <label for="gameId" style="color: #aaa;">Join Existing Game:</label>
            <input type="text" name="gameId" id="gameId" placeholder="Enter Game ID" required>
            <button type="submit" class="menu-btn">JOIN GAME</button>
        </form>
    </div>
    
    <div class="footer">
        <p>Use arrow keys or WASD to move</p>
        <p>Press SPACE to start, P to pause</p>
    </div>
</body>
</html>
