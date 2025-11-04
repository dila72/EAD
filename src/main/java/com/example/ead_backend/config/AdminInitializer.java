package com.example.ead_backend.config;

import com.example.ead_backend.model.entity.Employee;
import com.example.ead_backend.model.entity.User;
import com.example.ead_backend.model.enums.Role;
import com.example.ead_backend.repository.EmployeeRepository;
import com.example.ead_backend.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class AdminInitializer implements CommandLineRunner {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Check if admin already exists
        if (!userRepo.existsByEmail("admin@company.com")) {
            // Create user first
            User admin = new User();
            admin.setFirstName("System");
            admin.setLastName("Admin");
            admin.setEmail("admin@company.com");
            admin.setPassword(passwordEncoder.encode("admin123"));

            User savedUser = userRepo.save(admin);

            // Create employee record with ADMIN role
            Employee adminEmployee = new Employee();
            adminEmployee.setUser(savedUser);
            adminEmployee.setRole(Role.ADMIN);
            adminEmployee.setJoinedDate(LocalDate.now());

            employeeRepository.save(adminEmployee);

            System.out.println("=====================================");
            System.out.println("Default admin created successfully!");
            System.out.println("=====================================");
            System.out.println("Email: admin@company.com");
            System.out.println("Password: admin123");
            System.out.println("=====================================");
        } else {
            System.out.println("Admin user already exists. Skipping creation.");
        }
    }
}
