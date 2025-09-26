
package com.ecommerce.jsf.bean;

import jakarta.faces.context.FacesContext;

import com.ecommerce.jsf.model.Order;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import jakarta.faces.view.ViewScoped;

import com.ecommerce.jsf.service.OrderService;
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
    private OrderService orderService;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<Order> getOrders() {
        if (orders == null) {
            logger.info("Loading order list from service");
            orders = orderService.findAll();
        }
        return orders;
    }

    @Transactional
    public String save() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (!context.getExternalContext().isUserInRole("admin")) {
            logger.warn("Unauthorized save attempt by user without admin role");
            throw new SecurityException("Access denied: admin role required");
        }
        logger.info("Saving order: {}", order);
        orderService.save(order);
        order = new Order();
        orders = null;
        return null;
    }

    public String edit(Order o) {
        logger.info("Editing order with ID: {}", o.getOrderId());
        FacesContext context = FacesContext.getCurrentInstance();
        if (!context.getExternalContext().isUserInRole("admin")) {
            throw new SecurityException("Access denied: admin role required");
        }
        this.order = o;
        return null;
    }

    @Transactional
    public String delete(Order o) {
        FacesContext context = FacesContext.getCurrentInstance();
        if (!context.getExternalContext().isUserInRole("admin")) {
            throw new SecurityException("Access denied: admin role required");
        }
        logger.info("Deleting order with ID: {}", o.getOrderId());
        orderService.delete(o.getOrderId());
        orders = null;
        return null;
    }
}
