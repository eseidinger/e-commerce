package de.eseidinger.ecommerce.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

import de.eseidinger.ecommerce.exception.ValidationException;
import de.eseidinger.ecommerce.model.Customer;
import de.eseidinger.ecommerce.model.Order;
import de.eseidinger.ecommerce.repository.CustomerRepository;
import de.eseidinger.ecommerce.repository.OrderRepository;
import de.eseidinger.ecommerce.util.InputValidator;

@ApplicationScoped
public class OrderService {

  @Inject private OrderRepository orderRepository;

  @Inject private CustomerRepository customerRepository;

  public List<Order> findAll() {
    return orderRepository.findAll();
  }

  public Order findById(Long id) {
    return orderRepository.findById(id);
  }

  @Transactional
  public void save(Order order) {
    if (!InputValidator.isValidPrice(order.getTotalAmount())) {
      throw new ValidationException("Invalid total amount");
    }
    if (order.getOrderDate() == null) {
      throw new ValidationException("Order date cannot be null");
    }
    if (order.getCustomerId() == null) {
      throw new ValidationException("Customer ID cannot be null");
    }
    Customer customer = customerRepository.findById(order.getCustomerId());
    if (customer == null) {
      throw new ValidationException("Customer not found for id: " + order.getCustomerId());
    }
    order.setCustomer(customer);
    orderRepository.save(order);
  }

  @Transactional
  public void delete(Long id) {
    orderRepository.delete(id);
  }
}
