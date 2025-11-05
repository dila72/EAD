package com.example.ead_backend.service.impl;

import com.example.ead_backend.dto.CreateEmployeeRequest;
import com.example.ead_backend.dto.EmployeeCreateDTO;
import com.example.ead_backend.model.entity.Employee;
import com.example.ead_backend.model.entity.User;
import com.example.ead_backend.model.enums.Role;
import com.example.ead_backend.service.AdminService;
import com.example.ead_backend.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserService userService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public EmployeeCreateDTO createEmployee(CreateEmployeeRequest request) {
        // Hash the password before creating user
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // Create user first
        User user = userService.createUser(
                request.getFirstName(),
                request.getLastName(),
                encodedPassword,
                request.getEmail()
        );

        // Create employee record with EMPLOYEE role and today's date as joined date
        Employee employee = employeeService.createEmployee(user, Role.EMPLOYEE, LocalDate.now());

        // Convert to DTO
        return new EmployeeCreateDTO(
                employee.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                employee.getRole().name(),
                employee.getJoinedDate()
        );
    }
}
