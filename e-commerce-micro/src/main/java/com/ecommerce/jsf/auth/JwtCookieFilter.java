package com.ecommerce.jsf.auth;

import java.io.IOException;
import java.security.Principal;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

@WebFilter("/*")
public class JwtCookieFilter implements Filter {

    //instantiate logger
    private static final Logger logger = LoggerFactory.getLogger(JwtCookieFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String jwt = null;
        if (req.getCookies() != null) {
            for (Cookie cookie : req.getCookies()) {
                if ("JWT".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    break;
                }
            }
        }
        if (jwt != null) {
            try {
                Map<String, Object> payload = JwtUtils.decodeJwtPayload(jwt);

                String username = (String) payload.get("preferred_username");
                Set<String> roles = new HashSet<>();
                Object rolesClaim = payload.get("groups");

                if (rolesClaim instanceof Iterable<?>) {
                    for (Object r : (Iterable<?>) rolesClaim) {
                        roles.add(r.toString());
                    }
                } else if (rolesClaim instanceof String) {
                    roles.add((String) rolesClaim);
                }
                HttpServletRequestWrapper wrapped = new HttpServletRequestWrapper(req) {
                    @Override
                    public Principal getUserPrincipal() {
                        return () -> username;
                    }
                    @Override
                    public boolean isUserInRole(String role) {
                        return roles.contains(role);
                    }
                };
                chain.doFilter(wrapped, response);
                return;
            } catch (Exception e) {
                // Invalid JWT, fall through to unauthenticated
                // Log the exception for debugging purposes using slf4j
                logger.error("Invalid JWT token", e);
            }
        }
        chain.doFilter(request, response);
    }
}
