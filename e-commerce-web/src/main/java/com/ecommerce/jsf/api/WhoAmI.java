package com.ecommerce.jsf.api;

import java.security.Principal;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/auth")
@RequestScoped
public class WhoAmI {

    @Inject
    Principal principal;

    @Inject
    HttpAuthenticationMechanism httpAuthenticationMechanism;

    @GET
    @Path("/whoami")
    @RolesAllowed({"guest", "admin"})
    public String me() {
        if (principal == null) {
            return "No token!";
        }
        return principal.getName() + " authenticated via " + httpAuthenticationMechanism.getClass().getSimpleName();
    }
}
