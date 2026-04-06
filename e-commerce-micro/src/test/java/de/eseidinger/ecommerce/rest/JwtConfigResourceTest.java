package de.eseidinger.ecommerce.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import de.eseidinger.ecommerce.auth.OpenIdConfigBean;
import jakarta.ws.rs.core.Response;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JwtConfigResourceTest {

  @Mock private OpenIdConfigBean openIdConfigBean;

  @InjectMocks private JwtConfigResource jwtConfigResource;

  @Test
  void getJwtConfigShouldReturnConfigMap() {
    when(openIdConfigBean.getAuthHost()).thenReturn("http://localhost:8084");
    when(openIdConfigBean.getAuthRealm()).thenReturn("e-commerce-dev");
    when(openIdConfigBean.getClientId()).thenReturn("e-commerce");

    Response response = jwtConfigResource.getJwtConfig();

    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    assertNotNull(response.getEntity());

    @SuppressWarnings("unchecked")
    Map<String, Object> config = (Map<String, Object>) response.getEntity();
    assertEquals("http://localhost:8084", config.get("authHost"));
    assertEquals("e-commerce-dev", config.get("realm"));
    assertEquals("e-commerce", config.get("clientId"));
  }
}
