<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Pacman Multiplayer</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
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
