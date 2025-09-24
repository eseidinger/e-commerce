package com.ecommerce.jsf.rest;

import com.ecommerce.jsf.model.Customer;
import com.ecommerce.jsf.repository.CustomerRepository;

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
    private CustomerRepository customerRepository;

    @GET
    public List<Customer> getAll() {
        return customerRepository.findAll();
    }

    @GET
    @Path("/{id}")
    public Customer getById(@PathParam("id") Long id) {
        return customerRepository.findById(id);
    }

    @POST
    @RolesAllowed("admin")
    public Response create(Customer customer) {
        customerRepository.save(customer);
        return Response.status(Response.Status.CREATED).entity(customer).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed("admin")
    public Response update(@PathParam("id") Long id, Customer customer) {
        customer.setCustomerId(id);
        customerRepository.save(customer);
        return Response.ok(customer).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("admin")
    public Response delete(@PathParam("id") Long id) {
        customerRepository.delete(id);
        return Response.noContent().build();
    }
}
