package com.ecommerce.jsf.rest;

import com.ecommerce.jsf.dto.CustomerDto;
import com.ecommerce.jsf.model.Customer;
import com.ecommerce.jsf.service.CustomerService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerResource {

  @Inject private CustomerService customerService;

  @GET
  public List<CustomerDto> getAll() {
    return customerService.findAll().stream().map(Customer::toDto).toList();
  }

  @GET
  @Path("/{id}")
  public CustomerDto getById(@PathParam("id") Long id) {
    return customerService.findById(id) != null
        ? Customer.toDto(customerService.findById(id))
        : null;
  }

  @POST
  @RolesAllowed("admin")
  public Response create(CustomerDto customer) {
    customerService.save(Customer.fromDto(customer));
    return Response.status(Response.Status.CREATED).entity(customer).build();
  }

  @PUT
  @Path("/{id}")
  @RolesAllowed("admin")
  public Response update(@PathParam("id") Long id, CustomerDto customerDto) {
    Customer customer = Customer.fromDto(customerDto);
    customer.setCustomerId(id);
    customerService.save(customer);
    return Response.ok(customer).build();
  }

  @DELETE
  @Path("/{id}")
  @RolesAllowed("admin")
  public Response delete(@PathParam("id") Long id) {
    customerService.delete(id);
    return Response.noContent().build();
  }
}
