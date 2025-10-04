package de.eseidinger.ecommerce.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;

import de.eseidinger.ecommerce.model.Customer;

@ApplicationScoped
public class CustomerRepository {
  @PersistenceContext(unitName = "ecommercePU")
  private EntityManager em;

  public List<Customer> findAll() {
    TypedQuery<Customer> query = em.createQuery("SELECT c FROM Customer c", Customer.class);
    return query.getResultList();
  }

  public Customer findById(Long id) {
    return em.find(Customer.class, id);
  }

  public void save(Customer customer) {
    if (customer.getCustomerId() == null) {
      em.persist(customer);
    } else {
      em.merge(customer);
    }
  }

  public void delete(Long id) {
    Customer toRemove = em.find(Customer.class, id);
    if (toRemove != null) {
      em.remove(toRemove);
    }
  }
}
