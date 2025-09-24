package com.ecommerce.jsf.repository;

import com.ecommerce.jsf.model.OrderItem;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;

@ApplicationScoped
public class OrderItemRepository {
    @PersistenceContext(unitName = "ecommercePU")
    private EntityManager em;

    public List<OrderItem> findAll() {
        TypedQuery<OrderItem> query = em.createQuery("SELECT oi FROM OrderItem oi", OrderItem.class);
        return query.getResultList();
    }

    public OrderItem findById(Long id) {
        return em.find(OrderItem.class, id);
    }

    public void save(OrderItem orderItem) {
        if (orderItem.getOrderItemId() == null) {
            em.persist(orderItem);
        } else {
            em.merge(orderItem);
        }
    }

    public void delete(Long id) {
        OrderItem toRemove = em.find(OrderItem.class, id);
        if (toRemove != null) {
            em.remove(toRemove);
        }
    }
}
