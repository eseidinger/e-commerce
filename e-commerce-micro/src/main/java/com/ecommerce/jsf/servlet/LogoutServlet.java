package com.ecommerce.jsf.servlet;

import java.io.IOException;
import java.net.URLEncoder;

import fish.payara.security.openid.api.IdentityToken;
import fish.payara.security.openid.api.OpenIdContext;
import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Inject
    OpenIdContext context;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Invalidate local session
        req.getSession().invalidate();

        // Build OIDC logout URL
        String endSessionEndpoint = "http://localhost:8084/realms/e-commerce-dev/protocol/openid-connect/logout";
        IdentityToken idToken = context.getIdentityToken(); // Optional but recommended
        String postLogoutRedirectUri = "http://localhost:8080/instance-info.xhtml";

        String logoutUrl = endSessionEndpoint +
            "?id_token_hint=" + URLEncoder.encode(idToken.getToken(), "UTF-8") +
            "&post_logout_redirect_uri=" + URLEncoder.encode(postLogoutRedirectUri, "UTF-8");

        // Redirect to IdP logout
        resp.sendRedirect(logoutUrl);
    }
}