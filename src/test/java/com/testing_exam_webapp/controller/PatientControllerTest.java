package com.testing_exam_webapp.controller;

import com.testing_exam_webapp.dto.PatientRequest;
import com.testing_exam_webapp.model.mysql.Patient;
import com.testing_exam_webapp.service.PatientService;
import com.testing_exam_webapp.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Test suite for PatientController.
 * Tests HTTP endpoints, status codes, and response bodies.
 */
class PatientControllerTest {

    private PatientService patientService;
    private PatientController patientController;
    private Patient testPatient;

    @BeforeEach
    void setUp() {
        patientService = mock(PatientService.class);
        patientController = new PatientController(patientService);
        testPatient = TestDataBuilder.createPatient();
    }

    @Test
    @DisplayName("getPatients - Should return OK with patients list")
    void getPatients_WithPatients_ReturnsOk() {
        List<Patient> patients = Arrays.asList(testPatient);
        when(patientService.getPatients()).thenReturn(patients);

        ResponseEntity<List<Patient>> response = patientController.getPatients();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("getPatients - Should return NO_CONTENT when empty")
    void getPatients_EmptyList_ReturnsNoContent() {
        when(patientService.getPatients()).thenReturn(Collections.emptyList());

        ResponseEntity<List<Patient>> response = patientController.getPatients();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @DisplayName("getPatientById - Should return OK with patient")
    void getPatientById_ValidId_ReturnsOk() {
        UUID patientId = testPatient.getPatientId();
        when(patientService.getPatientById(patientId)).thenReturn(testPatient);

        ResponseEntity<Patient> response = patientController.getPatientById(patientId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(patientId, response.getBody().getPatientId());
    }

    @Test
    @DisplayName("createPatient - Should return CREATED with patient")
    void createPatient_ValidRequest_ReturnsCreated() {
        PatientRequest request = new PatientRequest();
        request.setPatientName("New Patient");
        request.setDateOfBirth(LocalDate.of(1990, 1, 1));

        when(patientService.createPatient(any(PatientRequest.class))).thenReturn(testPatient);

        ResponseEntity<Patient> response = patientController.createPatient(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("updatePatient - Should return OK with updated patient")
    void updatePatient_ValidRequest_ReturnsOk() {
        UUID patientId = testPatient.getPatientId();
        PatientRequest request = new PatientRequest();
        request.setPatientName("Updated Name");

        when(patientService.updatePatient(eq(patientId), any(PatientRequest.class))).thenReturn(testPatient);

        ResponseEntity<Patient> response = patientController.updatePatient(patientId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("deletePatient - Should return NO_CONTENT")
    void deletePatient_ValidId_ReturnsNoContent() {
        UUID patientId = testPatient.getPatientId();
        doNothing().when(patientService).deletePatient(patientId);

        ResponseEntity<Void> response = patientController.deletePatient(patientId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @DisplayName("getPatientsByWardId - Should return OK with patients")
    void getPatientsByWardId_ValidWardId_ReturnsOk() {
        UUID wardId = UUID.randomUUID();
        List<Patient> patients = Arrays.asList(testPatient);
        when(patientService.getPatientsByWardId(wardId)).thenReturn(patients);

        ResponseEntity<List<Patient>> response = patientController.getPatientsByWardId(wardId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}

