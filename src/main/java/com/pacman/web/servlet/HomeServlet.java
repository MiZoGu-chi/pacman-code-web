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

@WebServlet("/home")
public class HomeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DAOFactory daoFactory = DAOFactory.getDAOFactory(DAOFactory.MYSQL);
        
        List<Score> scores = daoFactory.getScoreDao().getTopScores(10); 
        
        request.setAttribute("scores", scores);
        this.getServletContext().getRequestDispatcher("/home.jsp").forward(request, response);
    }
}