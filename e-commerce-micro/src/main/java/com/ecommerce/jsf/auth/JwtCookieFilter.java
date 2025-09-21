package com.ecommerce.jsf.auth;

import java.io.IOException;
import java.io.StringReader;
import java.security.Principal;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ecommerce.jsf.auth.JwtUtils.TokenInfo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@WebFilter("/*")
public class JwtCookieFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(JwtCookieFilter.class);

    @Inject
    private OpenIdConfigBean openIdConfigBean;

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
        Response kcResp = client.target(tokenEndpoint)
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
            String newRefresh = obj.containsKey("refresh_token") ? obj.getString("refresh_token") : refreshToken;
            return new TokenPair(newJwt, newRefresh);
        }
        return null;
    }

    private String handleTokenRefresh(String refreshToken, HttpServletResponse response,
            ServletRequest request) throws IOException, ServletException {
        if (isRefreshTokenExpired(refreshToken)) {
            logger.debug("Refresh token expired");
            handleLoginRedirect(request, response);
            return null;
        }
        TokenPair tokenPair = refreshTokenPair(refreshToken, (HttpServletResponse) response);
        if (tokenPair == null) {
            logger.debug("Failed to refresh JWT");
            return null;
        }
        Cookie newJwtCookie = new Cookie("JWT", tokenPair.getIdToken());
        newJwtCookie.setPath("/");
        newJwtCookie.setHttpOnly(true);
        ((HttpServletResponse) response).addCookie(newJwtCookie);
        Cookie newRefreshCookie = new Cookie("JWT_REFRESH", tokenPair.getRefreshToken());
        newRefreshCookie.setPath("/");
        newRefreshCookie.setHttpOnly(true);
        ((HttpServletResponse) response).addCookie(newRefreshCookie);
        return tokenPair.getIdToken();
    }

    private HttpServletRequestWrapper handleExtractUserInfo(TokenInfo tokenInfo,
            HttpServletRequest req) throws Exception {
        // Extract user info from JWT payload
        String username = tokenInfo.getUsername();
        Set<String> roles = new HashSet<>(tokenInfo.getRoles());

        return new HttpServletRequestWrapper(req) {
            @Override
            public Principal getUserPrincipal() {
                return () -> username;
            }

            @Override
            public boolean isUserInRole(String role) {
                return roles.contains(role);
            }
        };
    }

    private void handleLoginRedirect(ServletRequest request, ServletResponse response)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        // allow access to index.html and base url without authentication
        if (req.getRequestURI().equals("/") || req.getRequestURI().equals("/index.html")) {
            return;
        }
        // redirect to login if not already on login or callback path
        if (!req.getRequestURI().endsWith("/login") && !req.getRequestURI().endsWith("/callback")
                && !req.getRequestURI().endsWith("/logout") && !req.getRequestURI().endsWith("/logout-callback")) {
            ((HttpServletResponse) response).sendRedirect(req.getContextPath() + "/api/auth/login");
        }
    }

    private TokenInfo extractIdTokenInfo(String jwt) throws Exception {
        Map<String, Object> header = JwtUtils.decodeJwtHeader(jwt);
        return JwtUtils.verifyAndExtract(jwt, openIdConfigBean.getJwksUrl(), header.get("kid").toString());
    }

    private Claims verifyIdToken(String jwt) throws Exception {
        Map<String, Object> header = JwtUtils.decodeJwtHeader(jwt);
        return JwtUtils.verify(jwt, openIdConfigBean.getJwksUrl(), header.get("kid").toString());
    }

    private boolean isRefreshTokenExpired(String jwt) {
        Map<String,Object> payload;
        try {
            payload = JwtUtils.decodeJwtPayload(jwt);
        } catch (Exception e) {
            logger.error("Failed to decode JWT payload", e);
            return false;
        }
        if (payload.containsKey("exp")) {
            Long exp = ((Number) payload.get("exp")).longValue();
            Long now = System.currentTimeMillis() / 1000;
            return now >= exp;
        }
        return false;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String jwt = getCookie("JWT", req.getCookies());
        String refreshToken = getCookie("JWT_REFRESH", req.getCookies());
        if (jwt == null) {
            logger.debug("No JWT cookie found");
            handleLoginRedirect(request, response);
            chain.doFilter(request, response);
            return;
        }
        try {
            verifyIdToken(jwt);
        } catch (ExpiredJwtException e) {
            logger.debug("JWT expired", e);
            if (refreshToken != null) {
                jwt = handleTokenRefresh(refreshToken, (HttpServletResponse) response, request);
                if (jwt == null) {
                    logger.debug("Failed to refresh JWT");
                    chain.doFilter(request, response);
                    return;
                }
            } else {
                logger.debug("Token expired and no refresh token available");
                handleLoginRedirect(request, response);
                chain.doFilter(request, response);
                return;
            }
        } catch (Exception e) {
            logger.error("Failed to decode JWT payload", e);
            chain.doFilter(request, response);
            return;
        }

        try {
            TokenInfo tokenInfo = extractIdTokenInfo(jwt);
            HttpServletRequestWrapper wrapped = handleExtractUserInfo(tokenInfo, req);
            chain.doFilter(wrapped, response);
            return;
        } catch (Exception e) {
            logger.error("Failed to verify JWT", e);
            chain.doFilter(request, response);
            return;
        }
    }
}
