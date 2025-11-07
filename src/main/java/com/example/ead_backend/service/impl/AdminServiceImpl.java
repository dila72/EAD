package com.example.ead_backend.service.impl;

import com.example.ead_backend.dto.AppointmentDTO;
import com.example.ead_backend.dto.CreateEmployeeRequest;
import com.example.ead_backend.dto.CustomerDTO;
import com.example.ead_backend.dto.EmployeeCreateDTO;
import com.example.ead_backend.dto.EmployeeDTO;
import com.example.ead_backend.dto.ProjectDTO;
import com.example.ead_backend.model.entity.Customer;
import com.example.ead_backend.model.entity.Employee;
import com.example.ead_backend.model.entity.User;
import com.example.ead_backend.model.enums.Role;
import com.example.ead_backend.repository.CustomerRepository;
import com.example.ead_backend.repository.EmployeeRepository;
import com.example.ead_backend.service.AdminService;
import com.example.ead_backend.service.AppointmentService;
import com.example.ead_backend.service.EmployeeService;
import com.example.ead_backend.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserService userService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ProjectService projectService;

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

    @Override
    public List<AppointmentDTO> getAllAppointments() {
        return appointmentService.getAllAppointments();
    }

    @Override
    public List<CustomerDTO> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return customers.stream()
                .map(customer -> new CustomerDTO(
                        customer.getId(),
                        customer.getUser().getFirstName(),
                        customer.getUser().getLastName(),
                        customer.getUser().getEmail(),
                        customer.getPhoneNumber()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeDTO> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        return employees.stream()
                .map(employee -> new EmployeeDTO(
                        employee.getId(),
                        employee.getUser().getFirstName(),
                        employee.getUser().getLastName(),
                        employee.getUser().getEmail(),
                        employee.getRole().name(),
                        employee.getJoinedDate()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectDTO> getAllProjects() {
        return projectService.getAllProjects();
    }
}
