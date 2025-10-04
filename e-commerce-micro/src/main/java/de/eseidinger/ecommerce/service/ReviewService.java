package de.eseidinger.ecommerce.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

import de.eseidinger.ecommerce.exception.ValidationException;
import de.eseidinger.ecommerce.model.Customer;
import de.eseidinger.ecommerce.model.Product;
import de.eseidinger.ecommerce.model.Review;
import de.eseidinger.ecommerce.repository.CustomerRepository;
import de.eseidinger.ecommerce.repository.ProductRepository;
import de.eseidinger.ecommerce.repository.ReviewRepository;
import de.eseidinger.ecommerce.util.InputValidator;

@ApplicationScoped
public class ReviewService {

  @Inject private ReviewRepository reviewRepository;

  @Inject private ProductRepository productRepository;

  @Inject private CustomerRepository customerRepository;

  public List<Review> findAll() {
    return reviewRepository.findAll();
  }

  public Review findById(Long id) {
    return reviewRepository.findById(id);
  }

  @Transactional
  public void save(Review review) {
    if (!InputValidator.isValidRating(review.getRating())) {
      throw new ValidationException("Rating must be between 1 and 5");
    }
    if (!InputValidator.isNonEmptyString(review.getComment())) {
      throw new ValidationException("Comment cannot be empty");
    }
    if (review.getReviewDate() == null) {
      throw new ValidationException("Review date cannot be null");
    }
    if (review.getProductId() == null) {
      throw new ValidationException("Product ID cannot be null");
    }
    if (review.getCustomerId() == null) {
      throw new ValidationException("Customer ID cannot be null");
    }
    Product product = productRepository.findById(review.getProductId());
    if (product == null) {
      throw new ValidationException("Product not found for id: " + review.getProductId());
    }
    review.setProduct(product);
    Customer customer = customerRepository.findById(review.getCustomerId());
    if (customer == null) {
      throw new ValidationException("Customer not found for id: " + review.getCustomerId());
    }
    review.setCustomer(customer);
    reviewRepository.save(review);
  }

  @Transactional
  public void delete(Long id) {
    reviewRepository.delete(id);
  }
}
