package de.eseidinger.ecommerce.auth;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class OpenIdConfigBean {

  @Inject
  @ConfigProperty(name = "auth.client.id", defaultValue = "e-commerce")
  private String clientId;

  @Inject
  @ConfigProperty(name = "auth.host", defaultValue = "http://localhost:8084")
  private String authHost;

  @Inject
  @ConfigProperty(name = "auth.host.internal", defaultValue = "http://localhost:8084")
  private String authHostInternal;

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

  public String getProviderUriInternal() {
    return authHostInternal + "/realms/" + authRealm;
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
    return getProviderUriInternal() + "/protocol/openid-connect/token";
  }

  public String getJwksUrl() {
    return getProviderUriInternal() + "/protocol/openid-connect/certs";
  }
}
