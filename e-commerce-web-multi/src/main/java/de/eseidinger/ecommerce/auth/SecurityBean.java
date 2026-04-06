package de.eseidinger.ecommerce.auth;

import jakarta.annotation.security.DeclareRoles;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Defines security role declarations used throughout the application.
 */
@DeclareRoles({"guest", "admin"})
@ApplicationScoped
public class SecurityBean {}
