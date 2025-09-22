package com.ecommerce.jsf.auth.jwt;

import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.AuthenticationException;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.IdentityStoreHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.HttpHeaders;

@ApplicationScoped
public class GuestFallbackAuthentication implements HttpAuthenticationMechanism {

    @Inject
    private IdentityStoreHandler identityStoreHandler;

    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest request,
                                                HttpServletResponse response,
                                                HttpMessageContext context) throws AuthenticationException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String path = request.getRequestURI();

        // Only apply fallback for paths starting with /api/
        if (path != null && path.startsWith("/api/")) {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                // No JWT → assign guest role
                UsernamePasswordCredential guestCredential = new UsernamePasswordCredential("guest", "guest");
                return context.notifyContainerAboutLogin(guestCredential.getCaller(), Set.of("guest"));
            }
        }

        // For other paths or valid JWT, let default mechanism handle it
        return context.doNothing();
    }
}
