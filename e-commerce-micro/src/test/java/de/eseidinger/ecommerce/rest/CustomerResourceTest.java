package de.eseidinger.ecommerce.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.eseidinger.ecommerce.dto.CustomerDto;
import de.eseidinger.ecommerce.model.Customer;
import de.eseidinger.ecommerce.service.CustomerService;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerResourceTest {

  @Mock private CustomerService customerService;

  @InjectMocks private CustomerResource customerResource;

  @Test
  void getAllShouldMapEntitiesToDtos() {
    Customer customer = new Customer();
    customer.setCustomerId(1L);
    customer.setName("Alice");
    customer.setEmail("alice@example.com");
    customer.setAddress("Street 1");
    when(customerService.findAll()).thenReturn(List.of(customer));

    List<CustomerDto> result = customerResource.getAll();

    assertEquals(1, result.size());
    assertEquals("Alice", result.get(0).name());
  }

  @Test
  void createShouldReturnCreatedResponse() {
    CustomerDto dto = new CustomerDto(1L, "Alice", "alice@example.com", "Street 1");

    Response response = customerResource.create(dto);

    assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    assertEquals(dto, response.getEntity());
  }

  @Test
  void deleteShouldReturnNoContent() {
    Response response = customerResource.delete(5L);

    assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    assertNotNull(response);
    verify(customerService).delete(5L);
  }
}
