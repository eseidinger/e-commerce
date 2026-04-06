package de.eseidinger.ecommerce.config;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Activates JAX-RS and sets the API base path for all REST resources.
 */
@ApplicationPath("/api")
public class ApplicationConfig extends Application {
  // No implementation needed, just activates JAX-RS
}
