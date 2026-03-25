package com.pacman.web.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.pacman.dao.DAOFactory;
import com.pacman.web.entity.User;

@WebServlet("/updateColor")
public class UpdatePacmanColorServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");

        String color = request.getParameter("pacmanColor");

        if (currentUser != null && color != null) {
            try {
                DAOFactory daoFactory = DAOFactory.getDAOFactory(DAOFactory.MYSQL);
                
                daoFactory.getUserDao().updateColor(currentUser.getId(), color);

                currentUser.setColor(color);
                
            } catch (Exception e) {
                throw new ServletException("Erreur lors de la mise à jour de la couleur", e);
            }
        }
        
        response.sendRedirect(request.getContextPath() + "/home");
    }
}
