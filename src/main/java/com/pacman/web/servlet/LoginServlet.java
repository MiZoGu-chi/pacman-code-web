package com.pacman.web.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.pacman.dao.DAOFactory;
import com.pacman.dao.UserDao;
import com.pacman.web.entity.User;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private UserDao userDao;

    public void init() {
    	DAOFactory daoFactory = DAOFactory.getDAOFactory(DAOFactory.MYSQL);
        userDao = daoFactory.getUserDao();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        getServletContext().getRequestDispatcher("/login.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        User user = userDao.findUser(email);

        if (user != null && user.getPassword().equals(password)) {
            HttpSession session = request.getSession();
            session.setAttribute("currentUser", user);
            
            response.sendRedirect(request.getContextPath() + "/home"); 
        } else {
            request.setAttribute("error", "Invalid email or password.");
            this.getServletContext().getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}