package de.eseidinger.ecommerce.bean;

import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.eseidinger.ecommerce.model.Customer;
import de.eseidinger.ecommerce.service.CustomerService;

@Named("customerBean")
@ViewScoped
public class CustomerBean implements Serializable {

  private static final Logger logger = LoggerFactory.getLogger(CustomerBean.class);
  private static final long serialVersionUID = 1L;

  private Customer customer = new Customer();
  private List<Customer> customers;

  @Inject private CustomerService customerService;

  public Customer getCustomer() {
    return customer;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
  }

  public List<Customer> getCustomers() {
    if (customers == null) {
      logger.info("Loading customer list from service");
      customers = customerService.findAll();
    }
    return customers;
  }

  public String save() {
    FacesContext context = FacesContext.getCurrentInstance();
    if (!context.getExternalContext().isUserInRole("admin")) {
      logger.warn("Unauthorized save attempt by user without admin role");
      throw new SecurityException("Access denied: admin role required");
    }
    logger.info("Saving customer: {}", customer);
    customerService.save(customer);
    customer = new Customer();
    customers = null;
    return null;
  }

  public String edit(Customer c) {
    logger.info("Editing customer with ID: {}", c.getCustomerId());
    FacesContext context = FacesContext.getCurrentInstance();
    if (!context.getExternalContext().isUserInRole("admin")) {
      logger.warn("Unauthorized edit attempt by user without admin role");
      throw new SecurityException("Access denied: admin role required");
    }
    this.customer = c;
    return null;
  }

  public String delete(Customer c) {
    FacesContext context = FacesContext.getCurrentInstance();
    if (!context.getExternalContext().isUserInRole("admin")) {
      logger.warn("Unauthorized delete attempt by user without admin role");
      throw new SecurityException("Access denied: admin role required");
    }
    logger.info("Deleting customer with ID: {}", c.getCustomerId());
    customerService.delete(c.getCustomerId());
    customers = null;
    return null;
  }
}
