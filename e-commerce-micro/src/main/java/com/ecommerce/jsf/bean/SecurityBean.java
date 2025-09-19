package com.ecommerce.jsf.bean;

import fish.payara.security.annotations.OpenIdAuthenticationDefinition;
import fish.payara.security.annotations.ProxyDefinition;
import jakarta.annotation.security.DeclareRoles;
import jakarta.enterprise.context.ApplicationScoped;


@OpenIdAuthenticationDefinition(
       providerURI = "#{openIdConfigBean.providerUri}",
       clientId = "#{openIdConfigBean.clientId}",
       redirectURI = "#{openIdConfigBean.redirectUri}",
       proxyDefinition = @ProxyDefinition(
           hostName = "#{openIdConfigBean.proxyHost}",
           port = "#{openIdConfigBean.proxyPort}"
       ),
       scope = { "openid", "profile", "email", "roles" }
)
@DeclareRoles({"guest", "admin"})
@ApplicationScoped
public class SecurityBean {

}
