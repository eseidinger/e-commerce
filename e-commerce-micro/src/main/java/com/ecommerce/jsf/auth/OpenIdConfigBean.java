package com.ecommerce.jsf.auth;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named
@ApplicationScoped
public class OpenIdConfigBean {

    @Inject
    @ConfigProperty(name = "auth.client.id", defaultValue = "e-commerce")
    private String clientId;

    @Inject
    @ConfigProperty(name = "auth.host", defaultValue = "https://keycloak.eseidinger.de")
    private String authHost;

    @Inject
    @ConfigProperty(name = "auth.realm", defaultValue = "e-commerce-dev")
    private String authRealm;

    @Inject
    @ConfigProperty(name = "base.url", defaultValue = "http://localhost:8080")
    private String baseUrl;

    public String getClientId() {
        return clientId;
    }

    public String getAuthHost() {
        return authHost;
    }

    public String getAuthRealm() {
        return authRealm;
    }

    public String getProviderUri() {
        return authHost + "/realms/" + authRealm;
    }

    public String getLogoutUrl() {
        return getProviderUri() + "/protocol/openid-connect/logout";
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getLoginRedirectUri() {
        return getBaseUrl() + "/callback";
    }

    public String getLogoutRedirectUri() {
        return getBaseUrl() + "/logout-callback";
    }

    public String getAuthorizationUrl() {
        return getProviderUri() + "/protocol/openid-connect/auth";
    }

    public String getTokenUrl() {
        return getProviderUri() + "/protocol/openid-connect/token";
    }

    public String getJwksUrl() {
        return getProviderUri() + "/protocol/openid-connect/certs";
    }
}
