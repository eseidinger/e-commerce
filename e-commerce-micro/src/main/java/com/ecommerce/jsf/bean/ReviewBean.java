
package com.ecommerce.jsf.bean;

import jakarta.faces.context.FacesContext;
import jakarta.faces.application.FacesMessage;

import com.ecommerce.jsf.model.Review;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import jakarta.faces.view.ViewScoped;

import com.ecommerce.jsf.repository.CustomerRepository;
import com.ecommerce.jsf.repository.ProductRepository;
import com.ecommerce.jsf.repository.ReviewRepository;
import jakarta.inject.Inject;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("reviewBean")
@ViewScoped
public class ReviewBean implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ReviewBean.class);

    private static final long serialVersionUID = 1L;

    private Review review = new Review();
    private List<Review> reviews;

    @Inject
    private ReviewRepository reviewRepository;

    @Inject
    private ProductRepository productRepository;

    @Inject
    private CustomerRepository customerRepository;

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }

    public List<Review> getReviews() {
        try {
            if (reviews == null) {
                logger.info("Loading review list from database");
                reviews = reviewRepository.findAll();
            }
            return reviews;
        } catch (Exception e) {
            logger.error("Error loading reviews: {}", e.getMessage());
            return null;
        }
    }

    @Transactional
    public String save() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (!context.getExternalContext().isUserInRole("admin")) {
            logger.warn("Unauthorized save attempt by user without admin role");
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Security Error",
                    "Access denied: admin role required"));
            return null;
        }
        try {
            review.setProduct(productRepository.findById(review.getProductId()));
            review.setCustomer(customerRepository.findById(review.getCustomerId()));
            review.setReviewDate(Date.from(Instant.now()));
            reviewRepository.save(review);
            review = new Review();
            reviews = null;
        } catch (Exception e) {
            logger.error("Error saving review: {}", e.getMessage());
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                    "An error occurred while saving the review."));
        }
        return null;
    }

    public String edit(Review r) {
        logger.info("Editing review with ID: {}", r.getReviewId());
        FacesContext context = FacesContext.getCurrentInstance();
        if (!context.getExternalContext().isUserInRole("admin")) {
            logger.warn("Unauthorized edit attempt by user without admin role");
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Security Error",
                    "Access denied: admin role required"));
            return null;
        }
        this.review = r;
        return null;
    }

    @Transactional
    public String delete(Review r) {
        FacesContext context = FacesContext.getCurrentInstance();
        if (!context.getExternalContext().isUserInRole("admin")) {
            logger.warn("Unauthorized delete attempt by user without admin role");
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Security Error",
                    "Access denied: admin role required"));
            return null;
        }
        try {
            logger.info("Deleting review with ID: {}", r.getReviewId());
            reviewRepository.delete(r.getReviewId());
            reviews = null;
        } catch (Exception e) {
            logger.error("Error deleting review: {}", e.getMessage());
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                    "An error occurred while deleting the review."));
        }
        return null;
    }
}
