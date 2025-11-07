package com.example.ead_backend.service.impl;

import com.example.ead_backend.service.AppointmentService;
import com.example.ead_backend.service.ProgressCalculationService;
import com.example.ead_backend.dto.AppointmentDTO;
import com.example.ead_backend.model.entity.Appointment;
import com.example.ead_backend.model.entity.Employee;
import com.example.ead_backend.model.entity.TimeLog;
import com.example.ead_backend.model.enums.AppointmentStatus;
import com.example.ead_backend.repository.AppointmentRepository;
import com.example.ead_backend.repository.EmployeeRepository;
import com.example.ead_backend.repository.TimeLogRepository;
import com.example.ead_backend.mapper.AppointmentMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final EmployeeRepository employeeRepository;
    private final TimeLogRepository timeLogRepository;
    private final AppointmentMapper appointmentMapper;
    private final ProgressCalculationService progressCalculationService;

    @Override
    public AppointmentDTO createAppointment(AppointmentDTO dto) {
         // Enforce unique slot per date (by start time), excluding cancelled appointments
        LocalDate date = dto.getDate();
        String start = dto.getStartTime();
        if (date != null && start != null && appointmentRepository.existsByDateAndStartTimeAndStatusNot(date, start, AppointmentStatus.CANCELLED)) {
            throw new IllegalStateException("Time slot already booked for this date");
        }
        Appointment entity = appointmentMapper.toEntity(dto);
        // ensure new fields are copied (mapper should handle this if configured)
        entity.setCustomerId(dto.getCustomerId());
        entity.setVehicleId(dto.getVehicleId());
        Appointment saved = appointmentRepository.save(entity);
        return appointmentMapper.toDTO(saved);
    }

    @Override
    public AppointmentDTO getAppointmentById(String id) {
        Appointment entity = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id " + id));
        return appointmentMapper.toDTO(entity);
    }

    @Override
    public List<AppointmentDTO> getAllAppointments() {
        return appointmentRepository.findAll()
                .stream()
                .map(appointmentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDTO> getAppointmentsByCustomerId(String customerId) {
        return appointmentRepository.findByCustomerId(customerId)
                .stream()
                .map(appointmentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AppointmentDTO updateAppointment(String id, AppointmentDTO dto) {
        Appointment existing = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id " + id));
                 LocalDate newDate = dto.getDate();
        String newStart = dto.getStartTime();
        if (newDate != null && newStart != null) {
            boolean slotTaken = appointmentRepository.existsByDateAndStartTimeAndStatusNot(newDate, newStart, AppointmentStatus.CANCELLED);
            // allow updating to same slot the record already has
            boolean sameSlot = newDate.equals(existing.getDate()) && newStart.equals(existing.getStartTime());
            if (slotTaken && !sameSlot) {
                throw new IllegalStateException("Time slot already booked for this date");
            }
        }

        existing.setService(dto.getService());
    existing.setCustomerId(dto.getCustomerId());
    existing.setVehicleId(dto.getVehicleId());
        existing.setVehicleNo(dto.getVehicleNo());
        existing.setDate(dto.getDate());
        existing.setStartTime(dto.getStartTime());
        existing.setEndTime(dto.getEndTime());
        // Update legacy 'time' to satisfy NOT NULL constraint
        String start = dto.getStartTime();
        String end = dto.getEndTime();
        String derived = (start != null && end != null) ? start + "-" + end : (start != null ? start : "00:00");
        existing.setTime(derived);
        existing.setStatus(dto.getStatus());

        Appointment updated = appointmentRepository.save(existing);
        return appointmentMapper.toDTO(updated);
    }

    @Override
    public void deleteAppointment(String id) {
        appointmentRepository.deleteById(id);
    }

    @Override
    public List<AppointmentDTO> getAppointmentsByEmployeeId(Long employeeId) {
        return appointmentRepository.findByEmployeeId(employeeId)
                .stream()
                .map(appointment -> {
                    AppointmentDTO dto = appointmentMapper.toDTO(appointment);
                    // Populate progress percentage from progress tracking system
                    try {
                        int percentage = progressCalculationService.getLatestProgress(appointment.getAppointmentId());
                        dto.setProgressPercentage(percentage);
                    } catch (Exception e) {
                        // If no progress data exists, default to 0
                        dto.setProgressPercentage(0);
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public AppointmentDTO assignEmployeeToAppointment(String appointmentId, Long employeeId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id " + appointmentId));
        
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id " + employeeId));
        
        appointment.setEmployee(employee);
        Appointment updated = appointmentRepository.save(appointment);
        return appointmentMapper.toDTO(updated);
    }

    @Override
    public List<String> getBookedStartTimes(LocalDate date) {
        // 1) Appointment-based bookings (exclude CANCELLED)
        List<String> bookedFromAppointments = appointmentRepository.findByDate(date)
                .stream()
                .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED)
                .map(Appointment::getStartTime)
                .collect(Collectors.toList());

        // 2) TimeLog-based blocked intervals expanded into 30-minute slot starts
        List<TimeLog> logs = timeLogRepository.findByDate(date);
        Set<String> bookedFromLogs = new HashSet<>();
        for (TimeLog log : logs) {
            LocalTime start = LocalTime.parse(safeHHMM(log.getStartTime()));
            LocalTime end = LocalTime.parse(safeHHMM(log.getEndTime()));
            for (LocalTime t = start; !t.isAfter(end.minusMinutes(30)); t = t.plusMinutes(30)) {
                bookedFromLogs.add(toHHMM(t));
            }
        }

        // 3) Merge and return unique HH:mm values
        Set<String> all = new HashSet<>(bookedFromAppointments);
        all.addAll(bookedFromLogs);
        return new ArrayList<>(all);
    }

    private static String toHHMM(LocalTime t) {
        return String.format("%02d:%02d", t.getHour(), t.getMinute());
    }

    private static String safeHHMM(String t) {
        if (t == null) return "00:00";
        String s = t.trim();
        if (s.contains("-")) s = s.split("-")[0].trim();
        if (s.matches("^\\d{1,2}:\\d{2}\\s*[AaPp][Mm]$")) {
            // convert 12h to 24h
            String[] parts = s.split(" ");
            String[] hm = parts[0].split(":");
            int h = Integer.parseInt(hm[0]);
            String m = hm[1];
            String mer = parts[1].toUpperCase();
            if (mer.equals("PM") && h != 12) h += 12;
            if (mer.equals("AM") && h == 12) h = 0;
            return String.format("%02d:%s", h, m);
        }
        // strip seconds if any
        if (s.matches("^\\d{2}:\\d{2}:\\d{2}$")) return s.substring(0,5);
        // pad hour if needed
        if (s.matches("^\\d{1}:\\d{2}$")) return "0" + s;
        return s;
    }

}
