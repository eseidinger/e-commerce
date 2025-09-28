package com.ecommerce.jsf.error.rest;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class RuntimeExceptionMapper implements ExceptionMapper<RuntimeException> {
  @Override
  public Response toResponse(RuntimeException exception) {
    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
        .entity("{\"error\": \"An unexpected error occurred: " + exception.getMessage() + "\"}")
        .type(MediaType.APPLICATION_JSON)
        .build();
  }
}
