package com.ecommerce.jsf.bean;

import com.ecommerce.jsf.model.OrderItem;
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import jakarta.transaction.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

@Named("orderItemBean")
@ViewScoped
@Transactional
public class OrderItemBean implements Serializable {
    private static final Logger logger = Logger.getLogger(OrderItemBean.class.getName());
    private static final long serialVersionUID = 1L;
    private OrderItem orderItem = new OrderItem();
    private List<OrderItem> orderItems;
    @PersistenceContext(unitName = "ecommercePU")
    private EntityManager em;

    public OrderItem getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }

    public List<OrderItem> getOrderItems() {
        try {
            if (orderItems == null) {
                logger.info("Loading order item list from database");
                TypedQuery<OrderItem> query = em.createQuery("SELECT oi FROM OrderItem oi", OrderItem.class);
                orderItems = query.getResultList();
            }
            return orderItems;
        } catch (Exception e) {
            logger.severe("Error loading order items: " + e.getMessage());
            return null;
        }
    }

    public String save() {
        try {
            if (orderItem.getOrderItemId() == null) {
                logger.info("Persisting new order item");
                em.persist(orderItem);
            } else {
                logger.info("Merging existing order item with ID: " + orderItem.getOrderItemId());
                em.merge(orderItem);
            }
            orderItem = new OrderItem();
            orderItems = null;
        } catch (Exception e) {
            logger.severe("Error saving order item: " + e.getMessage());
        }
        return null;
    }

    public String edit(OrderItem oi) {
        logger.info("Editing order item with ID: " + oi.getOrderItemId());
        this.orderItem = oi;
        return null;
    }

    public String delete(OrderItem oi) {
        try {
            logger.info("Deleting order item with ID: " + oi.getOrderItemId());
            OrderItem toRemove = em.find(OrderItem.class, oi.getOrderItemId());
            if (toRemove != null) {
                em.remove(toRemove);
            }
            orderItems = null;
        } catch (Exception e) {
            logger.severe("Error deleting order item: " + e.getMessage());
        }
        return null;
    }
}
