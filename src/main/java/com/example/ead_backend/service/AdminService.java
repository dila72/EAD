package com.example.ead_backend.service;

import com.example.ead_backend.dto.CreateEmployeeRequest;
import com.example.ead_backend.dto.EmployeeDTO;

public interface AdminService {
    EmployeeDTO createEmployee(CreateEmployeeRequest request);
}
