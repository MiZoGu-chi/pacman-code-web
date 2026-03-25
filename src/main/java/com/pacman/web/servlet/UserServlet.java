package com.pacman.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pacman.dao.DAOFactory;
import com.pacman.dao.UserDao;
import com.pacman.web.entity.User;

@WebServlet("/api/user")
public class UserServlet extends HttpServlet {
    private UserDao userDao;

    @Override
    public void init() throws ServletException {
        DAOFactory daoFactory = DAOFactory.getDAOFactory(DAOFactory.MYSQL);
        this.userDao = daoFactory.getUserDao();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String email = request.getParameter("email");
        PrintWriter out = response.getWriter();

        try {
            User user = userDao.findUser(email);

            if (user != null) {
                String json = String.format(
                    "{\"username\": \"%s\", \"color\": \"%s\", \"victories\": %d, \"rank\": \"%s\"}",
                    user.getUsername(),
                    user.getColor(),
                    user.getVictories(),
                    user.getRank().toString()
                );
                out.print(json);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\": \"Utilisateur non trouvé\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Erreur serveur\"}");
        }
        out.flush();
    }
}