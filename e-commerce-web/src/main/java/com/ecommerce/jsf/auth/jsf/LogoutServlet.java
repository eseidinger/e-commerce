package com.ecommerce.jsf.auth.jsf;

import java.io.IOException;
import java.net.URLEncoder;

import com.ecommerce.jsf.auth.OpenIdConfigBean;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Inject
    private OpenIdConfigBean openIdConfigBean;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idTokenHint = null;
        if (req.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : req.getCookies()) {
                if ("JWT".equals(cookie.getName())) {
                    idTokenHint = cookie.getValue();
                    break;
                }
            }
        }
        String redirectUri = openIdConfigBean.getLogoutRedirectUri();
        String logoutUrl = openIdConfigBean.getLogoutUrl() + "?post_logout_redirect_uri="
                + URLEncoder.encode(redirectUri, "UTF-8") + "&id_token_hint="
                + URLEncoder.encode(idTokenHint != null ? idTokenHint : "", "UTF-8");
        resp.sendRedirect(logoutUrl);
    }
}
