package com.ecommerce.jsf.service;

import com.ecommerce.jsf.model.Review;
import com.ecommerce.jsf.repository.ReviewRepository;
import com.ecommerce.jsf.util.InputValidator;
import com.ecommerce.jsf.model.Customer;
import com.ecommerce.jsf.model.Product;
import com.ecommerce.jsf.repository.ProductRepository;
import com.ecommerce.jsf.repository.CustomerRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class ReviewService {

    @Inject
    private ReviewRepository reviewRepository;

    @Inject
    private ProductRepository productRepository;

    @Inject
    private CustomerRepository customerRepository;

    public List<Review> findAll() {
        return reviewRepository.findAll();
    }

    public Review findById(Long id) {
        return reviewRepository.findById(id);
    }

    @Transactional
    public void save(Review review) {
        if (!InputValidator.isValidRating(review.getRating())) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        if (!InputValidator.isNonEmptyString(review.getComment())) {
            throw new IllegalArgumentException("Comment cannot be empty");
        }
        if (review.getProductId() == null) {    
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        if (review.getCustomerId() == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }
        Product product = productRepository.findById(review.getProductId());
        if (product == null) {
            throw new IllegalArgumentException("Product not found for id: " + review.getProductId());
        }
        review.setProduct(product);
        Customer customer = customerRepository.findById(review.getCustomerId());
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found for id: " + review.getCustomerId());
        }
        review.setCustomer(customer);
        review.setReviewDate(Date.from(Instant.now()));
        reviewRepository.save(review);
    }

    @Transactional
    public void delete(Long id) {
        reviewRepository.delete(id);
    }
}
