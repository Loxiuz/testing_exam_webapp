package com.testing_exam_webapp.service;

import com.testing_exam_webapp.dto.MedicationRequest;
import com.testing_exam_webapp.model.mysql.Medication;
import com.testing_exam_webapp.repository.MedicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test suite for MedicationService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MedicationService Tests")
class MedicationServiceTest {

    @Mock
    private MedicationRepository medicationRepository;

    @InjectMocks
    private MedicationService medicationService;

    private Medication testMedication;

    @BeforeEach
    void setUp() {
        testMedication = new Medication();
        testMedication.setMedicationId(UUID.randomUUID());
        testMedication.setMedicationName("Aspirin");
        testMedication.setDosage("100mg");
    }

    @Test
    @DisplayName("getMedications - Should return empty list")
    void getMedications_EmptyList_ReturnsEmptyList() {
        when(medicationRepository.findAll()).thenReturn(Collections.emptyList());
        List<Medication> result = medicationService.getMedications();
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getMedicationById - Should return medication for valid ID")
    void getMedicationById_ValidId_ReturnsMedication() {
        UUID medicationId = testMedication.getMedicationId();
        when(medicationRepository.findById(medicationId)).thenReturn(Optional.of(testMedication));
        Medication result = medicationService.getMedicationById(medicationId);
        assertEquals(medicationId, result.getMedicationId());
    }

    @Test
    @DisplayName("createMedication - Should create medication")
    void createMedication_ValidRequest_CreatesMedication() {
        MedicationRequest request = new MedicationRequest();
        request.setMedicationName("Ibuprofen");
        request.setDosage("200mg");

        when(medicationRepository.save(any(Medication.class))).thenAnswer(invocation -> {
            Medication m = invocation.getArgument(0);
            m.setMedicationId(UUID.randomUUID());
            return m;
        });

        Medication result = medicationService.createMedication(request);
        assertNotNull(result);
        assertEquals("Ibuprofen", result.getMedicationName());
    }

    @Test
    @DisplayName("updateMedication - Should update medication")
    void updateMedication_ValidRequest_UpdatesMedication() {
        UUID medicationId = testMedication.getMedicationId();
        MedicationRequest request = new MedicationRequest();
        request.setMedicationName("Updated Name");
        request.setDosage("300mg");

        when(medicationRepository.findById(medicationId)).thenReturn(Optional.of(testMedication));
        when(medicationRepository.save(any(Medication.class))).thenReturn(testMedication);

        Medication result = medicationService.updateMedication(medicationId, request);
        assertNotNull(result);
    }

    @Test
    @DisplayName("deleteMedication - Should delete medication")
    void deleteMedication_ValidId_DeletesMedication() {
        UUID medicationId = testMedication.getMedicationId();
        when(medicationRepository.existsById(medicationId)).thenReturn(true);
        medicationService.deleteMedication(medicationId);
        verify(medicationRepository, times(1)).deleteById(medicationId);
    }
}

