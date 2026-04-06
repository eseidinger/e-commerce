package de.eseidinger.ecommerce.bean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class ViewBeanTest {

  @Test
  void showShouldUpdateCurrentViewPath() {
    ViewBean bean = new ViewBean();

    bean.show("customer");

    assertEquals("/jsf/customer.xhtml", bean.getCurrentView());
  }

  @Test
  void getUsernameShouldReturnNullWhenFacesContextIsMissing() {
    try (MockedStatic<FacesContext> mockedFacesContext = mockStatic(FacesContext.class)) {
      mockedFacesContext.when(FacesContext::getCurrentInstance).thenReturn(null);

      ViewBean bean = new ViewBean();
      assertNull(bean.getUsername());
      assertFalse(bean.isLoggedIn());
    }
  }

  @Test
  void isLoggedInShouldBeTrueForAuthenticatedPrincipal() {
    FacesContext facesContext = mock(FacesContext.class);
    ExternalContext externalContext = mock(ExternalContext.class);
    HttpServletRequest request = mock(HttpServletRequest.class);
    Principal principal = mock(Principal.class);

    when(facesContext.getExternalContext()).thenReturn(externalContext);
    when(externalContext.getRequest()).thenReturn(request);
    when(request.getUserPrincipal()).thenReturn(principal);
    when(principal.getName()).thenReturn("john");

    try (MockedStatic<FacesContext> mockedFacesContext = mockStatic(FacesContext.class)) {
      mockedFacesContext.when(FacesContext::getCurrentInstance).thenReturn(facesContext);

      ViewBean bean = new ViewBean();
      assertEquals("john", bean.getUsername());
      assertTrue(bean.isLoggedIn());
    }
  }
}
