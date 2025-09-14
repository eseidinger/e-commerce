package com.ecommerce.jsf.bean;

import com.ecommerce.jsf.model.Customer;
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.transaction.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;
import com.ecommerce.jsf.util.InputValidator;

@Named("customerBean")
@ViewScoped
@Transactional
public class CustomerBean implements Serializable {
    private static final Logger logger = Logger.getLogger(CustomerBean.class.getName());
    private static final long serialVersionUID = 1L;
    private Customer customer = new Customer();
    private List<Customer> customers;
    @PersistenceContext(unitName = "ecommercePU")
    private EntityManager em;

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
                TypedQuery<Customer> query = em.createQuery("SELECT c FROM Customer c", Customer.class);
                customers = query.getResultList();
            }
            return customers;
        } catch (Exception e) {
            logger.severe("Error loading customers: " + e.getMessage());
            return null;
        }
    }

    public String save() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if (!InputValidator.isValidName(customer.getName())) {
                logger.warning("Invalid customer name");
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid name", "Please enter a valid name."));
                return null;
            }
            if (!InputValidator.isValidEmail(customer.getEmail())) {
                logger.warning("Invalid customer email");
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid email", "Please enter a valid email address."));
                return null;
            }
            if (!InputValidator.isValidAddress(customer.getAddress())) {
                logger.warning("Invalid customer address");
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid address", "Please enter a valid address."));
                return null;
            }
            if (customer.getCustomerId() == null) {
                logger.info("Persisting new customer");
                em.persist(customer);
            } else {
                logger.info("Merging existing customer with ID: " + customer.getCustomerId());
                em.merge(customer);
            }
            customer = new Customer();
            customers = null;
        } catch (Exception e) {
            logger.severe("Error saving customer: " + e.getMessage());
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "An error occurred while saving the customer."));
        }
        return null;
    }

    public String edit(Customer c) {
        logger.info("Editing customer with ID: " + c.getCustomerId());
        this.customer = c;
        return null;
    }

    public String delete(Customer c) {
        try {
            logger.info("Deleting customer with ID: " + c.getCustomerId());
            Customer toRemove = em.find(Customer.class, c.getCustomerId());
            if (toRemove != null) {
                em.remove(toRemove);
            }
            customers = null;
        } catch (Exception e) {
            logger.severe("Error deleting customer: " + e.getMessage());
        }
        return null;
    }
}
