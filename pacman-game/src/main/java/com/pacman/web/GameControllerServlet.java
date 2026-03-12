package com.pacman.web;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/game/*"})
public class GameControllerServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(GameControllerServlet.class.getName());
    private String layoutsPath;
    
    @Override
    public void init() throws ServletException {
        super.init();
        layoutsPath = getServletContext().getRealPath("/layouts");
        LOGGER.info("GameControllerServlet initialized. Layouts path: " + layoutsPath);
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                showLobby(request, response);
            } else if (pathInfo.equals("/new")) {
                showNewGameForm(request, response);
            } else if (pathInfo.equals("/create")) {
                createGame(request, response);
            } else if (pathInfo.equals("/join")) {
                joinGame(request, response);
            } else if (pathInfo.equals("/play")) {
                playGame(request, response);
            } else if (pathInfo.equals("/state")) {
                getGameState(request, response);
            } else if (pathInfo.equals("/scores")) {
                showScores(request, response);
            } else if (pathInfo.equals("/layouts")) {
                listLayouts(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error", e);
            request.setAttribute("error", e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo != null && pathInfo.equals("/move")) {
                handleMove(request, response);
            } else if (pathInfo != null && pathInfo.equals("/score")) {
                saveScore(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in GameController POST", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    private void showLobby(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        GameManager manager = GameManager.getInstance();
        List<String> activeGames = manager.getActiveGameIds();
        
        request.setAttribute("activeGames", activeGames);
        request.setAttribute("layouts", getAvailableLayouts());
        request.setAttribute("topScores", ScoreManager.getInstance().getTopScores(10));
        
        request.getRequestDispatcher("/WEB-INF/views/lobby.jsp").forward(request, response);
    }

    private void showNewGameForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setAttribute("layouts", getAvailableLayouts());
        request.getRequestDispatcher("/WEB-INF/views/newgame.jsp").forward(request, response);
    }

    private void createGame(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        String layout = request.getParameter("layout");
        if (layout == null || layout.isEmpty()) {
            layout = "mediumClassic.lay";
        }
        
        String layoutPath = layoutsPath + "/" + layout;
        String gameId = UUID.randomUUID().toString().substring(0, 8);
        
        GameManager.getInstance().createGame(gameId, layoutPath);
        
        LOGGER.info("Created new game: " + gameId + " with layout: " + layout);
        
        // Redirect to play page
        response.sendRedirect(request.getContextPath() + "/game/play?gameId=" + gameId);
    }
    private void joinGame(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        String gameId = request.getParameter("gameId");
        if (gameId == null || gameId.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/game");
            return;
        }
        
        GameManager.GameSession session = GameManager.getInstance().getGame(gameId);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/game?error=notfound");
            return;
        }
        
        response.sendRedirect(request.getContextPath() + "/game/play?gameId=" + gameId);
    }
    private void playGame(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String gameId = request.getParameter("gameId");
        
        if (gameId == null || gameId.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/game");
            return;
        }
        
        // Generate a UNIQUE player ID for EACH browser tab/window
        // Always generate a new playerId - the JS will handle reconnection if needed
        String playerId = UUID.randomUUID().toString().substring(0, 8);
        
        GameManager.GameSession gameSession = GameManager.getInstance().getGame(gameId);
        
        if (gameSession == null) {
            // Game doesn't exist yet, create with default layout
            String layoutPath = layoutsPath + "/mediumClassic.lay";
            gameSession = GameManager.getInstance().createGame(gameId, layoutPath);
        }
        
        request.setAttribute("gameId", gameId);
        request.setAttribute("playerId", playerId);
        request.setAttribute("wsUrl", getWebSocketUrl(request, gameId, playerId));
        
        request.getRequestDispatcher("/game.jsp").forward(request, response);
    }
    private void getGameState(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        String gameId = request.getParameter("gameId");
        
        GameManager.GameSession session = GameManager.getInstance().getGame(gameId);
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        
        if (session == null) {
            out.print("{\"error\":\"Game not found\"}");
        } else {
            out.print(session.getStateAsJson());
        }
        
        out.flush();
    }
    private void handleMove(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        String gameId = request.getParameter("gameId");
        String playerId = request.getParameter("playerId");
        String direction = request.getParameter("direction");
        
        GameManager.GameSession session = GameManager.getInstance().getGame(gameId);
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        
        if (session == null) {
            out.print("{\"error\":\"Game not found\"}");
        } else {
            session.handlePlayerMove(playerId, direction);
            out.print(session.getStateAsJson());
        }
        
        out.flush();
    }
    private void saveScore(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        String playerName = request.getParameter("playerName");
        String scoreStr = request.getParameter("score");
        String gameId = request.getParameter("gameId");
        
        if (playerName != null && scoreStr != null) {
            try {
                int score = Integer.parseInt(scoreStr);
                Score scoreObj = new Score(playerName, score, gameId);
                ScoreManager.getInstance().saveScore(scoreObj);
                
                response.setContentType("application/json");
                response.getWriter().print("{\"success\":true}");
            } catch (NumberFormatException e) {
                response.setContentType("application/json");
                response.getWriter().print("{\"error\":\"Invalid score\"}");
            }
        } else {
            response.setContentType("application/json");
            response.getWriter().print("{\"error\":\"Missing parameters\"}");
        }
    }
    private void showScores(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setAttribute("topScores", ScoreManager.getInstance().getTopScores(20));
        request.getRequestDispatcher("/WEB-INF/views/scores.jsp").forward(request, response);
    }

    private void listLayouts(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        List<String> layouts = getAvailableLayouts();
        
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < layouts.size(); i++) {
            if (i > 0) json.append(",");
            json.append("\"").append(layouts.get(i)).append("\"");
        }
        json.append("]");
        
        out.print(json.toString());
        out.flush();
    }
    private List<String> getAvailableLayouts() {
        List<String> layouts = new ArrayList<>();
        
        if (layoutsPath != null) {
            File layoutDir = new File(layoutsPath);
            if (layoutDir.exists() && layoutDir.isDirectory()) {
                File[] files = layoutDir.listFiles((dir, name) -> name.endsWith(".lay"));
                if (files != null) {
                    for (File file : files) {
                        layouts.add(file.getName());
                    }
                }
            }
        }
        
        if (layouts.isEmpty()) {
            layouts.add("mediumClassic.lay");
            layouts.add("smallClassic.lay");
            layouts.add("originalClassic.lay");
        }
        
        return layouts;
    }
    private String getWebSocketUrl(HttpServletRequest request, String gameId, String playerId) {
        String scheme = request.isSecure() ? "wss" : "ws";
        String host = request.getServerName();
        int port = request.getServerPort();
        String contextPath = request.getContextPath();
        
        String url = scheme + "://" + host;
        if ((scheme.equals("ws") && port != 80) || (scheme.equals("wss") && port != 443)) {
            url += ":" + port;
        }
        url += contextPath + "/ws/game/" + gameId + "/" + playerId;
        
        return url;
    }

}
