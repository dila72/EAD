package com.example.ead_backend.service.impl;

import com.example.ead_backend.model.entity.Customer;
import com.example.ead_backend.model.entity.User;
import com.example.ead_backend.repository.CustomerRepository;
import com.example.ead_backend.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    @Transactional
    public Customer createCustomer(User user, String phoneNumber) {
        Customer customer = new Customer(user, phoneNumber);
        return customerRepository.save(customer);
    }

    @Override
    public Customer findByUserId(Long userId) {
        return customerRepository.findByUserId(userId).orElse(null);
    }
}
