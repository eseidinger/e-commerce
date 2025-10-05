package de.eseidinger.ecommerce.auth;

import jakarta.annotation.security.DeclareRoles;
import jakarta.enterprise.context.ApplicationScoped;

@DeclareRoles({"guest", "admin"})
@ApplicationScoped
public class SecurityBean {}
