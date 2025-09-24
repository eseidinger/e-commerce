
package com.ecommerce.jsf.bean;

import jakarta.faces.context.FacesContext;
import jakarta.faces.application.FacesMessage;

import com.ecommerce.jsf.model.Customer;
import com.ecommerce.jsf.model.Order;
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

@Named("orderBean")
@ViewScoped
@Transactional
public class OrderBean implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(OrderBean.class);

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
            logger.error("Error loading orders: {}", e.getMessage());
            return null;
        }
    }

    private Customer getCustomerById(Long customerId) {
        try {
            return em.find(Customer.class, customerId);
        } catch (Exception e) {
            logger.error("Error finding customer with ID {}: {}", customerId, e.getMessage());
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
            if (order.getOrderId() == null) {
                logger.info("Persisting new order");
                Customer customer = getCustomerById(order.getCustomerId());
                order.setCustomer(customer);
                em.persist(order);
            } else {
                logger.info("Merging existing order with ID: {}", order.getOrderId());
                em.merge(order);
            }
            order = new Order();
            orders = null;
        } catch (Exception e) {
            logger.error("Error saving order: {}", e.getMessage());
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                    "An error occurred while saving the order."));
        }
        return null;
    }

    public String edit(Order o) {
        logger.info("Editing order with ID: {}", o.getOrderId());
        FacesContext context = FacesContext.getCurrentInstance();
        if (!context.getExternalContext().isUserInRole("admin")) {
            logger.warn("Unauthorized edit attempt by user without admin role");
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Security Error",
                    "Access denied: admin role required"));
            return null;
        }
        this.order = o;
        return null;
    }

    public String delete(Order o) {
        FacesContext context = FacesContext.getCurrentInstance();
        if (!context.getExternalContext().isUserInRole("admin")) {
            logger.warn("Unauthorized delete attempt by user without admin role");
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Security Error",
                    "Access denied: admin role required"));
            return null;
        }
        try {
            logger.info("Deleting order with ID: {}", o.getOrderId());
            Order toRemove = em.find(Order.class, o.getOrderId());
            if (toRemove != null) {
                em.remove(toRemove);
            }
            orders = null;
        } catch (Exception e) {
            logger.error("Error deleting order: {}", e.getMessage());
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                    "An error occurred while deleting the order."));
        }
        return null;
    }
}
