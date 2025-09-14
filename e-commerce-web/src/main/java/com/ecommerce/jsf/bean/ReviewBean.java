package com.ecommerce.jsf.bean;

import com.ecommerce.jsf.model.Review;
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import jakarta.transaction.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

@Named("reviewBean")
@ViewScoped
@Transactional
public class ReviewBean implements Serializable {
    private static final Logger logger = Logger.getLogger(ReviewBean.class.getName());
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
            logger.severe("Error loading reviews: " + e.getMessage());
            return null;
        }
    }

    public String save() {
        try {
            if (review.getReviewId() == null) {
                logger.info("Persisting new review");
                em.persist(review);
            } else {
                logger.info("Merging existing review with ID: " + review.getReviewId());
                em.merge(review);
            }
            review = new Review();
            reviews = null;
        } catch (Exception e) {
            logger.severe("Error saving review: " + e.getMessage());
        }
        return null;
    }

    public String edit(Review r) {
        logger.info("Editing review with ID: " + r.getReviewId());
        this.review = r;
        return null;
    }

    public String delete(Review r) {
        try {
            logger.info("Deleting review with ID: " + r.getReviewId());
            Review toRemove = em.find(Review.class, r.getReviewId());
            if (toRemove != null) {
                em.remove(toRemove);
            }
            reviews = null;
        } catch (Exception e) {
            logger.severe("Error deleting review: " + e.getMessage());
        }
        return null;
    }
}
