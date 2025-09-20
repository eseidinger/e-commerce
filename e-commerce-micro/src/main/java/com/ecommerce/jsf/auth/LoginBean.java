package com.ecommerce.jsf.auth;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;

import jakarta.inject.Inject;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/auth")
public class LoginBean {

    @Inject
    private OpenIdConfigBean openIdConfigBean;

    @Path("/login")
    @GET
    public Response startOidcLogin() throws IOException {
        String authorizationEndpoint = openIdConfigBean.getAuthorizationUrl();
        String clientId = openIdConfigBean.getClientId();
        String redirectUri = openIdConfigBean.getLoginRedirectUri();
        String state = java.util.UUID.randomUUID().toString();
        String url = authorizationEndpoint +
                "?response_type=code" +
                "&client_id=" + URLEncoder.encode(clientId, "UTF-8") +
                "&redirect_uri=" + URLEncoder.encode(redirectUri, "UTF-8") +
                "&scope=openid" +
                "&state=" + URLEncoder.encode(state, "UTF-8");
        return Response.seeOther(URI.create(url)).build();
    }

    @Path("/logout")
    @GET
    public Response logout(@CookieParam("JWT") String idTokenHint) throws IOException {
        String redirectUri = openIdConfigBean.getLogoutRedirectUri();
        String logoutUrl = openIdConfigBean.getLogoutUrl() + "?post_logout_redirect_uri="
                + URLEncoder.encode(redirectUri, "UTF-8") + "&id_token_hint="
                + URLEncoder.encode(idTokenHint, "UTF-8");
        return Response.seeOther(URI.create(logoutUrl)).build();
    }
}
