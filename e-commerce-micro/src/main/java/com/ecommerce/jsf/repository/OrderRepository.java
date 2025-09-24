package com.ecommerce.jsf.repository;

import com.ecommerce.jsf.model.Order;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;

@ApplicationScoped
public class OrderRepository {
    @PersistenceContext(unitName = "ecommercePU")
    private EntityManager em;

    public List<Order> findAll() {
        TypedQuery<Order> query = em.createQuery("SELECT o FROM Order o", Order.class);
        return query.getResultList();
    }

    public Order findById(Long id) {
        return em.find(Order.class, id);
    }

    public void save(Order order) {
        if (order.getOrderId() == null) {
            em.persist(order);
        } else {
            em.merge(order);
        }
    }

    public void delete(Long id) {
        Order toRemove = em.find(Order.class, id);
        if (toRemove != null) {
            em.remove(toRemove);
        }
    }
}
