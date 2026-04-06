package de.eseidinger.ecommerce.bean;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.eseidinger.ecommerce.model.Customer;
import de.eseidinger.ecommerce.service.CustomerService;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerBeanTest {

  @Mock private CustomerService customerService;
  @Mock private FacesContext facesContext;
  @Mock private ExternalContext externalContext;

  @InjectMocks private CustomerBean customerBean;

  @Test
  void getCustomersShouldLoadListOnce() {
    Customer customer = new Customer();
    customer.setCustomerId(1L);
    when(customerService.findAll()).thenReturn(List.of(customer));

    List<Customer> firstCall = customerBean.getCustomers();
    List<Customer> secondCall = customerBean.getCustomers();

    assertTrue(firstCall.size() == 1);
    assertTrue(secondCall.size() == 1);
    verify(customerService).findAll();
  }

  @Test
  void saveShouldPersistForAdminAndResetCurrentCustomer() {
    Customer customer = new Customer();
    customer.setName("Alice");
    customer.setEmail("alice@example.com");
    customer.setAddress("Main Street 1");
    customerBean.setCustomer(customer);

    try (MockedStatic<FacesContext> mockedFacesContext = mockStatic(FacesContext.class)) {
      mockedFacesContext.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
      when(facesContext.getExternalContext()).thenReturn(externalContext);
      when(externalContext.isUserInRole("admin")).thenReturn(true);

      customerBean.save();

      verify(customerService).save(customer);
      assertNotNull(customerBean.getCustomer());
      assertTrue(customerBean.getCustomer() != customer);
    }
  }

  @Test
  void saveShouldThrowForNonAdmin() {
    try (MockedStatic<FacesContext> mockedFacesContext = mockStatic(FacesContext.class)) {
      mockedFacesContext.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
      when(facesContext.getExternalContext()).thenReturn(externalContext);
      when(externalContext.isUserInRole("admin")).thenReturn(false);

      assertThrows(SecurityException.class, () -> customerBean.save());
      verify(customerService, never()).save(customerBean.getCustomer());
    }
  }
}
