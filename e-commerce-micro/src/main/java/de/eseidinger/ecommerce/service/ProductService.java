package de.eseidinger.ecommerce.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

import de.eseidinger.ecommerce.exception.ValidationException;
import de.eseidinger.ecommerce.model.Product;
import de.eseidinger.ecommerce.repository.ProductRepository;
import de.eseidinger.ecommerce.util.InputValidator;

@ApplicationScoped
public class ProductService {

  @Inject private ProductRepository productRepository;

  public List<Product> findAll() {
    return productRepository.findAll();
  }

  public Product findById(Long id) {
    return productRepository.findById(id);
  }

  @Transactional
  public void save(Product product) {
    if (!InputValidator.isNonEmptyString(product.getName())) {
      throw new ValidationException("Invalid product name");
    }
    if (!InputValidator.isNonEmptyString(product.getDescription())) {
      throw new ValidationException("Invalid product description");
    }
    if (!InputValidator.isValidPrice(product.getPrice())) {
      throw new ValidationException("Invalid product price");
    }
    productRepository.save(product);
  }

  @Transactional
  public void delete(Long id) {
    productRepository.delete(id);
  }
}
