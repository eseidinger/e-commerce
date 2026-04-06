package de.eseidinger.ecommerce.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.eseidinger.ecommerce.dto.ProductDto;
import de.eseidinger.ecommerce.model.Product;
import de.eseidinger.ecommerce.service.ProductService;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductResourceTest {

  @Mock private ProductService productService;

  @InjectMocks private ProductResource productResource;

  @Test
  void getByIdShouldReturnDtoWhenFound() {
    Product product = new Product();
    product.setProductId(7L);
    product.setName("Laptop");
    product.setDescription("Fast laptop");
    product.setPrice(999.0);
    when(productService.findById(7L)).thenReturn(product);

    ProductDto result = productResource.getById(7L);

    assertEquals("Laptop", result.name());
    assertEquals(999.0, result.price());
  }

  @Test
  void updateShouldReturnOkResponse() {
    ProductDto dto = new ProductDto(7L, "Laptop", "Fast laptop", 999.0);

    Response response = productResource.update(7L, dto);

    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    verify(productService).save(org.mockito.ArgumentMatchers.any(Product.class));
  }

  @Test
  void deleteShouldReturnNoContent() {
    Response response = productResource.delete(7L);

    assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    verify(productService).delete(7L);
  }
}
