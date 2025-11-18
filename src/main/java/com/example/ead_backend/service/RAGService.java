package com.example.ead_backend.service;

import com.example.ead_backend.model.entity.*;
import com.example.ead_backend.repository.*;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * RAG (Retrieval-Augmented Generation) Service
 * Retrieves relevant context from the database to augment the chatbot's
 * responses
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RAGService {

    private final ServiceRepository serviceRepository;
    private final AppointmentRepository appointmentRepository;
    private final EmployeeRepository employeeRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    /**
     * Build comprehensive context based on the user's question
     */
    public String buildContext(String question, String customerId) {
        try {
            log.info("Building context for question: '{}', customerId: '{}'", question, customerId);
            StringBuilder context = new StringBuilder();
            String lowerQuestion = question.toLowerCase();

            // Always include basic company info
            context.append(getCompanyInfo()).append("\n\n");

            // Detect intent and retrieve relevant context
            if (containsKeywords(lowerQuestion, "service", "services", "offer", "available", "type", "kind")) {
                log.debug("Adding services context");
                context.append(getServicesContext()).append("\n\n");
            }

            if (containsKeywords(lowerQuestion, "price", "cost", "charge", "fee", "expensive", "cheap")) {
                log.debug("Adding price context");
                context.append(getServicesContext()).append("\n\n");
            }

            if (containsKeywords(lowerQuestion, "slot", "time", "available", "schedule", "appointment", "book",
                    "when")) {
                log.debug("Adding available slots context");
                context.append(getAvailableSlotsContext()).append("\n\n");
            }

            if (containsKeywords(lowerQuestion, "appointment", "booking", "my appointment", "status")) {
                if (customerId != null && !customerId.isEmpty()) {
                    log.debug("Adding customer appointments context");
                    context.append(getCustomerAppointmentsContext(customerId)).append("\n\n");
                }
            }

            if (containsKeywords(lowerQuestion, "mechanic", "technician", "employee", "who", "staff")) {
                log.debug("Adding employees context");
                context.append(getEmployeesContext()).append("\n\n");
            }

            if (containsKeywords(lowerQuestion, "duration", "long", "take", "time required")) {
                log.debug("Adding duration context");
                context.append(getServicesContext()).append("\n\n");
            }

            if (containsKeywords(lowerQuestion, "contact", "location", "address", "phone", "email", "reach")) {
                log.debug("Adding contact info context");
                context.append(getContactInfo()).append("\n\n");
            }

            // If no specific context was added, provide general information
            if (context.length() < 200) {
                log.debug("Adding default context (services and slots)");
                context.append(getServicesContext()).append("\n\n");
                context.append(getAvailableSlotsContext()).append("\n\n");
            }

            log.info("Context built successfully, total length: {}", context.length());
            return context.toString();
        } catch (Exception e) {
            log.error("Error building context: {} - {}", e.getClass().getName(), e.getMessage(), e);
            // Return minimal context in case of error
            return getCompanyInfo()
                    + "\n\nI apologize, but I'm having trouble accessing detailed information at the moment.";
        }
    }

    private boolean containsKeywords(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private String getCompanyInfo() {
        return """
                === COMPANY INFORMATION ===
                Company Name: Automobile Service Center
                We are a professional automobile service center specializing in vehicle maintenance and repair.
                We offer quality service with experienced technicians and flexible scheduling.
                """;
    }

    private String getServicesContext() {
        try {
            log.debug("Fetching services from database...");
            List<com.example.ead_backend.model.entity.Service> services = serviceRepository.findByActiveTrue();
            log.debug("Found {} active services", services.size());

            if (services.isEmpty()) {
                return "=== SERVICES ===\nCurrently, no services are available. Please contact us for more information.";
            }

            StringBuilder sb = new StringBuilder("=== AVAILABLE SERVICES ===\n");
            for (com.example.ead_backend.model.entity.Service service : services) {
                sb.append(String.format("- %s\n", service.getName()));

                if (service.getDescription() != null && !service.getDescription().isEmpty()) {
                    sb.append(String.format("  Description: %s\n", service.getDescription()));
                }

                if (service.getPrice() != null) {
                    sb.append(String.format("  Price: $%.2f\n", service.getPrice()));
                }

                if (service.getEstimatedDurationMinutes() != null) {
                    int hours = service.getEstimatedDurationMinutes() / 60;
                    int minutes = service.getEstimatedDurationMinutes() % 60;
                    if (hours > 0) {
                        sb.append(String.format("  Duration: %d hour(s)", hours));
                        if (minutes > 0) {
                            sb.append(String.format(" %d minutes", minutes));
                        }
                        sb.append("\n");
                    } else {
                        sb.append(String.format("  Duration: %d minutes\n", minutes));
                    }
                }
                sb.append("\n");
            }

            return sb.toString();
        } catch (Exception e) {
            log.error("Error retrieving services context", e);
            return "=== SERVICES ===\nUnable to retrieve services at this time.";
        }
    }

    private String getAvailableSlotsContext() {
        try {
            log.debug("Fetching available time slots...");

            // Define available time slots (business hours: 9 AM to 6 PM, 30-minute slots)
            LocalDate today = LocalDate.now();
            LocalDate endDate = today.plusDays(14); // Show next 14 days for better coverage

            StringBuilder sb = new StringBuilder("=== AVAILABLE TIME SLOTS ===\n");
            sb.append("We are open Monday - Saturday, 9:00 AM - 6:00 PM\n");
            sb.append("Appointments are scheduled in 30-minute slots\n\n");

            // Get all appointments - we can't use date range easily without appointmentTime
            // field
            // So we'll get all appointments and filter by date
            List<Appointment> allAppointments = appointmentRepository.findAll();

            // Filter appointments for the next 14 days
            List<Appointment> existingAppointments = allAppointments.stream()
                    .filter(apt -> apt.getDate() != null &&
                            !apt.getDate().isBefore(today) &&
                            !apt.getDate().isAfter(endDate))
                    .toList();

            int daysShown = 0;
            int maxDaysToShow = 7; // Show up to 7 business days
            int maxSlotsPerDay = 8; // Show max 8 slots per day

            // Check next 14 days to find enough business days
            for (int day = 0; day < 14 && daysShown < maxDaysToShow; day++) {
                LocalDate date = today.plusDays(day);

                // Skip Sundays (assuming closed on Sundays)
                if (date.getDayOfWeek().getValue() == 7) {
                    continue;
                }

                List<String> availableTimesForDay = new java.util.ArrayList<>();

                // Check 30-minute slots from 9 AM to 5:30 PM (9:00, 9:30, 10:00, 10:30, ...
                // 17:30)
                // Last appointment starts at 5:30 PM and ends at 6:00 PM
                for (int hour = 9; hour <= 17; hour++) {
                    for (int minute = 0; minute < 60; minute += 30) {
                        // Skip 6:00 PM slot - last slot is 5:30 PM
                        if (hour == 17 && minute == 30) {
                            // This is the last slot (5:30 PM)
                        }

                        final int currentHour = hour;
                        final int currentMinute = minute;
                        String timeSlot = String.format("%02d:%02d", currentHour, currentMinute);

                        // Check if this slot is already booked
                        boolean isBooked = existingAppointments.stream()
                                .anyMatch(apt -> {
                                    if (apt.getDate() != null &&
                                            apt.getDate().equals(date) &&
                                            apt.getStartTime() != null &&
                                            (apt.getStatus().toString().equals("REQUESTING") ||
                                                    apt.getStatus().toString().equals("ASSIGNED") ||
                                                    apt.getStatus().toString().equals("IN_PROGRESS"))) {

                                        // Parse the start time and compare the hour and minute
                                        try {
                                            String startTime = apt.getStartTime();
                                            // Handle formats like "09:00", "9:00", "09:30", "9:30"
                                            String[] parts = startTime.split(":");
                                            int appointmentHour = Integer.parseInt(parts[0]);
                                            int appointmentMinute = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
                                            return appointmentHour == currentHour && appointmentMinute == currentMinute;
                                        } catch (Exception e) {
                                            return false;
                                        }
                                    }
                                    return false;
                                });

                        if (!isBooked) {
                            availableTimesForDay.add(timeSlot);
                        }
                    }
                }

                // Add available slots for this day if any
                if (!availableTimesForDay.isEmpty()) {
                    sb.append(String.format("Date: %s (%s)\n",
                            date.format(DATE_FORMATTER),
                            date.getDayOfWeek().toString().substring(0, 3)));

                    // Show limited slots per day to keep response concise
                    int slotsToShow = Math.min(availableTimesForDay.size(), maxSlotsPerDay);
                    for (int i = 0; i < slotsToShow; i++) {
                        sb.append(String.format("  - %s\n", availableTimesForDay.get(i)));
                    }

                    if (availableTimesForDay.size() > maxSlotsPerDay) {
                        sb.append(String.format("  ... and %d more slots\n",
                                availableTimesForDay.size() - maxSlotsPerDay));
                    }
                    sb.append("\n");
                    daysShown++;
                }
            }

            if (daysShown == 0) {
                return "=== AVAILABLE SLOTS ===\nNo available time slots in the next 14 days. Please contact us directly.";
            }

            log.debug("Found available time slots for {} days", daysShown);
            return sb.toString();

        } catch (Exception e) {
            log.error("Error retrieving available slots", e);
            return "=== AVAILABLE SLOTS ===\nUnable to retrieve available slots. Please contact us directly.";
        }
    }

    private String getCustomerAppointmentsContext(String customerId) {
        try {
            // Handle null or empty customerId
            if (customerId == null || customerId.trim().isEmpty() || customerId.equals("optional-customer-id")) {
                return "=== YOUR APPOINTMENTS ===\nPlease provide a valid customer ID to view your appointments.";
            }

            List<Appointment> appointments = appointmentRepository.findByCustomerId(customerId);

            if (appointments.isEmpty()) {
                return "=== YOUR APPOINTMENTS ===\nYou don't have any appointments yet.";
            }

            StringBuilder sb = new StringBuilder("=== YOUR APPOINTMENTS ===\n");

            // Show upcoming and recent appointments
            LocalDate today = LocalDate.now();

            List<Appointment> upcomingAppointments = appointments.stream()
                    .filter(a -> a.getDate() != null && (a.getDate().isAfter(today) || a.getDate().isEqual(today)))
                    .sorted((a1, a2) -> a1.getDate().compareTo(a2.getDate()))
                    .limit(5)
                    .toList();

            if (!upcomingAppointments.isEmpty()) {
                sb.append("\nUpcoming Appointments:\n");
                for (Appointment appointment : upcomingAppointments) {
                    sb.append(String.format("- %s on %s at %s (Status: %s)\n",
                            appointment.getService(),
                            appointment.getDate().format(DATE_FORMATTER),
                            appointment.getStartTime(),
                            appointment.getStatus()));
                    if (appointment.getEmployee() != null && appointment.getEmployee().getUser() != null) {
                        String employeeName = appointment.getEmployee().getUser().getFirstName() + " " +
                                appointment.getEmployee().getUser().getLastName();
                        sb.append(String.format("  Assigned to: %s\n", employeeName));
                    }
                }
            }

            return sb.toString();
        } catch (Exception e) {
            log.error("Error retrieving customer appointments", e);
            return "=== YOUR APPOINTMENTS ===\nUnable to retrieve your appointments at this time.";
        }
    }

    private String getEmployeesContext() {
        try {
            List<Employee> employees = employeeRepository.findAll();

            if (employees.isEmpty()) {
                return "=== STAFF INFORMATION ===\nStaff information is not available at the moment.";
            }

            StringBuilder sb = new StringBuilder("=== OUR TEAM ===\n");
            sb.append("Our experienced technicians:\n");

            for (Employee employee : employees) {
                if (employee.getUser() != null) {
                    String name = employee.getUser().getFirstName() + " " + employee.getUser().getLastName();
                    sb.append(String.format("- %s", name));
                    if (employee.getUser().getEmail() != null) {
                        sb.append(String.format(" (%s)", employee.getUser().getEmail()));
                    }
                    sb.append("\n");
                }
            }

            return sb.toString();
        } catch (Exception e) {
            log.error("Error retrieving employees", e);
            return "=== STAFF INFORMATION ===\nUnable to retrieve staff information at this time.";
        }
    }

    private String getContactInfo() {
        return """
                === CONTACT INFORMATION ===
                For direct inquiries, you can reach us at:
                - Visit our service center during business hours
                - Use this chatbot for quick assistance
                - Book appointments online through our system

                Business Hours: Monday - Saturday, 8:00 AM - 6:00 PM
                """;
    }
}
