package com.ecommerce.jsf.rest;

import com.ecommerce.jsf.model.OrderItem;
import com.ecommerce.jsf.repository.OrderItemRepository;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/order-items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderItemResource {
    @Inject
    private OrderItemRepository orderItemRepository;

    @GET
    public List<OrderItem> getAll() {
        return orderItemRepository.findAll();
    }

    @GET
    @Path("/{id}")
    public OrderItem getById(@PathParam("id") Long id) {
        return orderItemRepository.findById(id);
    }

    @POST
    @RolesAllowed("admin")
    public Response create(OrderItem orderItem) {
        orderItemRepository.save(orderItem);
        return Response.status(Response.Status.CREATED).entity(orderItem).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed("admin")
    public Response update(@PathParam("id") Long id, OrderItem orderItem) {
        orderItem.setOrderItemId(id);
        orderItemRepository.save(orderItem);
        return Response.ok(orderItem).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("admin")
    public Response delete(@PathParam("id") Long id) {
        orderItemRepository.delete(id);
        return Response.noContent().build();
    }
}
