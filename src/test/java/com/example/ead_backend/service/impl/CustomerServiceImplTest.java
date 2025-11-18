package com.example.ead_backend.service.impl;

import com.example.ead_backend.model.entity.Customer;
import com.example.ead_backend.model.entity.User;
import com.example.ead_backend.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private User testUser;
    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john@test.com");

        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setUser(testUser);
        testCustomer.setPhoneNumber("1234567890");
    }

    @Test
    void testCreateCustomer_Success() {
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        Customer result = customerService.createCustomer(testUser, "1234567890");

        assertNotNull(result);
        assertEquals("1234567890", result.getPhoneNumber());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void testFindByUserId_WhenCustomerExists_ReturnsCustomer() {
        when(customerRepository.findByUserId(1L)).thenReturn(Optional.of(testCustomer));

        Customer result = customerService.findByUserId(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(customerRepository, times(1)).findByUserId(1L);
    }

    @Test
    void testFindByUserId_WhenCustomerNotExists_ReturnsNull() {
        when(customerRepository.findByUserId(999L)).thenReturn(Optional.empty());

        Customer result = customerService.findByUserId(999L);

        assertNull(result);
        verify(customerRepository, times(1)).findByUserId(999L);
    }
}
