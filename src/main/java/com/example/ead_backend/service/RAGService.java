package com.example.ead_backend.service;

import com.example.ead_backend.model.entity.*;
import com.example.ead_backend.repository.*;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
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
        return "=== AVAILABLE SLOTS ===\nPlease contact us directly to check available time slots.";
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
