package com.ecommerce.jsf.bean;

import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;
import java.security.Principal;

@Named("viewBean")
@ViewScoped
public class ViewBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private String currentView = "/jsf/whoami.xhtml";

    public String getCurrentView() {
        return currentView;
    }

    public String setCurrentView(String currentView) {
        this.currentView = currentView;
        return null;
    }

    public String show(String view) {
        this.currentView = "/jsf/" + view + ".xhtml";
        return null;
    }

    public boolean isLoggedIn() {
        return getUsername() != null && !getUsername().equals("guest");
    }

    public String getUsername() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext == null)
            return null;
        HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        Principal principal = request.getUserPrincipal();
        return principal != null ? principal.getName() : null;
    }
}
