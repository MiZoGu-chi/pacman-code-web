package com.pacman.web.servlet;

import java.io.IOException;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.pacman.web.service.GameManager;
import com.pacman.web.entity.User;

@WebServlet("/game")
public class GameControllerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. VERIFICATION MVC : L'utilisateur est-il connecté ?
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            // Pas de login -> Redirection vers la page de connexion
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // 2. LOGIQUE : On récupère ou on crée une partie
        String gameId = request.getParameter("gameId");
        if (gameId == null || gameId.isEmpty()) {
            gameId = "game-" + UUID.randomUUID().toString().substring(0, 8);
        }

        // On s'assure que la partie existe dans le Manager
        GameManager gm = GameManager.getInstance();
        if (gm.getGame(gameId) == null) {
        	String layoutPath = getServletContext().getRealPath("/layouts/mediumClassic.lay");
        	gm.createGame(gameId, layoutPath);
        }

        // 3. VUE : On prépare les données pour game.jsp
        request.setAttribute("gameId", gameId);
        request.setAttribute("playerId", currentUser.getUsername()); // Utilise le pseudo de la BDD
        
        // Construction de l'URL WebSocket dynamique
     // --- VERSION CORRIGÉE ---
        String scheme = request.getScheme().equals("https") ? "wss" : "ws";

        // On ajoute "/ws" et on ajoute le playerId à la fin pour correspondre à l'annotation @ServerEndpoint
        String wsUrl = scheme + "://" + request.getServerName() + ":" + request.getServerPort() 
                       + request.getContextPath() + "/ws/game/" + gameId + "/" + currentUser.getUsername();

        request.setAttribute("wsUrl", wsUrl);
        // Envoi vers la page game.jsp
        this.getServletContext().getRequestDispatcher("/game.jsp").forward(request, response);
    }
}