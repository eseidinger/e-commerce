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

    // instantiate logger
    private static final Logger logger = LoggerFactory.getLogger(JwtCookieFilter.class);

    @Inject
    private OpenIdConfigBean openIdConfigBean;

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

    private boolean isTokenExpired(Long exp) {
        Long now = System.currentTimeMillis() / 1000;
        return exp != null && exp < now;
    }

    private String handleTokenRefresh(String refreshToken, HttpServletResponse response) {
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
            // Set new cookies
            Cookie newJwtCookie = new Cookie("JWT", newJwt);
            newJwtCookie.setPath("/");
            newJwtCookie.setHttpOnly(true);
            ((HttpServletResponse) response).addCookie(newJwtCookie);
            Cookie newRefreshCookie = new Cookie("JWT_REFRESH", newRefresh);
            newRefreshCookie.setPath("/");
            newRefreshCookie.setHttpOnly(true);
            ((HttpServletResponse) response).addCookie(newRefreshCookie);
            return newJwt;
        }
        return null;
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

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String jwt = getCookie("JWT", req.getCookies());
        if (jwt == null) {
            logger.debug("No JWT cookie found");
            // allow access to index.html and base url without authentication
            if (req.getRequestURI().equals("/") || req.getRequestURI().equals("/index.html")) {
                chain.doFilter(request, response);
                return;
            }
            // redirect to login if not already on login or callback path
            if (!req.getRequestURI().endsWith("/login") && !req.getRequestURI().endsWith("/callback")
                    && !req.getRequestURI().endsWith("/logout") && !req.getRequestURI().endsWith("/logout-callback")) {
                ((HttpServletResponse) response).sendRedirect(req.getContextPath() + "/api/auth/login");
                return;
            }
            chain.doFilter(request, response);
            return;
        }
        TokenInfo tokenInfo;
        try {
            Map<String, Object> header = JwtUtils.decodeJwtHeader(jwt);
            tokenInfo = JwtUtils.verifyAndExtract(jwt, openIdConfigBean.getJwksUrl(), header.get("kid").toString());
        } catch (Exception e) {
            logger.error("Failed to decode JWT payload", e);
            chain.doFilter(request, response);
            return;
        }

        String refreshToken = getCookie("JWT_REFRESH", req.getCookies());
        boolean tokenExpired = isTokenExpired(tokenInfo.getExp());
        if (tokenExpired && refreshToken != null) {
            jwt = handleTokenRefresh(refreshToken, (HttpServletResponse) response);
            if (jwt == null) {
                logger.debug("Failed to refresh JWT");
                chain.doFilter(request, response);
                return;
            }
            try {
                tokenInfo = JwtUtils.verifyAndExtract(jwt, openIdConfigBean.getJwksUrl(),
                        JwtUtils.decodeJwtHeader(jwt).get("kid").toString());
            } catch (Exception e) {
                logger.error("Failed to verify refreshed JWT", e);
                chain.doFilter(request, response);
                return;
            }
        } else if (tokenExpired && refreshToken == null) {
            logger.debug("Token expired and no refresh token available");
            chain.doFilter(request, response);
            return;
        }
        try {
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
