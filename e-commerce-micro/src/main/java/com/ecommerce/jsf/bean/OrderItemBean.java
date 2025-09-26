
package com.ecommerce.jsf.bean;

import jakarta.faces.context.FacesContext;

import com.ecommerce.jsf.model.OrderItem;

import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import jakarta.faces.view.ViewScoped;
import com.ecommerce.jsf.service.OrderItemService;

import jakarta.inject.Inject;
import java.io.Serializable;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named("orderItemBean")
@ViewScoped
public class OrderItemBean implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(OrderItemBean.class);

    private static final long serialVersionUID = 1L;

    private OrderItem orderItem = new OrderItem();
    private List<OrderItem> orderItems;

    @Inject
    private OrderItemService orderItemService;

    public OrderItem getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }

    public List<OrderItem> getOrderItems() {
        if (orderItems == null) {
            logger.info("Loading order item list from service");
            orderItems = orderItemService.findAll();
        }
        return orderItems;
    }

    @Transactional
    public String save() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (!context.getExternalContext().isUserInRole("admin")) {
            logger.warn("Unauthorized save attempt by user without admin role");
            throw new SecurityException("Access denied: admin role required");
        }
        logger.info("Saving order item: {}", orderItem);
        orderItemService.save(orderItem);
        orderItem = new OrderItem();
        orderItems = null;
        return null;
    }

    public String edit(OrderItem oi) {
        logger.info("Editing order item with ID: {}", oi.getOrderItemId());
        FacesContext context = FacesContext.getCurrentInstance();
        if (!context.getExternalContext().isUserInRole("admin")) {
            logger.warn("Unauthorized edit attempt by user without admin role");
            throw new SecurityException("Access denied: admin role required");
        }
        this.orderItem = oi;
        return null;
    }

    @Transactional
    public String delete(OrderItem oi) {
        FacesContext context = FacesContext.getCurrentInstance();
        if (!context.getExternalContext().isUserInRole("admin")) {
            logger.warn("Unauthorized delete attempt by user without admin role");
            throw new SecurityException("Access denied: admin role required");
        }
        logger.info("Deleting order item with ID: {}", oi.getOrderItemId());
        orderItemService.delete(oi.getOrderItemId());
        orderItems = null;
        return null;
    }
}
