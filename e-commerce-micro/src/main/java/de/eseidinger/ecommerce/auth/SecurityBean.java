package de.eseidinger.ecommerce.auth;

import jakarta.annotation.security.DeclareRoles;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Declares application security roles used by REST resources and UI flows.
 */
@DeclareRoles({"guest", "admin"})
@ApplicationScoped
public class SecurityBean {}
