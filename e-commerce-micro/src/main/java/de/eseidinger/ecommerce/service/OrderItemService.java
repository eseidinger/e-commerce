package de.eseidinger.ecommerce.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

import de.eseidinger.ecommerce.exception.ValidationException;
import de.eseidinger.ecommerce.model.Order;
import de.eseidinger.ecommerce.model.OrderItem;
import de.eseidinger.ecommerce.model.Product;
import de.eseidinger.ecommerce.repository.OrderItemRepository;
import de.eseidinger.ecommerce.repository.OrderRepository;
import de.eseidinger.ecommerce.repository.ProductRepository;
import de.eseidinger.ecommerce.util.InputValidator;

@ApplicationScoped
public class OrderItemService {

  @Inject private OrderItemRepository orderItemRepository;

  @Inject private ProductRepository productRepository;

  @Inject private OrderRepository orderRepository;

  public List<OrderItem> findAll() {
    return orderItemRepository.findAll();
  }

  public OrderItem findById(Long id) {
    return orderItemRepository.findById(id);
  }

  @Transactional
  public void save(OrderItem orderItem) {
    if (orderItem.getQuantity() == null || orderItem.getQuantity() <= 0) {
      throw new ValidationException("Quantity must be greater than zero");
    }
    if (!InputValidator.isValidPrice(orderItem.getPrice())) {
      throw new ValidationException("Invalid price");
    }
    if (orderItem.getOrderId() == null) {
      throw new ValidationException("Order ID cannot be null");
    }
    if (orderItem.getProductId() == null) {
      throw new ValidationException("Product ID cannot be null");
    }
    Product product = productRepository.findById(orderItem.getProductId());
    if (product == null) {
      throw new ValidationException("Product not found for id: " + orderItem.getProductId());
    }
    orderItem.setProduct(product);
    Order order = orderRepository.findById(orderItem.getOrderId());
    if (order == null) {
      throw new ValidationException("Order not found for id: " + orderItem.getOrderId());
    }
    orderItem.setOrder(order);
    orderItemRepository.save(orderItem);
  }

  @Transactional
  public void delete(Long id) {
    orderItemRepository.delete(id);
  }
}
