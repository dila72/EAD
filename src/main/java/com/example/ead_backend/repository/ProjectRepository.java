package com.example.ead_backend.repository;

import com.example.ead_backend.model.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {
    List<Project> findByCustomerId(String customerId);
    List<Project> findByEmployeeId(Long employeeId);
}
