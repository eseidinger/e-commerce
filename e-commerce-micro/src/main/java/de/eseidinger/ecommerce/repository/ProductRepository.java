package de.eseidinger.ecommerce.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;

import de.eseidinger.ecommerce.model.Product;

@ApplicationScoped
public class ProductRepository {
  @PersistenceContext(unitName = "ecommercePU")
  private EntityManager em;

  public List<Product> findAll() {
    TypedQuery<Product> query = em.createQuery("SELECT p FROM Product p", Product.class);
    return query.getResultList();
  }

  public Product findById(Long id) {
    return em.find(Product.class, id);
  }

  public void save(Product product) {
    if (product.getProductId() == null) {
      em.persist(product);
    } else {
      em.merge(product);
    }
  }

  public void delete(Long id) {
    Product toRemove = em.find(Product.class, id);
    if (toRemove != null) {
      em.remove(toRemove);
    }
  }
}
