package com.ecommerce.jsf.bean;

import jakarta.inject.Named;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.context.FacesContext;
import jakarta.servlet.http.HttpSession;

@Named("instanceInfoBean")
@ApplicationScoped
public class InstanceInfoBean {

    public InstanceInfoBean() {
        // No-op constructor
    }

    public String getHostname() {
        incrementSessionRequestCount();
        try {
            return java.net.InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "Unknown";
        }
    }

    private void incrementSessionRequestCount() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null && context.getExternalContext().getSession(true) != null) {
            HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
            Integer count = (Integer) session.getAttribute("sessionRequestCount");
            if (count == null)
                count = 0;
            session.setAttribute("sessionRequestCount", count + 1);
        }
    }

    public int getSessionRequestCount() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null && context.getExternalContext().getSession(false) != null) {
            HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
            Integer count = (Integer) session.getAttribute("sessionRequestCount");
            return count != null ? count : 0;
        }
        return 0;
    }

    public String getIpAddress() {
        try {
            return java.net.InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "Unknown";
        }
    }

    public String getSessionId() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null && context.getExternalContext().getSession(false) != null) {
            HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
            return session.getId();
        }
        return "No session";
    }

    public String getSessionCreationTime() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null && context.getExternalContext().getSession(false) != null) {
            HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
            return new java.util.Date(session.getCreationTime()).toString();
        }
        return "No session";
    }
}
