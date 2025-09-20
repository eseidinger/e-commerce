package com.ecommerce.jsf.bean;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.faces.context.FacesContext;
import java.io.IOException;

import com.ecommerce.jsf.auth.OpenIdConfigBean;

import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.ServletException;

@Named
@RequestScoped
public class LoginBean {

    @Inject
    private OpenIdConfigBean openIdConfigBean;

    public String startOidcLogin() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        String authorizationEndpoint = openIdConfigBean.getAuthorizationUrl();
        String clientId = openIdConfigBean.getClientId();
        String redirectUri = openIdConfigBean.getRedirectUri();
        String state = java.util.UUID.randomUUID().toString();
        String url = authorizationEndpoint +
                "?response_type=code" +
                "&client_id=" + java.net.URLEncoder.encode(clientId, "UTF-8") +
                "&redirect_uri=" + java.net.URLEncoder.encode(redirectUri, "UTF-8") +
                "&scope=openid" +
                "&state=" + java.net.URLEncoder.encode(state, "UTF-8");
        context.getExternalContext().redirect(url);
        return null;
    }

    public String logout() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        try {
            request.logout();
        } catch (ServletException e) {
            context.addMessage(null, new jakarta.faces.application.FacesMessage(
                    jakarta.faces.application.FacesMessage.SEVERITY_ERROR, "Logout failed", null));
        }
        // Optionally, redirect to Keycloak logout endpoint
        String redirectUri = openIdConfigBean.getBaseUrl() + "/index.xhtml";
        String logoutUrl = openIdConfigBean.getLogoutUrl() + "?redirect_uri="
                + java.net.URLEncoder.encode(redirectUri, "UTF-8");
        context.getExternalContext().redirect(logoutUrl);
        return null;
    }
}
