package de.eseidinger.ecommerce.bean;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.security.Principal;

@Named
@RequestScoped
public class WhoAmIBean implements Serializable {
  public String getUsername() {
    FacesContext facesContext = FacesContext.getCurrentInstance();
    if (facesContext == null) return null;
    HttpServletRequest request =
        (HttpServletRequest) facesContext.getExternalContext().getRequest();
    Principal principal = request.getUserPrincipal();
    return principal != null ? principal.getName() : null;
  }
}
