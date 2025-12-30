package com.ecommerce.jsf.bean;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

@Named
@ApplicationScoped
public class OpenIdConfigBean {

    public String getClientId() {
        return System.getenv().getOrDefault("AUTH_CLIENT_ID", "e-commerce");
    }

    public String getAuthHost() {
        return System.getenv().getOrDefault("AUTH_HOST", "https://keycloak.eseidinger.de");
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

    public String getBaseUrl() {
        return System.getenv().getOrDefault("BASE_URL", "http://localhost:8080");
    }

    public String getRedirectUri() {
        return getBaseUrl() + "/callback";
    }
}
