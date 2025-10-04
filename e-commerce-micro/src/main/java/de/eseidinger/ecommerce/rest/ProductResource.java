package de.eseidinger.ecommerce.rest;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

import de.eseidinger.ecommerce.dto.ProductDto;
import de.eseidinger.ecommerce.model.Product;
import de.eseidinger.ecommerce.service.ProductService;

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource {

  @Inject private ProductService productService;

  @GET
  public List<ProductDto> getAll() {
    return productService.findAll().stream().map(Product::toDto).toList();
  }

  @GET
  @Path("/{id}")
  public ProductDto getById(@PathParam("id") Long id) {
    return productService.findById(id) != null ? Product.toDto(productService.findById(id)) : null;
  }

  @POST
  @RolesAllowed("admin")
  public Response create(ProductDto product) {
    productService.save(Product.fromDto(product));
    return Response.status(Response.Status.CREATED).entity(product).build();
  }

  @PUT
  @Path("/{id}")
  @RolesAllowed("admin")
  public Response update(@PathParam("id") Long id, ProductDto productDto) {
    Product product = Product.fromDto(productDto);
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
