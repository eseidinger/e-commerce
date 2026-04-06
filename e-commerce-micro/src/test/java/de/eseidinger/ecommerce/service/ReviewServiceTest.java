package de.eseidinger.ecommerce.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.eseidinger.ecommerce.exception.ValidationException;
import de.eseidinger.ecommerce.model.Customer;
import de.eseidinger.ecommerce.model.Product;
import de.eseidinger.ecommerce.model.Review;
import de.eseidinger.ecommerce.repository.CustomerRepository;
import de.eseidinger.ecommerce.repository.ProductRepository;
import de.eseidinger.ecommerce.repository.ReviewRepository;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

  @Mock private ReviewRepository reviewRepository;

  @Mock private ProductRepository productRepository;

  @Mock private CustomerRepository customerRepository;

  @InjectMocks private ReviewService reviewService;

  @Test
  void saveShouldAttachProductAndCustomerAndPersistValidReview() {
    Review review = new Review();
    review.setRating(5);
    review.setComment("Great product");
    review.setReviewDate(new Date());
    review.setProductId(11L);
    review.setCustomerId(12L);

    Product product = new Product();
    product.setProductId(11L);
    when(productRepository.findById(11L)).thenReturn(product);

    Customer customer = new Customer();
    customer.setCustomerId(12L);
    when(customerRepository.findById(12L)).thenReturn(customer);

    reviewService.save(review);

    verify(productRepository).findById(11L);
    verify(customerRepository).findById(12L);
    verify(reviewRepository).save(review);
  }

  @Test
  void saveShouldThrowWhenRatingIsInvalid() {
    Review review = new Review();
    review.setRating(0);
    review.setComment("Great product");
    review.setReviewDate(new Date());
    review.setProductId(11L);
    review.setCustomerId(12L);

    assertThrows(ValidationException.class, () -> reviewService.save(review));
    verify(reviewRepository, never()).save(review);
  }

  @Test
  void saveShouldThrowWhenCustomerDoesNotExist() {
    Review review = new Review();
    review.setRating(5);
    review.setComment("Great product");
    review.setReviewDate(new Date());
    review.setProductId(11L);
    review.setCustomerId(12L);

    Product product = new Product();
    product.setProductId(11L);
    when(productRepository.findById(11L)).thenReturn(product);
    when(customerRepository.findById(12L)).thenReturn(null);

    assertThrows(ValidationException.class, () -> reviewService.save(review));
    verify(reviewRepository, never()).save(review);
  }
}
