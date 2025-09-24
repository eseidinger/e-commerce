
package com.ecommerce.jsf.bean;

import jakarta.faces.context.FacesContext;
import jakarta.faces.application.FacesMessage;

import com.ecommerce.jsf.model.OrderItem;

import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import jakarta.faces.view.ViewScoped;
import com.ecommerce.jsf.repository.OrderItemRepository;
import com.ecommerce.jsf.repository.OrderRepository;
import com.ecommerce.jsf.repository.ProductRepository;

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
    private OrderItemRepository orderItemRepository;

    @Inject
    private ProductRepository productRepository;

    @Inject
    private OrderRepository orderRepository;

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
                orderItems = orderItemRepository.findAll();
            }
            return orderItems;
        } catch (Exception e) {
            logger.error("Error loading order items: {}", e.getMessage());
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
            orderItem.setOrder(orderRepository.findById(orderItem.getOrderId()));
            orderItem.setProduct(productRepository.findById(orderItem.getProductId()));
            orderItemRepository.save(orderItem);
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

    @Transactional
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
            orderItemRepository.delete(oi.getOrderItemId());
            orderItems = null;
        } catch (Exception e) {
            logger.error("Error deleting order item: {}", e.getMessage());
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                    "An error occurred while deleting the order item."));
        }
        return null;
    }
}
