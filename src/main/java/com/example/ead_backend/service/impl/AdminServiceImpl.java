package com.example.ead_backend.service.impl;

import com.example.ead_backend.dto.CreateEmployeeRequest;
import com.example.ead_backend.dto.EmployeeDTO;
import com.example.ead_backend.model.entity.User;
import com.example.ead_backend.model.enums.Role;
import com.example.ead_backend.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public EmployeeDTO createEmployee(CreateEmployeeRequest request) {
        // Hash the password before creating user
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // Create user with EMPLOYEE role
        User employee = userService.createUser(
                request.getFirstName(),
                request.getLastName(),
                encodedPassword,
                request.getEmail(),
                request.getPhoneNumber(),
                Role.EMPLOYEE
        );

        // Convert to DTO
        return new EmployeeDTO(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getPhoneNumber(),
                employee.getRole().name()
        );
    }
}
