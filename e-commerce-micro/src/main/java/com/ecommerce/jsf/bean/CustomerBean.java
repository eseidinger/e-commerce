package com.ecommerce.jsf.bean;

import com.ecommerce.jsf.model.Customer;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import jakarta.faces.view.ViewScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import com.ecommerce.jsf.repository.CustomerRepository;
import jakarta.inject.Inject;
import java.io.Serializable;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ecommerce.jsf.util.InputValidator;

@Named("customerBean")
@ViewScoped
public class CustomerBean implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(CustomerBean.class);

    private static final long serialVersionUID = 1L;

    private Customer customer = new Customer();
    private List<Customer> customers;

    @Inject
    private CustomerRepository customerRepository;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<Customer> getCustomers() {
        try {
            if (customers == null) {
                logger.info("Loading customer list from database");
                customers = customerRepository.findAll();
            }
            return customers;
        } catch (Exception e) {
            logger.error("Error loading customers: {}", e.getMessage());
            return null;
        }
    }

    @Transactional
    public String save() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (!context.getExternalContext().isUserInRole("admin")) {
            logger.warn("Unauthorized save attempt by user without admin role");
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Security Error",
                    "Access denied: admin role required"));
            return null;
        }
        if (!InputValidator.isValidName(customer.getName())) {
            logger.warn("Invalid customer name");
            context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid name", "Please enter a valid name."));
            return null;
        }
        if (!InputValidator.isValidEmail(customer.getEmail())) {
            logger.warn("Invalid customer email");
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid email",
                    "Please enter a valid email address."));
            return null;
        }
        if (!InputValidator.isValidAddress(customer.getAddress())) {
            logger.warn("Invalid customer address");
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid address",
                    "Please enter a valid address."));
            return null;
        }
        try {
            customerRepository.save(customer);
            customer = new Customer();
            customers = null;
        } catch (Exception e) {
            logger.error("Error saving customer: {}", e.getMessage());
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                    "An error occurred while saving the customer."));
        }
        return null;
    }

    public String edit(Customer c) {
        logger.info("Editing customer with ID: {}", c.getCustomerId());
        FacesContext context = FacesContext.getCurrentInstance();
        if (!context.getExternalContext().isUserInRole("admin")) {
            logger.warn("Unauthorized edit attempt by user without admin role");
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Security Error",
                    "Access denied: admin role required"));
            return null;
        }
        this.customer = c;
        return null;
    }

    @Transactional
    public String delete(Customer c) {
        FacesContext context = FacesContext.getCurrentInstance();
        if (!context.getExternalContext().isUserInRole("admin")) {
            logger.warn("Unauthorized delete attempt by user without admin role");
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Security Error",
                    "Access denied: admin role required"));
            return null;
        }
        try {
            logger.info("Deleting customer with ID: {}", c.getCustomerId());
            customerRepository.delete(c.getCustomerId());
            customers = null;
        } catch (Exception e) {
            logger.error("Error deleting customer: {}", e.getMessage());
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                    "An error occurred while deleting the customer."));
        }
        return null;
    }
}
