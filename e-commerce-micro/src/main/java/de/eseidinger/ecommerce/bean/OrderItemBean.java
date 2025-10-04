package de.eseidinger.ecommerce.bean;

import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.eseidinger.ecommerce.model.OrderItem;
import de.eseidinger.ecommerce.service.OrderItemService;

@Named("orderItemBean")
@ViewScoped
public class OrderItemBean implements Serializable {

  private static final Logger logger = LoggerFactory.getLogger(OrderItemBean.class);

  private static final long serialVersionUID = 1L;

  private OrderItem orderItem = new OrderItem();
  private List<OrderItem> orderItems;

  @Inject private OrderItemService orderItemService;

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
    this.orderItem.setOrderId(oi.getOrder().getOrderId());
    this.orderItem.setProductId(oi.getProduct().getProductId());
    return null;
  }

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
