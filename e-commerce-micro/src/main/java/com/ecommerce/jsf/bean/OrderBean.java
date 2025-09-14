package com.ecommerce.jsf.bean;

import com.ecommerce.jsf.model.Order;
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import jakarta.transaction.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

@Named("orderBean")
@ViewScoped
@Transactional
public class OrderBean implements Serializable {
    private static final Logger logger = Logger.getLogger(OrderBean.class.getName());
    private static final long serialVersionUID = 1L;
    private Order order = new Order();
    private List<Order> orders;
    @PersistenceContext(unitName = "ecommercePU")
    private EntityManager em;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<Order> getOrders() {
        try {
            if (orders == null) {
                logger.info("Loading order list from database");
                TypedQuery<Order> query = em.createQuery("SELECT o FROM Order o", Order.class);
                orders = query.getResultList();
            }
            return orders;
        } catch (Exception e) {
            logger.severe("Error loading orders: " + e.getMessage());
            return null;
        }
    }

    public String save() {
        try {
            if (order.getOrderId() == null) {
                logger.info("Persisting new order");
                em.persist(order);
            } else {
                logger.info("Merging existing order with ID: " + order.getOrderId());
                em.merge(order);
            }
            order = new Order();
            orders = null;
        } catch (Exception e) {
            logger.severe("Error saving order: " + e.getMessage());
        }
        return null;
    }

    public String edit(Order o) {
        logger.info("Editing order with ID: " + o.getOrderId());
        this.order = o;
        return null;
    }

    public String delete(Order o) {
        try {
            logger.info("Deleting order with ID: " + o.getOrderId());
            Order toRemove = em.find(Order.class, o.getOrderId());
            if (toRemove != null) {
                em.remove(toRemove);
            }
            orders = null;
        } catch (Exception e) {
            logger.severe("Error deleting order: " + e.getMessage());
        }
        return null;
    }
}
