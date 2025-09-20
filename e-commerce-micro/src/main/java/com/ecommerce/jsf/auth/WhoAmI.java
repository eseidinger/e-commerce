package com.ecommerce.jsf.auth;

import java.security.Principal;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;

@Path("/auth")
@RequestScoped
public class WhoAmI {

    @Inject
    Principal principal;

    @GET
    @Path("/whoami")
    @RolesAllowed({"guest", "admin"})
    public String me(@HeaderParam("Authorization") String authHeader) {
        if (principal == null) {
            return "No token!";
        }
        return "Name: " + principal.getName();
    }
}
