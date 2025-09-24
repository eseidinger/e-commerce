
package com.ecommerce.jsf.bean;

import jakarta.faces.context.FacesContext;
import jakarta.faces.application.FacesMessage;

import com.ecommerce.jsf.model.Order;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import jakarta.faces.view.ViewScoped;

import com.ecommerce.jsf.repository.CustomerRepository;
import com.ecommerce.jsf.repository.OrderRepository;
import jakarta.inject.Inject;
import java.io.Serializable;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("orderBean")
@ViewScoped
public class OrderBean implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(OrderBean.class);

    private static final long serialVersionUID = 1L;

    private Order order = new Order();
    private List<Order> orders;

    @Inject
    private OrderRepository orderRepository;

    @Inject
    private CustomerRepository customerRepository;

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
                orders = orderRepository.findAll();
            }
            return orders;
        } catch (Exception e) {
            logger.error("Error loading orders: {}", e.getMessage());
            return null;
        }
    }

    @Transactional
    public String save() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (!context.getExternalContext().isUserInRole("admin")) {
            logger.warn("Unauthorized save attempt by user without admin role");
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Security Error",
                    "Access denied: admin role required"));
            return null;
        }
        try {
            order.setCustomer(customerRepository.findById(order.getCustomerId()));
            orderRepository.save(order);
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

    @Transactional
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
            orderRepository.delete(o.getOrderId());
            orders = null;
        } catch (Exception e) {
            logger.error("Error deleting order: {}", e.getMessage());
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                    "An error occurred while deleting the order."));
        }
        return null;
    }
}
