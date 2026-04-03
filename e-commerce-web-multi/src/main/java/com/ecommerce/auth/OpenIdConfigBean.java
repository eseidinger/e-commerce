package com.ecommerce.auth;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

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

  public String getBaseUrl() {
    return System.getenv().getOrDefault("BASE_URL", "http://localhost:8088");
  }

  public String getProviderUri() {
    return getAuthHost() + "/realms/" + getAuthRealm();
  }

  public String getLogoutUrl() {
    return getProviderUri() + "/protocol/openid-connect/logout";
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
