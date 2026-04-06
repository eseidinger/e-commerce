package de.eseidinger.ecommerce.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import de.eseidinger.ecommerce.exception.ValidationException;
import de.eseidinger.ecommerce.model.Product;
import de.eseidinger.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

  @Mock private ProductRepository productRepository;

  @InjectMocks private ProductService productService;

  @Test
  void saveShouldPersistValidProduct() {
    Product product = new Product();
    product.setName("Laptop");
    product.setDescription("Fast business laptop");
    product.setPrice(1299.99);

    productService.save(product);

    verify(productRepository).save(product);
  }

  @Test
  void saveShouldThrowForInvalidPrice() {
    Product product = new Product();
    product.setName("Laptop");
    product.setDescription("Fast business laptop");
    product.setPrice(0.0);

    assertThrows(ValidationException.class, () -> productService.save(product));
    verify(productRepository, never()).save(product);
  }

  @Test
  void deleteShouldDelegateToRepository() {
    productService.delete(7L);

    verify(productRepository).delete(7L);
  }
}
