
package com.ecommerce.jsf.bean;

import jakarta.faces.context.FacesContext;
import jakarta.faces.application.FacesMessage;

import com.ecommerce.jsf.model.Customer;
import com.ecommerce.jsf.model.Product;
import com.ecommerce.jsf.model.Review;
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import jakarta.transaction.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("reviewBean")
@ViewScoped
@Transactional
public class ReviewBean implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(ReviewBean.class);
    private static final long serialVersionUID = 1L;
    private Review review = new Review();
    private List<Review> reviews;
    @PersistenceContext(unitName = "ecommercePU")
    private EntityManager em;

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
                TypedQuery<Review> query = em.createQuery("SELECT r FROM Review r", Review.class);
                reviews = query.getResultList();
            }
            return reviews;
        } catch (Exception e) {
            logger.error("Error loading reviews: {}", e.getMessage());
            return null;
        }
    }

    private Product getProductById(Long productId) {
        try {
            return em.find(Product.class, productId);
        } catch (Exception e) {
            logger.error("Error finding product by ID {}: {}", productId, e.getMessage());
            throw e;
        }
    }

    private Customer getCustomerById(Long customerId) {
        try {
            return em.find(Customer.class, customerId);
        } catch (Exception e) {
            logger.error("Error finding customer by ID {}: {}", customerId, e.getMessage());
            throw e;
        }
    }

    public String save() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (!context.getExternalContext().isUserInRole("admin")) {
            logger.warn("Unauthorized save attempt by user without admin role");
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Security Error",
                    "Access denied: admin role required"));
            return null;
        }
        try {
            review.setProduct(getProductById(review.getProductId()));
            review.setCustomer(getCustomerById(review.getCustomerId()));
            review.setReviewDate(Date.from(Instant.now()));
            if (review.getReviewId() == null) {
                logger.info("Persisting new review");
                em.persist(review);
            } else {
                logger.info("Merging existing review with ID: {}", review.getReviewId());
                em.merge(review);
            }
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
            Review toRemove = em.find(Review.class, r.getReviewId());
            if (toRemove != null) {
                em.remove(toRemove);
            }
            reviews = null;
        } catch (Exception e) {
            logger.error("Error deleting review: {}", e.getMessage());
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                    "An error occurred while deleting the review."));
        }
        return null;
    }
}
