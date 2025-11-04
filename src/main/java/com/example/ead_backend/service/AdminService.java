package com.example.ead_backend.service;

import com.example.ead_backend.dto.CreateEmployeeRequest;
import com.example.ead_backend.dto.EmployeeCreateDTO;

public interface AdminService {
    EmployeeCreateDTO createEmployee(CreateEmployeeRequest request);
}
