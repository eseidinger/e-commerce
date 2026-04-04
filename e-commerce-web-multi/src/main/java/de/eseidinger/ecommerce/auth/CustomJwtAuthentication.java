package de.eseidinger.ecommerce.auth;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.AuthenticationException;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.eseidinger.ecommerce.auth.jsf.JwtCookieHandler;
import de.eseidinger.ecommerce.auth.jwt.JwtHeaderHandler;

@ApplicationScoped
public class CustomJwtAuthentication implements HttpAuthenticationMechanism {

  private static final Logger logger = LoggerFactory.getLogger(CustomJwtAuthentication.class);

  @Inject JwtCookieHandler jwtCookieHandler;

  @Inject JwtHeaderHandler jwtHeaderHandler;

  @Override
  public AuthenticationStatus validateRequest(
      HttpServletRequest request, HttpServletResponse response, HttpMessageContext context)
      throws AuthenticationException {
    logger.info("CustomJwtAuthentication triggered");
    String path = request.getRequestURI();

    if (path.startsWith("/api")) {
      return jwtHeaderHandler.handleJwtHandler(request, response, context);
    } else if (path.startsWith("/jsf")) {
      // Public paths, allow access
      try {
        return jwtCookieHandler.handleJwtCookie(request, response, context);
      } catch (IOException e) {
        logger.error("IOException in handleJsfPathRequest: {}", e.getMessage());
        return AuthenticationStatus.NOT_DONE;
      }
    }
    return AuthenticationStatus.NOT_DONE;
  }
}
