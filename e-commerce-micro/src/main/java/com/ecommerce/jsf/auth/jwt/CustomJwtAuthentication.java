package com.ecommerce.jsf.auth.jwt;

import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.ecommerce.jsf.auth.OpenIdConfigBean;
import com.ecommerce.jsf.auth.utils.JwtUtils;
import com.ecommerce.jsf.auth.utils.JwtUtils.TokenInfo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.AuthenticationException;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ApplicationScoped
public class CustomJwtAuthentication implements HttpAuthenticationMechanism {

    private static final Logger logger = Logger.getLogger(CustomJwtAuthentication.class.getName());

    @Inject
    OpenIdConfigBean openIdConfigBean;

    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest request,
            HttpServletResponse response,
            HttpMessageContext context) throws AuthenticationException {
        logger.info("CustomJwtAuthentication triggered");
        String path = request.getRequestURI();

        // 🔒 Only apply JWT logic for /api/*
        if (path.startsWith("/api")) {
            String authHeader = request.getHeader("Authorization");

            // No token → assign guest role
            String principal = "guest";
            CredentialValidationResult result = new CredentialValidationResult(
                    principal,
                    Set.of("guest"));

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring("Bearer ".length()).trim();
                Map<String, Object> header;
                try {
                    header = JwtUtils.decodeJwtHeader(token);
                } catch (Exception e) {
                    logger.warning("Failed to decode JWT header: " + e.getMessage());
                    return context.responseUnauthorized();
                }
                String kid = (String) header.get("kid");
                logger.info("Found Bearer token: " + token);
                try {
                    TokenInfo tokenInfo = JwtUtils.verifyAndExtract(token, openIdConfigBean.getJwksUrl(), kid);
                    principal = tokenInfo.getUsername();
                    result = new CredentialValidationResult(
                            principal,
                            tokenInfo.getRoles() != null ? Set.copyOf(tokenInfo.getRoles()) : Set.of());
                    logger.info("JWT verified for user: " + principal + " with roles: " + result.getCallerGroups());
                } catch (Exception e) {
                    logger.warning("Failed to verify and extract JWT: " + e.getMessage());
                    return context.responseUnauthorized();
                }
            }

            context.notifyContainerAboutLogin(result);
            return AuthenticationStatus.SUCCESS;
        }
        return AuthenticationStatus.NOT_DONE;
    }
}
