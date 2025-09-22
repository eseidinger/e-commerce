package com.ecommerce.jsf.auth.jwt;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.Priority;
import jakarta.decorator.Decorator;
import jakarta.decorator.Delegate;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptor;
import jakarta.security.enterprise.AuthenticationException;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStoreHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Decorator
@Priority(Interceptor.Priority.APPLICATION)
public class GuestFallbackAuthentication implements HttpAuthenticationMechanism {

    private static final Logger logger = LoggerFactory.getLogger(GuestFallbackAuthentication.class);

    @Inject
    @Delegate
    @Any
    private HttpAuthenticationMechanism delegate;

    @Inject
    private IdentityStoreHandler identityStoreHandler;

    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest request,
            HttpServletResponse response,
            HttpMessageContext context) throws AuthenticationException {
        logger.info("GuestFallbackAuthentication triggered");
        String path = request.getRequestURI();

        // 🔒 Only apply JWT logic for /api/*
        if (path.startsWith("/api")) {
            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                return delegate.validateRequest(request, response, context);
            }

            // No token → assign guest role
            String guestPrincipal = "guest";
            CredentialValidationResult guestResult = new CredentialValidationResult(
                    guestPrincipal,
                    Set.of("guest"));

            request.setAttribute(CredentialValidationResult.class.getName(), guestResult);
            return AuthenticationStatus.SUCCESS;
        }

        // 🛑 Outside /api → skip authentication
        return AuthenticationStatus.NOT_DONE;
    }
}
