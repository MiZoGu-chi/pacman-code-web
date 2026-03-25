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
        
        <h3>Personnaliser mon Pacman</h3>
        <p>Debug Rank : ${sessionScope.currentUser.getRank().toString()}</p>
        <img src="images/ranks/${sessionScope.currentUser.rank.toString()}.svg" alt="Rank icon" width="80" />
		<p>Niveau : ${sessionScope.currentUser.rank}</p>
		<form action="updateColor" method="post">
		    <label for="pacmanColor">Couleur de votre personnage :</label>
		    <select name="pacmanColor" id="pacmanColor">
		        <option value="yellow" ${sessionScope.currentUser.color == 'yellow' ? 'selected' : ''}>Jaune (Classique)</option>
		        <option value="red" ${sessionScope.currentUser.color == 'red' ? 'selected' : ''}>Rouge</option>
		        <option value="blue" ${sessionScope.currentUser.color == 'blue' ? 'selected' : ''}>Bleu</option>
		        <option value="pink" ${sessionScope.currentUser.color == 'pink' ? 'selected' : ''}>Rose</option>
		    </select>
		    <input type="submit" value="Enregistrer mon choix" />
		</form>
        
        <br/>
        <p><a href="logout">Se déconnecter</a></p>
    </div>
</body>
</html>