<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <title>Pacman - Dashboard</title>
    <link type="text/css" rel="stylesheet" href="css/style.css" />
</head>
<body>
    <div class="container">
        <h1>Bienvenue, ${sessionScope.currentUser.username} !</h1>
        
        <h2>Historique Global des Scores</h2>
        <table border="1">
            <thead>
                <tr>
                    <th>Joueur</th>
                    <th>Score</th>
                    <th>Date</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="s" items="${scores}">
                    <tr>
                        <td>${s.playerName}</td>
                        <td>${s.score}</td>
                        <td>${s.gameDate}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        
        <br/>
        <p><a href="login.jsp">Se déconnecter</a></p>
    </div>
</body>
</html>