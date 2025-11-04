package com.example.ead_backend.service.impl;

import com.example.ead_backend.model.entity.Employee;
import com.example.ead_backend.model.entity.User;
import com.example.ead_backend.model.enums.Role;
import com.example.ead_backend.repository.EmployeeRepository;
import com.example.ead_backend.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public Employee createEmployee(User user, Role role, LocalDate joinedDate) {
        if (role != Role.ADMIN && role != Role.EMPLOYEE) {
            throw new IllegalArgumentException("Invalid role for employee");
        }
        Employee employee = new Employee(user, role, joinedDate);
        return employeeRepository.save(employee);
    }

    @Override
    public Employee findByUserId(Long userId) {
        return employeeRepository.findByUserId(userId).orElse(null);
    }
}
