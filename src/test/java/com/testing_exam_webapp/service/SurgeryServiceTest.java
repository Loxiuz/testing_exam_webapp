package com.testing_exam_webapp.service;

import com.testing_exam_webapp.dto.SurgeryRequest;
import com.testing_exam_webapp.model.mysql.Doctor;
import com.testing_exam_webapp.model.mysql.Patient;
import com.testing_exam_webapp.model.mysql.Surgery;
import com.testing_exam_webapp.repository.DoctorRepository;
import com.testing_exam_webapp.repository.PatientRepository;
import com.testing_exam_webapp.repository.SurgeryRepository;
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
 * Test suite for SurgeryService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SurgeryService Tests")
class SurgeryServiceTest {

    @Mock
    private SurgeryRepository surgeryRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private SurgeryService surgeryService;

    private Surgery testSurgery;
    private Patient testPatient;
    private Doctor testDoctor;

    @BeforeEach
    void setUp() {
        testSurgery = new Surgery();
        testSurgery.setSurgeryId(UUID.randomUUID());
        testSurgery.setSurgeryDate(LocalDate.now().plusDays(7));
        testSurgery.setDescription("Heart surgery");
        
        testPatient = TestDataBuilder.createPatient();
        testDoctor = TestDataBuilder.createDoctor();
    }

    @Test
    @DisplayName("getSurgeries - Should return empty list")
    void getSurgeries_EmptyList_ReturnsEmptyList() {
        when(surgeryRepository.findAll()).thenReturn(Collections.emptyList());
        List<Surgery> result = surgeryService.getSurgeries();
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getSurgeryById - Should return surgery for valid ID")
    void getSurgeryById_ValidId_ReturnsSurgery() {
        UUID surgeryId = testSurgery.getSurgeryId();
        when(surgeryRepository.findById(surgeryId)).thenReturn(Optional.of(testSurgery));
        Surgery result = surgeryService.getSurgeryById(surgeryId);
        assertEquals(surgeryId, result.getSurgeryId());
    }

    @Test
    @DisplayName("createSurgery - Should create surgery with patient and doctor")
    void createSurgery_WithPatientAndDoctor_CreatesSurgery() {
        SurgeryRequest request = new SurgeryRequest();
        request.setSurgeryDate(LocalDate.now().plusDays(14));
        request.setDescription("Knee surgery");
        request.setPatientId(testPatient.getPatientId());
        request.setDoctorId(testDoctor.getDoctorId());

        when(patientRepository.findById(testPatient.getPatientId())).thenReturn(Optional.of(testPatient));
        when(doctorRepository.findById(testDoctor.getDoctorId())).thenReturn(Optional.of(testDoctor));
        when(surgeryRepository.save(any(Surgery.class))).thenAnswer(invocation -> {
            Surgery s = invocation.getArgument(0);
            s.setSurgeryId(UUID.randomUUID());
            return s;
        });

        Surgery result = surgeryService.createSurgery(request);
        assertNotNull(result);
    }

    @Test
    @DisplayName("updateSurgery - Should update surgery")
    void updateSurgery_ValidRequest_UpdatesSurgery() {
        UUID surgeryId = testSurgery.getSurgeryId();
        SurgeryRequest request = new SurgeryRequest();
        request.setSurgeryDate(LocalDate.now().plusDays(21));
        request.setDescription("Updated description");

        when(surgeryRepository.findById(surgeryId)).thenReturn(Optional.of(testSurgery));
        when(surgeryRepository.save(any(Surgery.class))).thenReturn(testSurgery);

        Surgery result = surgeryService.updateSurgery(surgeryId, request);
        assertNotNull(result);
    }

    @Test
    @DisplayName("deleteSurgery - Should delete surgery")
    void deleteSurgery_ValidId_DeletesSurgery() {
        UUID surgeryId = testSurgery.getSurgeryId();
        when(surgeryRepository.existsById(surgeryId)).thenReturn(true);
        surgeryService.deleteSurgery(surgeryId);
        verify(surgeryRepository, times(1)).deleteById(surgeryId);
    }
}

