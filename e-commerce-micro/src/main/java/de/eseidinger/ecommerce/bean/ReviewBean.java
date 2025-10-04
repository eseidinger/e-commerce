package de.eseidinger.ecommerce.bean;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.eseidinger.ecommerce.model.Review;
import de.eseidinger.ecommerce.service.ReviewService;

@Named("reviewBean")
@ViewScoped
public class ReviewBean implements Serializable {

  private static final Logger logger = LoggerFactory.getLogger(ReviewBean.class);

  private static final long serialVersionUID = 1L;

  private Review review = new Review();
  private List<Review> reviews;

  @Inject private ReviewService reviewService;

  public Review getReview() {
    return review;
  }

  public void setReview(Review review) {
    this.review = review;
  }

  public List<Review> getReviews() {
    try {
      if (reviews == null) {
        logger.info("Loading review list from service");
        reviews = reviewService.findAll();
      }
      return reviews;
    } catch (Exception e) {
      logger.error("Error loading reviews: {}", e.getMessage());
      return null;
    }
  }

  public String save() {
    FacesContext context = FacesContext.getCurrentInstance();
    if (!context.getExternalContext().isUserInRole("admin")) {
      logger.warn("Unauthorized save attempt by user without admin role");
      context.addMessage(
          null,
          new FacesMessage(
              FacesMessage.SEVERITY_ERROR, "Security Error", "Access denied: admin role required"));
      return null;
    }
    try {
      reviewService.save(review);
      review = new Review();
      reviews = null;
    } catch (Exception e) {
      logger.error("Error saving review: {}", e.getMessage());
      context.addMessage(
          null,
          new FacesMessage(
              FacesMessage.SEVERITY_ERROR, "Error", "An error occurred while saving the review."));
    }
    return null;
  }

  public String edit(Review r) {
    logger.info("Editing review with ID: {}", r.getReviewId());
    FacesContext context = FacesContext.getCurrentInstance();
    if (!context.getExternalContext().isUserInRole("admin")) {
      logger.warn("Unauthorized edit attempt by user without admin role");
      context.addMessage(
          null,
          new FacesMessage(
              FacesMessage.SEVERITY_ERROR, "Security Error", "Access denied: admin role required"));
      return null;
    }
    this.review = r;
    this.review.setCustomerId(r.getCustomer().getCustomerId());
    this.review.setProductId(r.getProduct().getProductId());
    return null;
  }

  public String delete(Review r) {
    FacesContext context = FacesContext.getCurrentInstance();
    if (!context.getExternalContext().isUserInRole("admin")) {
      logger.warn("Unauthorized delete attempt by user without admin role");
      context.addMessage(
          null,
          new FacesMessage(
              FacesMessage.SEVERITY_ERROR, "Security Error", "Access denied: admin role required"));
      return null;
    }
    try {
      logger.info("Deleting review with ID: {}", r.getReviewId());
      reviewService.delete(r.getReviewId());
      reviews = null;
    } catch (Exception e) {
      logger.error("Error deleting review: {}", e.getMessage());
      context.addMessage(
          null,
          new FacesMessage(
              FacesMessage.SEVERITY_ERROR,
              "Error",
              "An error occurred while deleting the review."));
    }
    return null;
  }

  public String getUtcDateInLocalDateFormat(Review review) {
    if (review.getReviewDate() == null) {
      return "";
    }
    LocalDateTime utcDateTime =
        LocalDateTime.ofInstant(review.getReviewDate().toInstant(), ZoneOffset.UTC);
    DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
    return utcDateTime.format(formatter);
  }
}
