package com.ecommerce.jsf.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class JwtUtils {

    public static Map<String, Object> decodeJwtPayload(String token) throws Exception {
        String[] parts = token.split("\\.");
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid JWT format.");
        }

        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(payloadJson, Map.class);
    }

    public static Map<String, Object> decodeJwtHeader(String token) throws Exception {
        String[] parts = token.split("\\.");
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid JWT format.");
        }

        String headerJson = new String(Base64.getUrlDecoder().decode(parts[0]));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(headerJson, Map.class);
    }

    /**
     * Simple POJO for extracted info.
     */
    public static class TokenInfo {
        private final String username;
        private final List<String> roles;
        private final Long exp;

        public TokenInfo(String username, List<String> roles, Long exp) {
            this.username = username;
            this.roles = roles;
            this.exp = exp;
        }

        public String getUsername() {
            return username;
        }

        public List<String> getRoles() {
            return roles;
        }

        public Long getExp() {
            return exp;
        }
    }

    /**
     * Verifies a JWT using the public key from Keycloak, and extracts username and
     * roles.
     *
     * @param token             The JWT string
     * @param keycloakPublicKey The Keycloak public key in Base64 (from realm
     *                          settings -> Keys -> RS256)
     * @return UserInfo containing username and roles
     */
    public static TokenInfo verifyAndExtract(String token, String jwksUrl, String kid) throws Exception {
        PublicKey publicKey = getPublicKeyFromJwks(jwksUrl, kid);

        try {
            Jws<Claims> jws = Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token);

            Claims claims = jws.getPayload();

            // Extract username (Keycloak typically uses "preferred_username")
            String username = claims.get("preferred_username", String.class);

            // Roles are usually nested under groups
            List<String> roles = null;
            if (claims.get("groups") != null) {
                roles = claims.get("groups", List.class);
            }

            Long exp = claims.get("exp", Long.class);

            return new TokenInfo(username, roles, exp);

        } catch (SignatureException e) {
            throw new SecurityException("Invalid JWT signature", e);
        }
    }

    /**
     * Downloads the RSA public key from the Keycloak JWKS endpoint.
     *
     * @param jwksUrl Keycloak JWKS endpoint, e.g.
     *                https://<host>/realms/<realm>/protocol/openid-connect/certs
     * @param kid     The Key ID (KID) from the JWT header
     * @return PublicKey object
     */
    public static PublicKey getPublicKeyFromJwks(String jwksUrl, String kid) throws Exception {
        // Fetch JWKS JSON using Jakarta REST Client
        Client client = ClientBuilder.newClient();
        String jwksJson = client.target(jwksUrl)
                .request("application/json")
                .get(String.class);
        client.close();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jwks = mapper.readTree(jwksJson);

        // Iterate over keys to find matching kid
        for (JsonNode key : jwks.get("keys")) {
            if (key.get("kid").asText().equals(kid)) {
                String modulusB64 = key.get("n").asText();
                String exponentB64 = key.get("e").asText();

                byte[] modulusBytes = Base64.getUrlDecoder().decode(modulusB64);
                byte[] exponentBytes = Base64.getUrlDecoder().decode(exponentB64);

                BigInteger modulus = new BigInteger(1, modulusBytes);
                BigInteger exponent = new BigInteger(1, exponentBytes);

                RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                return keyFactory.generatePublic(spec);
            }
        }

        throw new RuntimeException("No matching key found in JWKS for kid: " + kid);
    }
}
