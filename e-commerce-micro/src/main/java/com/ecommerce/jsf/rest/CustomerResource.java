package com.ecommerce.jsf.rest;

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

    @Inject
    private CustomerService customerService;

    @GET
    public List<Customer> getAll() {
        return customerService.findAll();
    }

    @GET
    @Path("/{id}")
    public Customer getById(@PathParam("id") Long id) {
        // Optionally, add a findById method to CustomerService if needed
        throw new UnsupportedOperationException("Not implemented: use service method");
    }

    @POST
    @RolesAllowed("admin")
    public Response create(Customer customer) {
        customerService.save(customer);
        return Response.status(Response.Status.CREATED).entity(customer).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed("admin")
    public Response update(@PathParam("id") Long id, Customer customer) {
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
