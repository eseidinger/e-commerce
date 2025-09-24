
package com.ecommerce.jsf.bean;

import jakarta.faces.context.FacesContext;
import jakarta.faces.application.FacesMessage;

import com.ecommerce.jsf.model.OrderItem;
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import jakarta.transaction.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("orderItemBean")
@ViewScoped
@Transactional
public class OrderItemBean implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(OrderItemBean.class);
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
            logger.error("Error loading order items: {}", e.getMessage());
            return null;
        }
    }

    public String save() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (!context.getExternalContext().isUserInRole("admin")) {
            logger.warn("Unauthorized save attempt by user without admin role");
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Security Error",
                    "Access denied: admin role required"));
            return null;
        }
        try {
            if (orderItem.getOrderItemId() == null) {
                logger.info("Persisting new order item");
                em.persist(orderItem);
            } else {
                logger.info("Merging existing order item with ID: {}", orderItem.getOrderItemId());
                em.merge(orderItem);
            }
            orderItem = new OrderItem();
            orderItems = null;
        } catch (Exception e) {
            logger.error("Error saving order item: {}", e.getMessage());
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                    "An error occurred while saving the order item."));
        }
        return null;
    }

    public String edit(OrderItem oi) {
        logger.info("Editing order item with ID: {}", oi.getOrderItemId());
        FacesContext context = FacesContext.getCurrentInstance();
        if (!context.getExternalContext().isUserInRole("admin")) {
            logger.warn("Unauthorized edit attempt by user without admin role");
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Security Error",
                    "Access denied: admin role required"));
            return null;
        }
        this.orderItem = oi;
        return null;
    }

    public String delete(OrderItem oi) {
        FacesContext context = FacesContext.getCurrentInstance();
        if (!context.getExternalContext().isUserInRole("admin")) {
            logger.warn("Unauthorized delete attempt by user without admin role");
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Security Error",
                    "Access denied: admin role required"));
            return null;
        }
        try {
            logger.info("Deleting order item with ID: {}", oi.getOrderItemId());
            OrderItem toRemove = em.find(OrderItem.class, oi.getOrderItemId());
            if (toRemove != null) {
                em.remove(toRemove);
            }
            orderItems = null;
        } catch (Exception e) {
            logger.error("Error deleting order item: {}", e.getMessage());
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                    "An error occurred while deleting the order item."));
        }
        return null;
    }
}
