package de.eseidinger.ecommerce.bean;

import fish.payara.security.annotations.OpenIdAuthenticationDefinition;
import jakarta.annotation.security.DeclareRoles;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Declares OpenID Connect authentication settings and application security roles.
 */

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
