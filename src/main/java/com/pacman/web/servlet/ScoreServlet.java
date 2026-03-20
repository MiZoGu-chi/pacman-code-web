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
        DAOFactory daoFactory = DAOFactory.getInstance();
        this.scoreManager = new ScoreManager(daoFactory.getScoreDao());
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
}