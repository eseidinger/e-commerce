package com.ecommerce.jsf.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.Date;

@WebServlet("/session-info")
public class SessionInfoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(true);
        Integer count = (Integer) session.getAttribute("sessionRequestCount");
        if (count == null) count = 0;
        session.setAttribute("sessionRequestCount", count + 1);

        String hostname;
        String ipAddress;
        try {
            hostname = InetAddress.getLocalHost().getHostName();
            ipAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            hostname = "Unknown";
            ipAddress = "Unknown";
        }

        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        out.write("{\n");
        out.write("  \"hostname\": \"" + hostname + "\",\n");
        out.write("  \"ipAddress\": \"" + ipAddress + "\",\n");
        out.write("  \"sessionId\": \"" + session.getId() + "\",\n");
        out.write("  \"sessionCreationTime\": \"" + new Date(session.getCreationTime()).toString() + "\",\n");
        out.write("  \"sessionRequestCount\": " + (count + 1) + "\n");
        out.write("}\n");
        out.flush();
    }
}
