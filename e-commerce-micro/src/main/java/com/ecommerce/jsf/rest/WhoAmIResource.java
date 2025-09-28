package com.ecommerce.jsf.rest;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import java.security.Principal;

@Path("/auth")
@RequestScoped
public class WhoAmIResource {

  @Inject Principal principal;

  @GET
  @Path("/whoami")
  @RolesAllowed({"guest", "admin"})
  public String me() {
    if (principal == null) {
      return "No token!";
    }
    return principal.getName();
  }
}
