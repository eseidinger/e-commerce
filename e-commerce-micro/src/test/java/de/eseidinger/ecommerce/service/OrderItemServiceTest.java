package de.eseidinger.ecommerce.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.eseidinger.ecommerce.exception.ValidationException;
import de.eseidinger.ecommerce.model.Order;
import de.eseidinger.ecommerce.model.OrderItem;
import de.eseidinger.ecommerce.model.Product;
import de.eseidinger.ecommerce.repository.OrderItemRepository;
import de.eseidinger.ecommerce.repository.OrderRepository;
import de.eseidinger.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderItemServiceTest {

  @Mock private OrderItemRepository orderItemRepository;

  @Mock private ProductRepository productRepository;

  @Mock private OrderRepository orderRepository;

  @InjectMocks private OrderItemService orderItemService;

  @Test
  void saveShouldAttachProductAndOrderAndPersistValidOrderItem() {
    OrderItem orderItem = new OrderItem();
    orderItem.setQuantity(2);
    orderItem.setPrice(19.99);
    orderItem.setOrderId(10L);
    orderItem.setProductId(20L);

    Product product = new Product();
    product.setProductId(20L);
    when(productRepository.findById(20L)).thenReturn(product);

    Order order = new Order();
    order.setOrderId(10L);
    when(orderRepository.findById(10L)).thenReturn(order);

    orderItemService.save(orderItem);

    verify(productRepository).findById(20L);
    verify(orderRepository).findById(10L);
    verify(orderItemRepository).save(orderItem);
  }

  @Test
  void saveShouldThrowWhenQuantityIsInvalid() {
    OrderItem orderItem = new OrderItem();
    orderItem.setQuantity(0);
    orderItem.setPrice(19.99);
    orderItem.setOrderId(10L);
    orderItem.setProductId(20L);

    assertThrows(ValidationException.class, () -> orderItemService.save(orderItem));
    verify(orderItemRepository, never()).save(orderItem);
  }

  @Test
  void saveShouldThrowWhenProductDoesNotExist() {
    OrderItem orderItem = new OrderItem();
    orderItem.setQuantity(1);
    orderItem.setPrice(19.99);
    orderItem.setOrderId(10L);
    orderItem.setProductId(20L);

    when(productRepository.findById(20L)).thenReturn(null);

    assertThrows(ValidationException.class, () -> orderItemService.save(orderItem));
    verify(orderRepository, never()).findById(10L);
    verify(orderItemRepository, never()).save(orderItem);
  }
}
