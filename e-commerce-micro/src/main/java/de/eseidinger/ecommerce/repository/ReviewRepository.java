package de.eseidinger.ecommerce.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;

import de.eseidinger.ecommerce.model.Review;

@ApplicationScoped
public class ReviewRepository {
  @PersistenceContext(unitName = "ecommercePU")
  private EntityManager em;

  public List<Review> findAll() {
    TypedQuery<Review> query = em.createQuery("SELECT r FROM Review r", Review.class);
    return query.getResultList();
  }

  public Review findById(Long id) {
    return em.find(Review.class, id);
  }

  public void save(Review review) {
    if (review.getReviewId() == null) {
      em.persist(review);
    } else {
      em.merge(review);
    }
  }

  public void delete(Long id) {
    Review toRemove = em.find(Review.class, id);
    if (toRemove != null) {
      em.remove(toRemove);
    }
  }
}
