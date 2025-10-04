package de.eseidinger.ecommerce.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import de.eseidinger.ecommerce.dto.OrderDto;

@Entity
@Table(name = "orders")
public class Order implements Serializable {

  public static Order fromDto(OrderDto dto) {
    Order order = new Order();
    order.setOrderId(dto.orderId());
    if (dto.orderDate() != null) {
      order.setOrderDate(Date.from(dto.orderDate().toInstant()));
    }
    order.setTotalAmount(dto.totalAmount());
    order.setCustomerId(dto.customerId());
    return order;
  }

  public static OrderDto toDto(Order order) {
    Long customerId = order.getCustomer() != null ? order.getCustomer().getCustomerId() : null;
    OffsetDateTime orderDate =
        order.getOrderDate() != null
            ? order.getOrderDate().toInstant().atZone(ZoneId.of("UTC")).toOffsetDateTime()
            : null;
    return new OrderDto(order.getOrderId(), orderDate, customerId, order.getTotalAmount());
  }

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "order_id")
  private Long orderId;

  @ManyToOne
  @JoinColumn(name = "customer_id")
  private Customer customer;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "order_date")
  private Date orderDate;

  @Column(name = "total_amount")
  private Double totalAmount;

  @OneToMany(mappedBy = "order")
  private List<OrderItem> orderItems;

  @Transient private Long customerId;

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public Customer getCustomer() {
    return customer;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
    this.customerId = customer != null ? customer.getCustomerId() : null;
  }

  public Date getOrderDate() {
    return orderDate;
  }

  public void setOrderDate(Date orderDate) {
    this.orderDate = orderDate;
  }

  public Double getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(Double totalAmount) {
    this.totalAmount = totalAmount;
  }

  public List<OrderItem> getOrderItems() {
    return orderItems;
  }

  public void setOrderItems(List<OrderItem> orderItems) {
    this.orderItems = orderItems;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }
}
