package com.example.ead_backend.service;

import com.example.ead_backend.dto.AppointmentDTO;
import com.example.ead_backend.dto.CreateEmployeeRequest;
import com.example.ead_backend.dto.CustomerDTO;
import com.example.ead_backend.dto.EmployeeCreateDTO;
import com.example.ead_backend.dto.EmployeeDTO;
import com.example.ead_backend.dto.ProjectDTO;

import java.util.List;

public interface AdminService {
    EmployeeCreateDTO createEmployee(CreateEmployeeRequest request);
    
    List<AppointmentDTO> getAllAppointments();
    
    List<CustomerDTO> getAllCustomers();
    
    List<EmployeeDTO> getAllEmployees();
    
    List<ProjectDTO> getAllProjects();
}
