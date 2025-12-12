package com.testing_exam_webapp.service;

import com.testing_exam_webapp.dto.DiagnosisRequest;
import com.testing_exam_webapp.model.mysql.Diagnosis;
import com.testing_exam_webapp.model.mysql.Doctor;
import com.testing_exam_webapp.repository.DiagnosisRepository;
import com.testing_exam_webapp.repository.DoctorRepository;
import com.testing_exam_webapp.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test suite for DiagnosisService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DiagnosisService Tests")
class DiagnosisServiceTest {

    @Mock
    private DiagnosisRepository diagnosisRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private DiagnosisService diagnosisService;

    private Diagnosis testDiagnosis;
    private Doctor testDoctor;

    @BeforeEach
    void setUp() {
        testDiagnosis = TestDataBuilder.createDiagnosis();
        testDoctor = TestDataBuilder.createDoctor();
    }

    @Test
    @DisplayName("getDiagnoses - Should return empty list")
    void getDiagnoses_EmptyList_ReturnsEmptyList() {
        when(diagnosisRepository.findAll()).thenReturn(Collections.emptyList());
        List<Diagnosis> result = diagnosisService.getDiagnoses();
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getDiagnosisById - Should return diagnosis for valid ID")
    void getDiagnosisById_ValidId_ReturnsDiagnosis() {
        UUID diagnosisId = testDiagnosis.getDiagnosisId();
        when(diagnosisRepository.findById(diagnosisId)).thenReturn(Optional.of(testDiagnosis));
        Diagnosis result = diagnosisService.getDiagnosisById(diagnosisId);
        assertEquals(diagnosisId, result.getDiagnosisId());
    }

    @Test
    @DisplayName("createDiagnosis - Should create diagnosis with valid request")
    void createDiagnosis_ValidRequest_CreatesDiagnosis() {
        DiagnosisRequest request = new DiagnosisRequest();
        request.setDiagnosisDate(LocalDate.now());
        request.setDescription("Test diagnosis");

        when(diagnosisRepository.save(any(Diagnosis.class))).thenAnswer(invocation -> {
            Diagnosis d = invocation.getArgument(0);
            d.setDiagnosisId(UUID.randomUUID());
            return d;
        });

        Diagnosis result = diagnosisService.createDiagnosis(request);
        assertNotNull(result);
    }

    @Test
    @DisplayName("createDiagnosis - Should create diagnosis with doctor")
    void createDiagnosis_WithDoctor_CreatesDiagnosis() {
        DiagnosisRequest request = new DiagnosisRequest();
        request.setDiagnosisDate(LocalDate.now());
        request.setDescription("Test diagnosis");
        request.setDoctorId(testDoctor.getDoctorId());

        when(doctorRepository.findById(testDoctor.getDoctorId())).thenReturn(Optional.of(testDoctor));
        when(diagnosisRepository.save(any(Diagnosis.class))).thenAnswer(invocation -> {
            Diagnosis d = invocation.getArgument(0);
            d.setDiagnosisId(UUID.randomUUID());
            return d;
        });

        Diagnosis result = diagnosisService.createDiagnosis(request);
        assertNotNull(result);
    }

    @Test
    @DisplayName("updateDiagnosis - Should update diagnosis")
    void updateDiagnosis_ValidRequest_UpdatesDiagnosis() {
        UUID diagnosisId = testDiagnosis.getDiagnosisId();
        DiagnosisRequest request = new DiagnosisRequest();
        request.setDescription("Updated description");

        when(diagnosisRepository.findById(diagnosisId)).thenReturn(Optional.of(testDiagnosis));
        when(diagnosisRepository.save(any(Diagnosis.class))).thenReturn(testDiagnosis);

        Diagnosis result = diagnosisService.updateDiagnosis(diagnosisId, request);
        assertNotNull(result);
    }

    @Test
    @DisplayName("deleteDiagnosis - Should delete diagnosis")
    void deleteDiagnosis_ValidId_DeletesDiagnosis() {
        UUID diagnosisId = testDiagnosis.getDiagnosisId();
        when(diagnosisRepository.existsById(diagnosisId)).thenReturn(true);
        diagnosisService.deleteDiagnosis(diagnosisId);
        verify(diagnosisRepository, times(1)).deleteById(diagnosisId);
    }
}

