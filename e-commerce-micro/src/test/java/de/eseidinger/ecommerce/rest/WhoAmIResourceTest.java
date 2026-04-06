package de.eseidinger.ecommerce.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.security.Principal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WhoAmIResourceTest {

  @Mock private Principal principal;

  @InjectMocks private WhoAmIResource whoAmIResource;

  @Test
  void meShouldReturnPrincipalNameWhenPresent() {
    when(principal.getName()).thenReturn("john");

    assertEquals("john", whoAmIResource.me());
  }

  @Test
  void meShouldReturnNoTokenWhenPrincipalIsNull() {
    WhoAmIResource resource = new WhoAmIResource();

    assertEquals("No token!", resource.me());
  }
}
