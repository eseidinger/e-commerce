package de.eseidinger.ecommerce.auth.jsf;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.eseidinger.ecommerce.auth.OpenIdConfigBean;
import de.eseidinger.ecommerce.auth.utils.JwtUtils;
import de.eseidinger.ecommerce.auth.utils.JwtUtils.TokenInfo;

@ApplicationScoped
public class JwtCookieHandler {

  private static final Logger logger = LoggerFactory.getLogger(JwtCookieHandler.class);

  @Inject OpenIdConfigBean openIdConfigBean;

  public static class TokenPair {
    private final String idToken;
    private final String refreshToken;

    public TokenPair(String idToken, String refreshToken) {
      this.idToken = idToken;
      this.refreshToken = refreshToken;
    }

    public String getIdToken() {
      return idToken;
    }

    public String getRefreshToken() {
      return refreshToken;
    }
  }

  private String getCookie(String name, Cookie[] cookies) {
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (name.equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }

  private TokenPair refreshTokenPair(String refreshToken, HttpServletResponse response) {
    String tokenEndpoint = openIdConfigBean.getTokenUrl();
    String clientId = openIdConfigBean.getClientId();
    Client client = ClientBuilder.newClient();
    Form form = new Form();
    form.param("grant_type", "refresh_token");
    form.param("refresh_token", refreshToken);
    form.param("client_id", clientId);
    Response kcResp =
        client
            .target(tokenEndpoint)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
    String json = kcResp.readEntity(String.class);
    kcResp.close();
    client.close();
    JsonObject obj;
    try (JsonReader reader = Json.createReader(new StringReader(json))) {
      obj = reader.readObject();
    }
    if (obj.containsKey("id_token")) {
      String newJwt = obj.getString("id_token");
      String newRefresh =
          obj.containsKey("refresh_token") ? obj.getString("refresh_token") : refreshToken;
      return new TokenPair(newJwt, newRefresh);
    }
    return null;
  }

  private String handleTokenRefresh(
      String refreshToken, HttpServletResponse response, ServletRequest request) {
    if (isRefreshTokenExpired(refreshToken)) {
      logger.info("Refresh token expired");
      return null;
    }
    TokenPair tokenPair = refreshTokenPair(refreshToken, (HttpServletResponse) response);
    if (tokenPair == null) {
      logger.warn("Failed to refresh JWT");
      return null;
    }
    Cookie newJwtCookie = new Cookie("JWT", tokenPair.getIdToken());
    newJwtCookie.setPath("/");
    newJwtCookie.setHttpOnly(true);
    response.addCookie(newJwtCookie);
    Cookie newRefreshCookie = new Cookie("JWT_REFRESH", tokenPair.getRefreshToken());
    newRefreshCookie.setPath("/");
    newRefreshCookie.setHttpOnly(true);
    response.addCookie(newRefreshCookie);
    return tokenPair.getIdToken();
  }

  private TokenInfo extractIdTokenInfo(String jwt) throws Exception {
    Map<String, Object> header = JwtUtils.decodeJwtHeader(jwt);
    return JwtUtils.verifyAndExtract(
        jwt, openIdConfigBean.getJwksUrl(), header.get("kid").toString());
  }

  private Claims verifyIdToken(String jwt) throws Exception {
    Map<String, Object> header = JwtUtils.decodeJwtHeader(jwt);
    return JwtUtils.verify(jwt, openIdConfigBean.getJwksUrl(), header.get("kid").toString());
  }

  private boolean isRefreshTokenExpired(String jwt) {
    Map<String, Object> payload;
    try {
      payload = JwtUtils.decodeJwtPayload(jwt);
    } catch (Exception e) {
      logger.warn("Failed to decode JWT payload: {}", e.getMessage());
      return false;
    }
    if (payload.containsKey("exp")) {
      Long exp = ((Number) payload.get("exp")).longValue();
      Long now = System.currentTimeMillis() / 1000;
      return now >= exp;
    }
    return false;
  }

  public AuthenticationStatus handleJwtCookie(
      HttpServletRequest request, HttpServletResponse response, HttpMessageContext context)
      throws IOException {
    HttpServletRequest req = (HttpServletRequest) request;
    String jwt = getCookie("JWT", req.getCookies());
    String refreshToken = getCookie("JWT_REFRESH", req.getCookies());

    // No token → assign guest role
    String principal = "guest";
    CredentialValidationResult result = new CredentialValidationResult(principal, Set.of("guest"));

    if (jwt == null) {
      logger.info("No JWT cookie found");
      context.notifyContainerAboutLogin(result);
      return AuthenticationStatus.SUCCESS;
    }
    try {
      verifyIdToken(jwt);
    } catch (ExpiredJwtException e) {
      logger.info("JWT expired");
      if (refreshToken != null) {
        jwt = handleTokenRefresh(refreshToken, (HttpServletResponse) response, request);
        if (jwt == null) {
          logger.warn("Failed to refresh JWT");
          ((HttpServletResponse) response).sendRedirect(req.getContextPath() + "/login");
          return AuthenticationStatus.SEND_CONTINUE;
        }
      } else {
        logger.info("Token expired and no refresh token available");
        ((HttpServletResponse) response).sendRedirect(req.getContextPath() + "/login");
        return AuthenticationStatus.SEND_CONTINUE;
      }
    } catch (Exception e) {
      logger.warn("Failed to decode JWT payload: {}", e.getMessage());
      return AuthenticationStatus.NOT_DONE;
    }

    try {
      TokenInfo tokenInfo = extractIdTokenInfo(jwt);
      result =
          new CredentialValidationResult(
              tokenInfo.getUsername(),
              tokenInfo.getRoles() != null ? Set.copyOf(tokenInfo.getRoles()) : Set.of());
      context.notifyContainerAboutLogin(result);
      logger.info(
          "JWT verified for user: {} with roles: {}",
          tokenInfo.getUsername(),
          result.getCallerGroups());
      return AuthenticationStatus.SUCCESS;
    } catch (Exception e) {
      logger.warn("Failed to verify JWT: {}", e.getMessage());
      return AuthenticationStatus.NOT_DONE;
    }
  }
}
