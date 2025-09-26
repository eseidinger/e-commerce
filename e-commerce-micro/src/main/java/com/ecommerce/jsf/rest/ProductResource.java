package com.ecommerce.jsf.rest;

import com.ecommerce.jsf.model.Product;
import com.ecommerce.jsf.service.ProductService;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource {

    @Inject
    private ProductService productService;

    @GET
    public List<Product> getAll() {
        return productService.findAll();
    }

    @GET
    @Path("/{id}")
    public Product getById(@PathParam("id") Long id) {
        return productService.findById(id);
    }

    @POST
    @RolesAllowed("admin")
    public Response create(Product product) {
        productService.save(product);
        return Response.status(Response.Status.CREATED).entity(product).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed("admin")
    public Response update(@PathParam("id") Long id, Product product) {
        product.setProductId(id);
        productService.save(product);
        return Response.ok(product).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("admin")
    public Response delete(@PathParam("id") Long id) {
        productService.delete(id);
        return Response.noContent().build();
    }
}
