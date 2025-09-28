package com.ecommerce.jsf.service;

import com.ecommerce.jsf.exception.ValidationException;
import com.ecommerce.jsf.model.Customer;
import com.ecommerce.jsf.model.Order;
import com.ecommerce.jsf.repository.CustomerRepository;
import com.ecommerce.jsf.repository.OrderRepository;
import com.ecommerce.jsf.util.InputValidator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

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
