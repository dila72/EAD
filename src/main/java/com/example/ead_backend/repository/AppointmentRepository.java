package com.example.ead_backend.repository;

import com.example.ead_backend.model.entity.Appointment;
import com.example.ead_backend.model.entity.Employee;
import com.example.ead_backend.model.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.time.LocalDate;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, String> {
    List<Appointment> findByCustomerId(String customerId);
    List<Appointment> findByDate(LocalDate date);
    
    // Check if a time slot is already booked (excluding cancelled appointments)
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Appointment a " +
           "WHERE a.date = :date AND a.startTime = :startTime AND a.status != :cancelledStatus")
    boolean existsByDateAndStartTimeAndStatusNot(
        @Param("date") LocalDate date, 
        @Param("startTime") String startTime,
        @Param("cancelledStatus") AppointmentStatus cancelledStatus
    );
    
    // Check if a customer already has an appointment at this date/time (excluding cancelled)
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Appointment a " +
           "WHERE a.customerId = :customerId AND a.date = :date AND a.startTime = :startTime AND a.status != :cancelledStatus")
    boolean existsByCustomerIdAndDateAndStartTimeAndStatusNot(
        @Param("customerId") String customerId,
        @Param("date") LocalDate date,
        @Param("startTime") String startTime,
        @Param("cancelledStatus") AppointmentStatus cancelledStatus
    );
    
    // Find appointments assigned to an employee
    List<Appointment> findByEmployee(Employee employee);
    
    // Find appointments assigned to an employee by employee ID
    List<Appointment> findByEmployeeId(Long employeeId);
    
    // Find appointments by employee ID and status
    List<Appointment> findByEmployeeIdAndStatus(Long employeeId, AppointmentStatus status);
}
