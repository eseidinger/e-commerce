package com.ecommerce.jsf.servlet;

import java.io.IOException;
import java.net.URLEncoder;

import com.ecommerce.jsf.bean.OpenIdConfigBean;

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

    @Inject
    OpenIdConfigBean openIdConfigBean;


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Invalidate local session
        req.getSession().invalidate();

        // Build OIDC logout URL
        String endSessionEndpoint = openIdConfigBean.getLogoutUrl();
        IdentityToken idToken = context.getIdentityToken(); // Optional but recommended
        String postLogoutRedirectUri = openIdConfigBean.getBaseUrl() + "/instance-info.xhtml";

        String logoutUrl = endSessionEndpoint +
            "?id_token_hint=" + URLEncoder.encode(idToken.getToken(), "UTF-8") +
            "&post_logout_redirect_uri=" + URLEncoder.encode(postLogoutRedirectUri, "UTF-8");

        // Redirect to IdP logout
        resp.sendRedirect(logoutUrl);
    }
}