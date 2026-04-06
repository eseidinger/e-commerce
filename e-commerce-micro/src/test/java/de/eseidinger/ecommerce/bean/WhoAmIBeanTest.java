package de.eseidinger.ecommerce.bean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class WhoAmIBeanTest {

  @Test
  void getUsernameShouldReturnNullWhenFacesContextIsMissing() {
    try (MockedStatic<FacesContext> mockedFacesContext = mockStatic(FacesContext.class)) {
      mockedFacesContext.when(FacesContext::getCurrentInstance).thenReturn(null);

      WhoAmIBean bean = new WhoAmIBean();
      assertNull(bean.getUsername());
    }
  }

  @Test
  void getUsernameShouldReturnPrincipalName() {
    FacesContext facesContext = mock(FacesContext.class);
    ExternalContext externalContext = mock(ExternalContext.class);
    HttpServletRequest request = mock(HttpServletRequest.class);
    Principal principal = mock(Principal.class);

    when(facesContext.getExternalContext()).thenReturn(externalContext);
    when(externalContext.getRequest()).thenReturn(request);
    when(request.getUserPrincipal()).thenReturn(principal);
    when(principal.getName()).thenReturn("alice");

    try (MockedStatic<FacesContext> mockedFacesContext = mockStatic(FacesContext.class)) {
      mockedFacesContext.when(FacesContext::getCurrentInstance).thenReturn(facesContext);

      WhoAmIBean bean = new WhoAmIBean();
      assertEquals("alice", bean.getUsername());
    }
  }
}
