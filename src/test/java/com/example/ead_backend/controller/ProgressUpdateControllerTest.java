package com.example.ead_backend.controller;

import com.example.ead_backend.dto.ProgressResponse;
import com.example.ead_backend.dto.ProgressUpdateRequest;
import com.example.ead_backend.service.ProgressService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller tests for ProgressUpdateController.
 */
@WebMvcTest(controllers = {ProgressUpdateController.class, ProgressViewController.class})
class ProgressUpdateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProgressService progressService;

    private ProgressUpdateRequest testRequest;
    private ProgressResponse testResponse;

    @BeforeEach
    void setUp() {
        testRequest = ProgressUpdateRequest.builder()
                .stage("Inspection")
                .percentage(50)
                .remarks("Initial inspection completed")
                .build();

        testResponse = ProgressResponse.builder()
                .id(1L)
                .appointmentId(100L)
                .stage("Inspection")
                .percentage(50)
                .remarks("Initial inspection completed")
                .updatedBy(10L)
                .updatedAt(Timestamp.from(Instant.now()))
                .build();
    }

    @Test
    @WithMockUser
    void testUpdateProgress_Success() throws Exception {
        // Arrange
        when(progressService.createOrUpdateProgress(anyLong(), any(ProgressUpdateRequest.class), anyLong()))
                .thenReturn(testResponse);

        // Act & Assert
        mockMvc.perform(put("/api/employee/progress/100")
                        .with(csrf())
                        .header("X-User-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.appointmentId", is(100)))
                .andExpect(jsonPath("$.stage", is("Inspection")))
                .andExpect(jsonPath("$.percentage", is(50)))
                .andExpect(jsonPath("$.remarks", is("Initial inspection completed")));
    }

    @Test
    @WithMockUser
    void testUpdateProgress_InvalidRequest_MissingStage() throws Exception {
        // Arrange
        ProgressUpdateRequest invalidRequest = ProgressUpdateRequest.builder()
                .percentage(50)
                .remarks("Missing stage")
                .build();

        // Act & Assert
        mockMvc.perform(put("/api/employee/progress/100")
                        .with(csrf())
                        .header("X-User-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testUpdateProgress_InvalidPercentage_LessThanZero() throws Exception {
        // Arrange
        ProgressUpdateRequest invalidRequest = ProgressUpdateRequest.builder()
                .stage("Testing")
                .percentage(-10)
                .remarks("Invalid percentage")
                .build();

        // Act & Assert
        mockMvc.perform(put("/api/employee/progress/100")
                        .with(csrf())
                        .header("X-User-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testUpdateProgress_InvalidPercentage_GreaterThan100() throws Exception {
        // Arrange
        ProgressUpdateRequest invalidRequest = ProgressUpdateRequest.builder()
                .stage("Testing")
                .percentage(150)
                .remarks("Invalid percentage")
                .build();

        // Act & Assert
        mockMvc.perform(put("/api/employee/progress/100")
                        .with(csrf())
                        .header("X-User-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testUpdateStatus_Success() throws Exception {
        // Arrange
        when(progressService.createOrUpdateProgress(anyLong(), any(ProgressUpdateRequest.class), anyLong()))
                .thenReturn(testResponse);

        // Act & Assert
        mockMvc.perform(post("/api/employee/progress/100/status")
                        .with(csrf())
                        .header("X-User-Id", "10")
                        .param("status", "In Progress"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Status updated successfully"));
    }

    @Test
    @WithMockUser
    void testGetProgressHistory_Success() throws Exception {
        // Arrange
        ProgressResponse response1 = ProgressResponse.builder()
                .id(1L)
                .appointmentId(100L)
                .stage("Inspection")
                .percentage(25)
                .build();

        ProgressResponse response2 = ProgressResponse.builder()
                .id(2L)
                .appointmentId(100L)
                .stage("Repair")
                .percentage(75)
                .build();

        List<ProgressResponse> responses = Arrays.asList(response1, response2);

        when(progressService.getProgressForAppointment(100L)).thenReturn(responses);

        // Act & Assert
        mockMvc.perform(get("/api/customer/progress/100")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].stage", is("Inspection")))
                .andExpect(jsonPath("$[0].percentage", is(25)))
                .andExpect(jsonPath("$[1].stage", is("Repair")))
                .andExpect(jsonPath("$[1].percentage", is(75)));
    }

    @Test
    @WithMockUser
    void testGetLatestProgress_Success() throws Exception {
        // Arrange
        List<ProgressResponse> responses = Arrays.asList(testResponse);
        when(progressService.getProgressForAppointment(100L)).thenReturn(responses);

        // Act & Assert
        mockMvc.perform(get("/api/customer/progress/100/latest")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.stage", is("Inspection")))
                .andExpect(jsonPath("$.percentage", is(50)));
    }

    @Test
    @WithMockUser
    void testGetLatestProgress_NotFound() throws Exception {
        // Arrange
        when(progressService.getProgressForAppointment(100L)).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/api/customer/progress/100/latest")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void testGetProgressPercentage_Success() throws Exception {
        // Arrange
        when(progressService.calculateProgressPercentage(100L)).thenReturn(65);

        // Act & Assert
        mockMvc.perform(get("/api/customer/progress/100/percentage")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("65"));
    }
}
