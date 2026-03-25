package com.pacman.web.servlet;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pacman.dao.DAOFactory;
import com.pacman.web.entity.Score;
import com.pacman.web.service.ScoreManager;

@WebServlet("/getScores")
public class ScoreServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ScoreManager scoreManager;

    @Override
    public void init() {
        DAOFactory daoFactory = DAOFactory.getDAOFactory(DAOFactory.MYSQL);
        scoreManager = new ScoreManager(daoFactory.getScoreDao());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        List<Score> scores = scoreManager.getTopScores(10); 
        
        // Construction du JSON
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < scores.size(); i++) {
            Score s = scores.get(i);
            json.append(String.format("{\"playerName\":\"%s\",\"score\":%d}", 
                s.getPlayerName(), 
                s.getScore()));
            
            if (i < scores.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json.toString());
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String playerName = request.getParameter("playerName");
        String scoreStr = request.getParameter("score");

        if (playerName != null && scoreStr != null) {
            try {
                int scoreValue = Integer.parseInt(scoreStr);
                
                Score newScore = new Score();
                newScore.setPlayerName(playerName);
                newScore.setScore(scoreValue);
                
                scoreManager.saveScore(newScore); 

                System.out.println("Score enregistré avec succès pour : " + playerName);
                response.setStatus(HttpServletResponse.SC_OK); // 200 OK
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Format de score invalide");
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Paramètres manquants");
        }
    }
}