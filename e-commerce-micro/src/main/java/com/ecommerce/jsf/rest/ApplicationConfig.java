package com.ecommerce.jsf.rest;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/api")
public class ApplicationConfig extends Application {
  // No implementation needed, just activates JAX-RS
}
