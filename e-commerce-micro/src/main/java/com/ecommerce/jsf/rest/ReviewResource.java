package com.ecommerce.jsf.rest;

import com.ecommerce.jsf.dto.ReviewDto;
import com.ecommerce.jsf.model.Review;
import com.ecommerce.jsf.service.ReviewService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/reviews")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReviewResource {

  @Inject private ReviewService reviewService;

  @GET
  public List<ReviewDto> getAll() {
    return reviewService.findAll().stream().map(Review::toDto).toList();
  }

  @GET
  @Path("/{id}")
  public ReviewDto getById(@PathParam("id") Long id) {
    return reviewService.findById(id) != null ? Review.toDto(reviewService.findById(id)) : null;
  }

  @POST
  @RolesAllowed("admin")
  public Response create(Review review) {
    reviewService.save(review);
    return Response.status(Response.Status.CREATED).entity(Review.toDto(review)).build();
  }

  @PUT
  @Path("/{id}")
  @RolesAllowed("admin")
  public Response update(@PathParam("id") Long id, Review review) {
    review.setReviewId(id);
    reviewService.save(review);
    return Response.ok(Review.toDto(review)).build();
  }

  @DELETE
  @Path("/{id}")
  @RolesAllowed("admin")
  public Response delete(@PathParam("id") Long id) {
    reviewService.delete(id);
    return Response.noContent().build();
  }
}
