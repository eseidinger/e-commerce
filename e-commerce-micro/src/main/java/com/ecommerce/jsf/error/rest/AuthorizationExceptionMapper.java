package com.ecommerce.jsf.error.rest;

import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class AuthorizationExceptionMapper implements ExceptionMapper<ForbiddenException> {
  @Override
  public Response toResponse(ForbiddenException exception) {
    return Response.status(Response.Status.FORBIDDEN)
        .entity("{\"error\": \"" + exception.getMessage() + "\"}")
        .type(MediaType.APPLICATION_JSON)
        .build();
  }
}
