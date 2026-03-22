package com.pacman.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pacman.dao.DAOFactory;
import com.pacman.web.entity.User;

@WebServlet("/inscription")
public class RegistrationServlet extends HttpServlet {
	
    private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = new User();
        user.setUsername(request.getParameter("nom"));
        user.setEmail(request.getParameter("email"));
        user.setPassword(request.getParameter("motdepasse"));
        
        DAOFactory daoFactory = DAOFactory.getDAOFactory(DAOFactory.MYSQL);
        daoFactory.getUserDao().saveUser(user);
        
        response.sendRedirect("login.jsp");
    }
}
