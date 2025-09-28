package com.ecommerce.jsf.rest;

import com.ecommerce.jsf.dto.OrderItemDto;
import com.ecommerce.jsf.model.OrderItem;
import com.ecommerce.jsf.service.OrderItemService;
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

  @Inject private OrderItemService orderItemService;

  @GET
  public List<OrderItemDto> getAll() {
    return orderItemService.findAll().stream().map(OrderItem::toDto).toList();
  }

  @GET
  @Path("/{id}")
  public OrderItemDto getById(@PathParam("id") Long id) {
    return orderItemService.findById(id) != null
        ? OrderItem.toDto(orderItemService.findById(id))
        : null;
  }

  @POST
  @RolesAllowed("admin")
  public Response create(OrderItem orderItem) {
    orderItemService.save(orderItem);
    return Response.status(Response.Status.CREATED).entity(OrderItem.toDto(orderItem)).build();
  }

  @PUT
  @Path("/{id}")
  @RolesAllowed("admin")
  public Response update(@PathParam("id") Long id, OrderItem orderItem) {
    orderItem.setOrderItemId(id);
    orderItemService.save(orderItem);
    return Response.ok(OrderItem.toDto(orderItem)).build();
  }

  @DELETE
  @Path("/{id}")
  @RolesAllowed("admin")
  public Response delete(@PathParam("id") Long id) {
    orderItemService.delete(id);
    return Response.noContent().build();
  }
}
