package de.eseidinger.ecommerce.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import de.eseidinger.ecommerce.exception.ValidationException;
import de.eseidinger.ecommerce.model.Customer;
import de.eseidinger.ecommerce.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

  @Mock private CustomerRepository customerRepository;

  @InjectMocks private CustomerService customerService;

  @Test
  void saveShouldPersistValidCustomer() {
    Customer customer = new Customer();
    customer.setName("John Doe");
    customer.setEmail("john.doe@example.com");
    customer.setAddress("123 Main Street");

    customerService.save(customer);

    verify(customerRepository).save(customer);
  }

  @Test
  void saveShouldThrowForInvalidEmail() {
    Customer customer = new Customer();
    customer.setName("John Doe");
    customer.setEmail("invalid-email");
    customer.setAddress("123 Main Street");

    assertThrows(ValidationException.class, () -> customerService.save(customer));
    verify(customerRepository, never()).save(customer);
  }

  @Test
  void deleteShouldDelegateToRepository() {
    customerService.delete(42L);

    verify(customerRepository).delete(42L);
  }
}
