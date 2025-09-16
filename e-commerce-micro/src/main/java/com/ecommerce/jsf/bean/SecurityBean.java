package com.ecommerce.jsf.bean;

import fish.payara.security.annotations.OpenIdAuthenticationDefinition;
import jakarta.enterprise.context.ApplicationScoped;


@OpenIdAuthenticationDefinition(
       providerURI = "http://localhost:8084/realms/e-commerce-dev",
       clientId = "e-commerce",
       redirectURI = "${baseURL}/callback",
       scope = "roles"
)
@ApplicationScoped
public class SecurityBean {

}
