package com.ecommerce.jsf.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/logout-callback")
public class LogoutCallbackServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Clear JWT cookie
        Cookie jwtCookie = new Cookie("JWT", "");
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0); // Expire immediately
        resp.addCookie(jwtCookie);
        // Redirect to index.html
        resp.sendRedirect(req.getContextPath() + "/index.html");
    }
}
