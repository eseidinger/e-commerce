package com.ecommerce.jsf.service;

import java.util.List;

import com.ecommerce.jsf.exception.ValidationException;
import com.ecommerce.jsf.model.Customer;
import com.ecommerce.jsf.repository.CustomerRepository;
import com.ecommerce.jsf.util.InputValidator;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CustomerService {

    @Inject
    private CustomerRepository customerRepository;

    private void validateCustomer(Customer customer) {
        if (!InputValidator.isValidAddress(customer.getAddress())) {
            throw new ValidationException("Invalid address");
        }
        if (!InputValidator.isValidEmail(customer.getEmail())) {
            throw new ValidationException("Invalid email");
        }
        if (!InputValidator.isValidName(customer.getName())) {
            throw new ValidationException("Invalid name");
        }
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
