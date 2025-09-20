package com.ecommerce.jsf.auth;

import org.eclipse.microprofile.auth.LoginConfig;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/api")
@LoginConfig(authMethod = "MP-JWT")
public class RestApplication extends Application {
    // No implementation needed
}
