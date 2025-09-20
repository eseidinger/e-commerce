package com.ecommerce.jsf.auth;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

@Named
@ApplicationScoped
public class OpenIdConfigBean {

    public String getClientId() {
        return System.getenv().getOrDefault("AUTH_CLIENT_ID", "e-commerce");
    }

    public String getAuthHost() {
        return System.getenv().getOrDefault("AUTH_HOST", "http://localhost:8084");
    }

    public String getAuthRealm() {
        return System.getenv().getOrDefault("AUTH_REALM", "e-commerce-dev");
    }

    public String getProviderUri() {
        return getAuthHost() + "/realms/" + getAuthRealm();
    }

    public String getLogoutUrl() {
        return getProviderUri() + "/protocol/openid-connect/logout";
    }

    public String getProxyHost() {
        return System.getenv().getOrDefault("PROXY_HOST", "");
    }

    public String getProxyPort() {
        return System.getenv().getOrDefault("PROXY_PORT", "");
    }

    public String getBaseUrl() {
        return System.getenv().getOrDefault("BASE_URL", "http://localhost:8080");
    }

    public String getRedirectUri() {
        return getBaseUrl() + "/callback";
    }

    public String getAuthorizationUrl() {
        return getProviderUri() + "/protocol/openid-connect/auth";
    }

    public String getTokenUrl() {
        return getProviderUri() + "/protocol/openid-connect/token";
    }
}
