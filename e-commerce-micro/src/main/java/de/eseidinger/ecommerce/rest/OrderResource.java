package de.eseidinger.ecommerce.rest;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

import de.eseidinger.ecommerce.dto.OrderDto;
import de.eseidinger.ecommerce.model.Order;
import de.eseidinger.ecommerce.service.OrderService;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {

  @Inject private OrderService orderService;

  @GET
  public List<OrderDto> getAll() {
    return orderService.findAll().stream().map(Order::toDto).toList();
  }

  @GET
  @Path("/{id}")
  public OrderDto getById(@PathParam("id") Long id) {
    Order order = orderService.findById(id);
    return order != null ? Order.toDto(order) : null;
  }

  @POST
  @RolesAllowed("admin")
  public Response create(OrderDto orderDto) {
    Order order = Order.fromDto(orderDto);
    orderService.save(order);
    return Response.status(Response.Status.CREATED).entity(Order.toDto(order)).build();
  }

  @PUT
  @Path("/{id}")
  @RolesAllowed("admin")
  public Response update(@PathParam("id") Long id, Order order) {
    order.setOrderId(id);
    orderService.save(order);
    return Response.ok(Order.toDto(order)).build();
  }

  @DELETE
  @Path("/{id}")
  @RolesAllowed("admin")
  public Response delete(@PathParam("id") Long id) {
    orderService.delete(id);
    return Response.noContent().build();
  }
}
