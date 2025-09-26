package com.ecommerce.jsf.rest;

import com.ecommerce.jsf.model.Order;
import com.ecommerce.jsf.service.OrderService;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {

    @Inject
    private OrderService orderService;

    @GET
    public List<Order> getAll() {
        return orderService.findAll();
    }

    @GET
    @Path("/{id}")
    public Order getById(@PathParam("id") Long id) {
        return orderService.findById(id);
    }

    @POST
    @RolesAllowed("admin")
    public Response create(Order order) {
        orderService.save(order);
        return Response.status(Response.Status.CREATED).entity(order).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed("admin")
    public Response update(@PathParam("id") Long id, Order order) {
        order.setOrderId(id);
        orderService.save(order);
        return Response.ok(order).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("admin")
    public Response delete(@PathParam("id") Long id) {
        orderService.delete(id);
        return Response.noContent().build();
    }
}
