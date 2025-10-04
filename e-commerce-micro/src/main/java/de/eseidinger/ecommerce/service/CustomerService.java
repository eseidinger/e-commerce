package de.eseidinger.ecommerce.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

import de.eseidinger.ecommerce.exception.ValidationException;
import de.eseidinger.ecommerce.model.Customer;
import de.eseidinger.ecommerce.repository.CustomerRepository;
import de.eseidinger.ecommerce.util.InputValidator;

@ApplicationScoped
public class CustomerService {

  @Inject private CustomerRepository customerRepository;

  private void validateCustomer(Customer customer) {
    if (!InputValidator.isValidAddress(customer.getAddress())) {
      throw new ValidationException("Invalid address");
    }
    if (!InputValidator.isValidEmail(customer.getEmail())) {
      throw new ValidationException("Invalid email");
    }
    if (!InputValidator.isNonEmptyString(customer.getName())) {
      throw new ValidationException("Invalid name");
    }
  }

  public Customer findById(Long id) {
    return customerRepository.findById(id);
  }

  public List<Customer> findAll() {
    return customerRepository.findAll();
  }

  @Transactional
  public void save(Customer customer) {
    validateCustomer(customer);
    customerRepository.save(customer);
  }

  @Transactional
  public void delete(Long id) {
    customerRepository.delete(id);
  }
}
