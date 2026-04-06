package de.eseidinger.ecommerce.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.eseidinger.ecommerce.exception.ValidationException;
import de.eseidinger.ecommerce.model.Customer;
import de.eseidinger.ecommerce.model.Order;
import de.eseidinger.ecommerce.repository.CustomerRepository;
import de.eseidinger.ecommerce.repository.OrderRepository;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

  @Mock private OrderRepository orderRepository;

  @Mock private CustomerRepository customerRepository;

  @InjectMocks private OrderService orderService;

  @Test
  void saveShouldAttachCustomerAndPersistValidOrder() {
    Order order = new Order();
    order.setOrderDate(new Date());
    order.setTotalAmount(89.90);
    order.setCustomerId(3L);

    Customer customer = new Customer();
    customer.setCustomerId(3L);
    customer.setName("Alice");

    when(customerRepository.findById(3L)).thenReturn(customer);

    orderService.save(order);

    verify(customerRepository).findById(3L);
    verify(orderRepository).save(order);
  }

  @Test
  void saveShouldThrowWhenCustomerDoesNotExist() {
    Order order = new Order();
    order.setOrderDate(new Date());
    order.setTotalAmount(89.90);
    order.setCustomerId(99L);

    when(customerRepository.findById(99L)).thenReturn(null);

    assertThrows(ValidationException.class, () -> orderService.save(order));
    verify(orderRepository, never()).save(order);
  }

  @Test
  void saveShouldThrowWhenOrderDateIsMissing() {
    Order order = new Order();
    order.setTotalAmount(89.90);
    order.setCustomerId(3L);

    assertThrows(ValidationException.class, () -> orderService.save(order));
    verify(orderRepository, never()).save(order);
  }
}
