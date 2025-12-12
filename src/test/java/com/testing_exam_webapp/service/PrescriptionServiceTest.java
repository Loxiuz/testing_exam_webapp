package com.testing_exam_webapp.service;

import com.testing_exam_webapp.dto.PrescriptionRequest;
import com.testing_exam_webapp.model.mysql.*;
import com.testing_exam_webapp.repository.*;
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
 * Test suite for PrescriptionService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PrescriptionService Tests")
class PrescriptionServiceTest {

    @Mock
    private PrescriptionRepository prescriptionRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private MedicationRepository medicationRepository;

    @InjectMocks
    private PrescriptionService prescriptionService;

    private Prescription testPrescription;
    private Patient testPatient;
    private Doctor testDoctor;
    private Medication testMedication;

    @BeforeEach
    void setUp() {
        testPrescription = new Prescription();
        testPrescription.setPrescriptionId(UUID.randomUUID());
        testPrescription.setStartDate(LocalDate.now());
        testPrescription.setEndDate(LocalDate.now().plusDays(30));
        
        testPatient = TestDataBuilder.createPatient();
        testDoctor = TestDataBuilder.createDoctor();
        testMedication = new Medication();
        testMedication.setMedicationId(UUID.randomUUID());
    }

    @Test
    @DisplayName("getPrescriptions - Should return empty list")
    void getPrescriptions_EmptyList_ReturnsEmptyList() {
        when(prescriptionRepository.findAll()).thenReturn(Collections.emptyList());
        List<Prescription> result = prescriptionService.getPrescriptions();
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("createPrescription - Should create prescription with all relations")
    void createPrescription_WithAllRelations_CreatesPrescription() {
        PrescriptionRequest request = new PrescriptionRequest();
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusDays(30));
        request.setPatientId(testPatient.getPatientId());
        request.setDoctorId(testDoctor.getDoctorId());
        request.setMedicationId(testMedication.getMedicationId());

        when(patientRepository.findById(testPatient.getPatientId())).thenReturn(Optional.of(testPatient));
        when(doctorRepository.findById(testDoctor.getDoctorId())).thenReturn(Optional.of(testDoctor));
        when(medicationRepository.findById(testMedication.getMedicationId())).thenReturn(Optional.of(testMedication));
        when(prescriptionRepository.save(any(Prescription.class))).thenAnswer(invocation -> {
            Prescription p = invocation.getArgument(0);
            p.setPrescriptionId(UUID.randomUUID());
            return p;
        });

        Prescription result = prescriptionService.createPrescription(request);
        assertNotNull(result);
    }

    @Test
    @DisplayName("updatePrescription - Should update prescription")
    void updatePrescription_ValidRequest_UpdatesPrescription() {
        UUID prescriptionId = testPrescription.getPrescriptionId();
        PrescriptionRequest request = new PrescriptionRequest();
        request.setStartDate(LocalDate.now().plusDays(1));
        request.setEndDate(LocalDate.now().plusDays(60));

        when(prescriptionRepository.findById(prescriptionId)).thenReturn(Optional.of(testPrescription));
        when(prescriptionRepository.save(any(Prescription.class))).thenReturn(testPrescription);

        Prescription result = prescriptionService.updatePrescription(prescriptionId, request);
        assertNotNull(result);
    }

    @Test
    @DisplayName("deletePrescription - Should delete prescription")
    void deletePrescription_ValidId_DeletesPrescription() {
        UUID prescriptionId = testPrescription.getPrescriptionId();
        when(prescriptionRepository.existsById(prescriptionId)).thenReturn(true);
        prescriptionService.deletePrescription(prescriptionId);
        verify(prescriptionRepository, times(1)).deleteById(prescriptionId);
    }
}

