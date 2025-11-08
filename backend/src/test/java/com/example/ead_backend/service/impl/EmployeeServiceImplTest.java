package com.example.ead_backend.service.impl;

import com.example.ead_backend.model.entity.Employee;
import com.example.ead_backend.model.entity.User;
import com.example.ead_backend.model.enums.Role;
import com.example.ead_backend.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private User testUser;
    private Employee testEmployee;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john@test.com");

        testEmployee = new Employee();
        testEmployee.setId(1L);
        testEmployee.setUser(testUser);
        testEmployee.setRole(Role.EMPLOYEE);
        testEmployee.setJoinedDate(LocalDate.now());
    }

    @Test
    void testCreateEmployee_WithValidEmployeeRole_Success() {
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);

        Employee result = employeeService.createEmployee(testUser, Role.EMPLOYEE, LocalDate.now());

        assertNotNull(result);
        assertEquals(Role.EMPLOYEE, result.getRole());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void testCreateEmployee_WithAdminRole_Success() {
        testEmployee.setRole(Role.ADMIN);
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);

        Employee result = employeeService.createEmployee(testUser, Role.ADMIN, LocalDate.now());

        assertNotNull(result);
        assertEquals(Role.ADMIN, result.getRole());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void testCreateEmployee_WithInvalidRole_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            employeeService.createEmployee(testUser, Role.CUSTOMER, LocalDate.now());
        });

        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void testFindByUserId_WhenEmployeeExists_ReturnsEmployee() {
        when(employeeRepository.findByUserId(1L)).thenReturn(Optional.of(testEmployee));

        Employee result = employeeService.findByUserId(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(employeeRepository, times(1)).findByUserId(1L);
    }

    @Test
    void testFindByUserId_WhenEmployeeNotExists_ReturnsNull() {
        when(employeeRepository.findByUserId(999L)).thenReturn(Optional.empty());

        Employee result = employeeService.findByUserId(999L);

        assertNull(result);
        verify(employeeRepository, times(1)).findByUserId(999L);
    }
}
