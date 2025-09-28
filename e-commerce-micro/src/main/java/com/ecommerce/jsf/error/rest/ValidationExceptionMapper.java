package com.ecommerce.jsf.error.rest;

import com.ecommerce.jsf.exception.ValidationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {
  @Override
  public Response toResponse(ValidationException exception) {
    return Response.status(Response.Status.BAD_REQUEST)
        .entity("{\"error\": \"" + exception.getMessage() + "\"}")
        .type(MediaType.APPLICATION_JSON)
        .build();
  }
}
