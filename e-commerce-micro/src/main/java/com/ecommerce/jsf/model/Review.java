package com.ecommerce.jsf.model;

import com.ecommerce.jsf.dto.ReviewDto;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
public class Review implements Serializable {

  private static final long serialVersionUID = 1L;

  public static Review fromDto(ReviewDto dto) {
    Review review = new Review();
    review.setReviewId(dto.reviewId());
    review.setProductId(dto.productId());
    review.setCustomerId(dto.customerId());
    review.setRating(dto.rating());
    review.setComment(dto.comment());
    if (dto.reviewDate() != null) {
      review.setReviewDate(Date.from(dto.reviewDate().toInstant()));
    } else {
      review.setReviewDate(null);
    }
    return review;
  }

  public static ReviewDto toDto(Review review) {
    Long productId = review.getProduct() != null ? review.getProduct().getProductId() : null;
    Long customerId = review.getCustomer() != null ? review.getCustomer().getCustomerId() : null;
    return new ReviewDto(
        review.getReviewId(),
        productId,
        customerId,
        review.getRating(),
        review.getComment(),
        review.getReviewDate() != null
            ? review
                .getReviewDate()
                .toInstant()
                .atZone(java.time.ZoneId.of("UTC"))
                .toOffsetDateTime()
            : null);
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "review_id")
  private Long reviewId;

  @ManyToOne
  @JoinColumn(name = "product_id")
  private Product product;

  @ManyToOne
  @JoinColumn(name = "customer_id")
  private Customer customer;

  @Column(name = "rating")
  private Integer rating;

  @Column(name = "comment")
  private String comment;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "review_date")
  private Date reviewDate;

  @Transient private Long productId;

  @Transient private Long customerId;

  public Long getReviewId() {
    return reviewId;
  }

  public void setReviewId(Long reviewId) {
    this.reviewId = reviewId;
  }

  public Product getProduct() {
    return product;
  }

  public void setProduct(Product product) {
    this.product = product;
    this.productId = (product != null) ? product.getProductId() : null;
  }

  public Customer getCustomer() {
    return customer;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
    this.customerId = (customer != null) ? customer.getCustomerId() : null;
  }

  public Integer getRating() {
    return rating;
  }

  public void setRating(Integer rating) {
    this.rating = rating;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public Date getReviewDate() {
    return reviewDate;
  }

  public void setReviewDate(Date reviewDate) {
    this.reviewDate = reviewDate;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }
}
