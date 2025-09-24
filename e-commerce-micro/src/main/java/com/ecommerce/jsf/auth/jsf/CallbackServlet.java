package com.ecommerce.jsf.auth.jsf;

import java.io.IOException;
import java.io.StringReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ecommerce.jsf.auth.OpenIdConfigBean;

import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@WebServlet("/callback")
public class CallbackServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(CallbackServlet.class);

    @Inject
    private OpenIdConfigBean openIdConfigBean;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String code = req.getParameter("code");
        if (code == null || code.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing authorization code");
            return;
        }

        // Exchange code for JWT access token using Jakarta Client
        Client client = ClientBuilder.newClient();
        Form form = new Form();
        form.param("grant_type", "authorization_code");
        form.param("code", code);
        form.param("client_id", openIdConfigBean.getClientId());
        // form.param("client_secret", clientSecret);
        form.param("redirect_uri", openIdConfigBean.getLoginRedirectUri());

        Response tokenResponse = client.target(openIdConfigBean.getTokenUrl())
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

        String tokenJson = tokenResponse.readEntity(String.class);
        tokenResponse.close();
        client.close();

        // Parse JSON response
        try (JsonReader reader = Json.createReader(new StringReader(tokenJson))) {
            JsonObject json = reader.readObject();
            String idToken = json.getString("id_token");
            String refreshToken = json.containsKey("refresh_token") ? json.getString("refresh_token") : null;
            // Store JWT in secure, HttpOnly cookie
            Cookie jwtCookie = new Cookie("JWT", idToken);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            resp.addCookie(jwtCookie);
            if (refreshToken != null) {
                Cookie refreshCookie = new Cookie("JWT_REFRESH", refreshToken);
                refreshCookie.setHttpOnly(true);
                refreshCookie.setPath("/");
                resp.addCookie(refreshCookie);
            }
        }
        resp.sendRedirect(req.getContextPath() + "/index.html");
    }
}
