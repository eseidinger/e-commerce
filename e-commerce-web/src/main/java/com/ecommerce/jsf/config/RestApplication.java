package com.ecommerce.jsf.config;

import org.eclipse.microprofile.auth.LoginConfig;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import org.slf4j.bridge.SLF4JBridgeHandler;

@ApplicationPath("/api")
@LoginConfig(authMethod = "MP-JWT")
@ApplicationScoped
public class RestApplication extends Application {
    static {
        // Remove existing handlers
        SLF4JBridgeHandler.removeHandlersForRootLogger();

        // Install SLF4J bridge
        SLF4JBridgeHandler.install();
    }
}
