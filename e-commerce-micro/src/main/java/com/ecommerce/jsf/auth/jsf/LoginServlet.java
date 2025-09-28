package com.ecommerce.jsf.auth.jsf;

import com.ecommerce.jsf.auth.OpenIdConfigBean;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

  @Inject private OpenIdConfigBean openIdConfigBean;

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String authorizationEndpoint = openIdConfigBean.getAuthorizationUrl();
    String clientId = openIdConfigBean.getClientId();
    String redirectUri = openIdConfigBean.getLoginRedirectUri();
    String state = java.util.UUID.randomUUID().toString();
    String url =
        authorizationEndpoint
            + "?response_type=code"
            + "&client_id="
            + URLEncoder.encode(clientId, "UTF-8")
            + "&redirect_uri="
            + URLEncoder.encode(redirectUri, "UTF-8")
            + "&scope=openid"
            + "&state="
            + URLEncoder.encode(state, "UTF-8");
    resp.sendRedirect(url);
  }
}
