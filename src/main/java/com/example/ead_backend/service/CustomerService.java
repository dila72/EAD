package com.example.ead_backend.service;

import com.example.ead_backend.model.entity.Customer;
import com.example.ead_backend.model.entity.User;

public interface CustomerService {
    Customer createCustomer(User user, String phoneNumber);
    Customer findByUserId(Long userId);
}
