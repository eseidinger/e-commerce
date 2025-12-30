package com.ecommerce.jsf.bean;

import fish.payara.security.annotations.OpenIdAuthenticationDefinition;
import jakarta.annotation.security.DeclareRoles;
import jakarta.enterprise.context.ApplicationScoped;


@OpenIdAuthenticationDefinition(
       providerURI = "#{openIdConfigBean.providerUri}",
       clientId = "#{openIdConfigBean.clientId}",
       redirectURI = "#{openIdConfigBean.redirectUri}",
       scope = { "openid", "profile", "email", "roles" }
)
@DeclareRoles({"guest", "admin"})
@ApplicationScoped
public class SecurityBean {

}
