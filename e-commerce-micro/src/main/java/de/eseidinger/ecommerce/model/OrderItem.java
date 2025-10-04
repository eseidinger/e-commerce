package de.eseidinger.ecommerce.model;

import jakarta.persistence.*;
import java.io.Serializable;

import de.eseidinger.ecommerce.dto.OrderItemDto;

@Entity
@Table(name = "order_item")
public class OrderItem implements Serializable {

  private static final long serialVersionUID = 1L;

  public static OrderItem fromDto(OrderItemDto dto) {
    OrderItem orderItem = new OrderItem();
    orderItem.setOrderItemId(dto.orderItemId());
    orderItem.setOrderId(dto.orderId());
    orderItem.setProductId(dto.productId());
    orderItem.setQuantity(dto.quantity());
    orderItem.setPrice(dto.price());
    return orderItem;
  }

  public static OrderItemDto toDto(OrderItem orderItem) {
    return new OrderItemDto(
        orderItem.getOrderItemId(),
        (orderItem.getOrder() != null) ? orderItem.getOrder().getOrderId() : null,
        (orderItem.getProduct() != null) ? orderItem.getProduct().getProductId() : null,
        orderItem.getQuantity(),
        orderItem.getPrice());
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "order_item_id")
  private Long orderItemId;

  @ManyToOne
  @JoinColumn(name = "order_id")
  private Order order;

  @ManyToOne
  @JoinColumn(name = "product_id")
  private Product product;

  @Column(name = "quantity")
  private Integer quantity;

  @Column(name = "price")
  private Double price;

  @Transient private Long orderId;

  @Transient private Long productId;

  public Long getOrderItemId() {
    return orderItemId;
  }

  public void setOrderItemId(Long orderItemId) {
    this.orderItemId = orderItemId;
  }

  public Order getOrder() {
    return order;
  }

  public void setOrder(Order order) {
    this.order = order;
    this.orderId = (order != null) ? order.getOrderId() : null;
  }

  public Product getProduct() {
    return product;
  }

  public void setProduct(Product product) {
    this.product = product;
    this.productId = (product != null) ? product.getProductId() : null;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }
}
