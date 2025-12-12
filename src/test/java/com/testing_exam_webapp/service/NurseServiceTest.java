package com.testing_exam_webapp.service;

import com.testing_exam_webapp.dto.NurseRequest;
import com.testing_exam_webapp.exception.EntityNotFoundException;
import com.testing_exam_webapp.exception.ValidationException;
import com.testing_exam_webapp.model.mysql.Hospital;
import com.testing_exam_webapp.model.mysql.Nurse;
import com.testing_exam_webapp.model.mysql.Ward;
import com.testing_exam_webapp.model.types.NurseSpecialityType;
import com.testing_exam_webapp.repository.HospitalRepository;
import com.testing_exam_webapp.repository.NurseRepository;
import com.testing_exam_webapp.repository.WardRepository;
import com.testing_exam_webapp.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Comprehensive test suite for NurseService.
 * Similar structure to DoctorService with nurse-specific validations.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("NurseService Tests")
class NurseServiceTest {

    @Mock
    private NurseRepository nurseRepository;

    @Mock
    private WardRepository wardRepository;

    @Mock
    private HospitalRepository hospitalRepository;

    @InjectMocks
    private NurseService nurseService;

    private Nurse testNurse;
    private Hospital testHospital;
    private Ward testWard;

    @BeforeEach
    void setUp() {
        testNurse = new Nurse();
        testNurse.setNurseId(UUID.randomUUID());
        testNurse.setNurseName("Nurse Jane");
        testNurse.setSpeciality(NurseSpecialityType.ICU);
        
        testHospital = TestDataBuilder.createHospital();
        testWard = TestDataBuilder.createWard();
    }

    @Test
    @DisplayName("getNurses - Should return empty list")
    void getNurses_EmptyList_ReturnsEmptyList() {
        when(nurseRepository.findAll()).thenReturn(Collections.emptyList());

        List<Nurse> result = nurseService.getNurses();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getNurseById - Should return nurse for valid ID")
    void getNurseById_ValidId_ReturnsNurse() {
        UUID nurseId = testNurse.getNurseId();
        when(nurseRepository.findById(nurseId)).thenReturn(Optional.of(testNurse));

        Nurse result = nurseService.getNurseById(nurseId);

        assertNotNull(result);
        assertEquals(nurseId, result.getNurseId());
    }

    @Test
    @DisplayName("getNurseById - Should throw exception for null ID")
    void getNurseById_NullId_ThrowsException() {
        assertThrows(NullPointerException.class, () -> {
            nurseService.getNurseById(null);
        });
    }

    @Test
    @DisplayName("createNurse - Should create nurse with valid request")
    void createNurse_ValidRequest_CreatesNurse() {
        NurseRequest request = new NurseRequest();
        request.setNurseName("Nurse Smith");
        request.setSpeciality(NurseSpecialityType.EMERGENCY);

        when(nurseRepository.save(any(Nurse.class))).thenAnswer(invocation -> {
            Nurse n = invocation.getArgument(0);
            n.setNurseId(UUID.randomUUID());
            return n;
        });

        Nurse result = nurseService.createNurse(request);

        assertNotNull(result);
        assertNotNull(result.getNurseId());
        assertEquals("Nurse Smith", result.getNurseName());
    }

    @Test
    @DisplayName("createNurse - Should validate ward-hospital relationship")
    void createNurse_WardHospitalMismatch_ThrowsValidationException() {
        // testWard is not associated with testHospital, so validation should fail
        NurseRequest request = new NurseRequest();
        request.setNurseName("Nurse Test");
        request.setSpeciality(NurseSpecialityType.GENERAL_CARE);
        request.setWardId(testWard.getWardId());
        request.setHospitalId(testHospital.getHospitalId());

        when(wardRepository.findById(testWard.getWardId())).thenReturn(Optional.of(testWard));
        when(hospitalRepository.findById(testHospital.getHospitalId())).thenReturn(Optional.of(testHospital));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            nurseService.createNurse(request);
        });
        assertTrue(exception.getMessage().contains("does not belong to the selected hospital"));
    }

    @ParameterizedTest
    @EnumSource(NurseSpecialityType.class)
    @DisplayName("createNurse - Equivalence Partitioning: Test all speciality types")
    void createNurse_AllSpecialityTypes_CreatesNurse(NurseSpecialityType speciality) {
        NurseRequest request = new NurseRequest();
        request.setNurseName("Nurse Test");
        request.setSpeciality(speciality);

        when(nurseRepository.save(any(Nurse.class))).thenAnswer(invocation -> {
            Nurse n = invocation.getArgument(0);
            n.setNurseId(UUID.randomUUID());
            return n;
        });

        Nurse result = nurseService.createNurse(request);
        assertEquals(speciality, result.getSpeciality());
    }

    @Test
    @DisplayName("updateNurse - Should update nurse with valid request")
    void updateNurse_ValidRequest_UpdatesNurse() {
        UUID nurseId = testNurse.getNurseId();
        NurseRequest request = new NurseRequest();
        request.setNurseName("Updated Nurse");
        request.setSpeciality(NurseSpecialityType.EMERGENCY);

        when(nurseRepository.findById(nurseId)).thenReturn(Optional.of(testNurse));
        when(nurseRepository.save(any(Nurse.class))).thenReturn(testNurse);

        Nurse result = nurseService.updateNurse(nurseId, request);

        assertNotNull(result);
        verify(nurseRepository, times(1)).findById(nurseId);
    }

    @Test
    @DisplayName("deleteNurse - Should delete nurse when valid ID provided")
    void deleteNurse_ValidId_DeletesNurse() {
        UUID nurseId = testNurse.getNurseId();
        when(nurseRepository.existsById(nurseId)).thenReturn(true);
        doNothing().when(nurseRepository).deleteById(nurseId);

        nurseService.deleteNurse(nurseId);

        verify(nurseRepository, times(1)).deleteById(nurseId);
    }

    @Test
    @DisplayName("deleteNurse - Should throw exception when nurse not found")
    void deleteNurse_NurseNotFound_ThrowsException() {
        UUID nonExistentId = UUID.randomUUID();
        when(nurseRepository.existsById(nonExistentId)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            nurseService.deleteNurse(nonExistentId);
        });
        assertEquals("Nurse not found", exception.getMessage());
    }
}

