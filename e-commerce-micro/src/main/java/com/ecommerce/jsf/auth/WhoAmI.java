package com.ecommerce.jsf.auth;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import java.security.Principal;

@Path("/whoami")
@RequestScoped
public class WhoAmI {

    @Inject
    Principal principal;

    @GET
    @RolesAllowed({"guest", "admin"})
    public String me() {
        if (principal == null) {
            return "No token!";
        }
        return "Name: " + principal.getName();
    }
}
