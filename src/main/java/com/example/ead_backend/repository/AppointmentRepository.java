package com.example.ead_backend.repository;

import com.example.ead_backend.model.entity.Appointment;
import com.example.ead_backend.model.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, String> {
    List<Appointment> findByCustomerId(String customerId);
    
    List<Appointment> findByStatus(AppointmentStatus status);
    
    // Count appointments for an employee on a specific date
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.employee.id = :employeeId AND a.date = :date AND a.status <> 'CANCELLED'")
    Long countAppointmentsByEmployeeAndDate(@Param("employeeId") Long employeeId, @Param("date") LocalDate date);
    
    // Get all appointments for an employee on a specific date
    @Query("SELECT a FROM Appointment a WHERE a.employee.id = :employeeId AND a.date = :date AND a.status <> 'CANCELLED'")
    List<Appointment> findByEmployeeIdAndDate(@Param("employeeId") Long employeeId, @Param("date") LocalDate date);
}
