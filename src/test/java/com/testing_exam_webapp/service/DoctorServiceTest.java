package com.testing_exam_webapp.service;

import com.testing_exam_webapp.dto.DoctorRequest;
import com.testing_exam_webapp.exception.EntityNotFoundException;
import com.testing_exam_webapp.exception.ValidationException;
import com.testing_exam_webapp.model.mysql.Doctor;
import com.testing_exam_webapp.model.mysql.Hospital;
import com.testing_exam_webapp.model.mysql.Ward;
import com.testing_exam_webapp.model.types.DoctorSpecialityType;
import com.testing_exam_webapp.repository.DoctorRepository;
import com.testing_exam_webapp.repository.HospitalRepository;
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
 * Comprehensive test suite for DoctorService.
 * 
 * Demonstrates:
 * - Black Box Testing: Equivalence partitioning (speciality types), decision table testing (ward-hospital validation)
 * - White Box Testing: Branch coverage, condition coverage for validation logic
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DoctorService Tests")
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private WardRepository wardRepository;

    @Mock
    private HospitalRepository hospitalRepository;

    @InjectMocks
    private DoctorService doctorService;

    private Doctor testDoctor;
    private Hospital testHospital;
    private Ward testWard;

    @BeforeEach
    void setUp() {
        testDoctor = TestDataBuilder.createDoctor();
        testHospital = TestDataBuilder.createHospital();
        testWard = TestDataBuilder.createWard();
    }

    @Test
    @DisplayName("getDoctors - Should return empty list")
    void getDoctors_EmptyList_ReturnsEmptyList() {
        when(doctorRepository.findAll()).thenReturn(Collections.emptyList());

        List<Doctor> result = doctorService.getDoctors();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(doctorRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getDoctors - Should return populated list")
    void getDoctors_PopulatedList_ReturnsList() {
        List<Doctor> doctors = Arrays.asList(testDoctor, TestDataBuilder.createDoctor());
        when(doctorRepository.findAll()).thenReturn(doctors);

        List<Doctor> result = doctorService.getDoctors();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("getDoctorById - Should return doctor for valid ID")
    void getDoctorById_ValidId_ReturnsDoctor() {
        UUID doctorId = testDoctor.getDoctorId();
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(testDoctor));

        Doctor result = doctorService.getDoctorById(doctorId);

        assertNotNull(result);
        assertEquals(doctorId, result.getDoctorId());
    }

    @Test
    @DisplayName("getDoctorById - Should throw exception for null ID")
    void getDoctorById_NullId_ThrowsException() {
        assertThrows(NullPointerException.class, () -> {
            doctorService.getDoctorById(null);
        });
    }

    @Test
    @DisplayName("getDoctorById - Should throw exception when doctor not found")
    void getDoctorById_NonExistentId_ThrowsException() {
        UUID nonExistentId = UUID.randomUUID();
        when(doctorRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            doctorService.getDoctorById(nonExistentId);
        });
        assertEquals("Doctor not found", exception.getMessage());
    }

    @Test
    @DisplayName("createDoctor - Should create doctor with valid request")
    void createDoctor_ValidRequest_CreatesDoctor() {
        DoctorRequest request = new DoctorRequest();
        request.setDoctorName("Dr. Smith");
        request.setSpeciality(DoctorSpecialityType.CARDIOLOGY);

        when(doctorRepository.save(any(Doctor.class))).thenAnswer(invocation -> {
            Doctor d = invocation.getArgument(0);
            d.setDoctorId(UUID.randomUUID());
            return d;
        });

        Doctor result = doctorService.createDoctor(request);

        assertNotNull(result);
        assertNotNull(result.getDoctorId());
        assertEquals("Dr. Smith", result.getDoctorName());
        assertEquals(DoctorSpecialityType.CARDIOLOGY, result.getSpeciality());
    }

    @Test
    @DisplayName("createDoctor - Should create doctor when ward and hospital both provided and valid (Decision Table)")
    void createDoctor_WardAndHospitalValid_CreatesDoctor() {
        TestDataBuilder.associateWardWithHospital(testWard, testHospital);
        
        DoctorRequest request = new DoctorRequest();
        request.setDoctorName("Dr. Smith");
        request.setSpeciality(DoctorSpecialityType.NEUROLOGY);
        request.setWardId(testWard.getWardId());
        request.setHospitalId(testHospital.getHospitalId());

        when(wardRepository.findById(testWard.getWardId())).thenReturn(Optional.of(testWard));
        when(hospitalRepository.findById(testHospital.getHospitalId())).thenReturn(Optional.of(testHospital));
        when(doctorRepository.save(any(Doctor.class))).thenAnswer(invocation -> {
            Doctor d = invocation.getArgument(0);
            d.setDoctorId(UUID.randomUUID());
            return d;
        });

        Doctor result = doctorService.createDoctor(request);

        assertNotNull(result);
        assertNotNull(result.getWard());
        assertNotNull(result.getHospital());
    }

    @Test
    @DisplayName("createDoctor - Should throw ValidationException when ward doesn't belong to hospital")
    void createDoctor_WardHospitalMismatch_ThrowsValidationException() {
        Ward otherWard = TestDataBuilder.createWard();
        
        DoctorRequest request = new DoctorRequest();
        request.setDoctorName("Dr. Smith");
        request.setSpeciality(DoctorSpecialityType.GENERAL_MEDICINE);
        request.setWardId(otherWard.getWardId());
        request.setHospitalId(testHospital.getHospitalId());

        when(wardRepository.findById(otherWard.getWardId())).thenReturn(Optional.of(otherWard));
        when(hospitalRepository.findById(testHospital.getHospitalId())).thenReturn(Optional.of(testHospital));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            doctorService.createDoctor(request);
        });
        assertTrue(exception.getMessage().contains("does not belong to the selected hospital"));
    }

    @Test
    @DisplayName("createDoctor - Should throw exception when ward not found")
    void createDoctor_WardNotFound_ThrowsException() {
        UUID nonExistentWardId = UUID.randomUUID();
        DoctorRequest request = new DoctorRequest();
        request.setDoctorName("Dr. Smith");
        request.setSpeciality(DoctorSpecialityType.SURGERY);
        request.setWardId(nonExistentWardId);

        when(wardRepository.findById(nonExistentWardId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            doctorService.createDoctor(request);
        });
        assertEquals("Ward not found", exception.getMessage());
    }

    @ParameterizedTest
    @EnumSource(DoctorSpecialityType.class)
    @DisplayName("createDoctor - Equivalence Partitioning: Test all speciality types")
    void createDoctor_AllSpecialityTypes_CreatesDoctor(DoctorSpecialityType speciality) {
        DoctorRequest request = new DoctorRequest();
        request.setDoctorName("Dr. Test");
        request.setSpeciality(speciality);

        when(doctorRepository.save(any(Doctor.class))).thenAnswer(invocation -> {
            Doctor d = invocation.getArgument(0);
            d.setDoctorId(UUID.randomUUID());
            return d;
        });

        Doctor result = doctorService.createDoctor(request);
        assertEquals(speciality, result.getSpeciality());
    }

    @Test
    @DisplayName("updateDoctor - Should update doctor with valid request")
    void updateDoctor_ValidRequest_UpdatesDoctor() {
        UUID doctorId = testDoctor.getDoctorId();
        DoctorRequest request = new DoctorRequest();
        request.setDoctorName("Updated Doctor");
        request.setSpeciality(DoctorSpecialityType.NEUROLOGY);

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(testDoctor));
        when(doctorRepository.save(any(Doctor.class))).thenReturn(testDoctor);

        Doctor result = doctorService.updateDoctor(doctorId, request);

        assertNotNull(result);
        verify(doctorRepository, times(1)).findById(doctorId);
        verify(doctorRepository, times(1)).save(testDoctor);
    }

    @Test
    @DisplayName("updateDoctor - Should throw exception when doctor not found")
    void updateDoctor_DoctorNotFound_ThrowsException() {
        UUID nonExistentId = UUID.randomUUID();
        DoctorRequest request = new DoctorRequest();
        request.setDoctorName("Updated Doctor");

        when(doctorRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            doctorService.updateDoctor(nonExistentId, request);
        });
        assertEquals("Doctor not found", exception.getMessage());
    }

    @Test
    @DisplayName("deleteDoctor - Should delete doctor when valid ID provided")
    void deleteDoctor_ValidId_DeletesDoctor() {
        UUID doctorId = testDoctor.getDoctorId();
        when(doctorRepository.existsById(doctorId)).thenReturn(true);
        doNothing().when(doctorRepository).deleteById(doctorId);

        doctorService.deleteDoctor(doctorId);

        verify(doctorRepository, times(1)).existsById(doctorId);
        verify(doctorRepository, times(1)).deleteById(doctorId);
    }

    @Test
    @DisplayName("deleteDoctor - Should throw exception when doctor not found")
    void deleteDoctor_DoctorNotFound_ThrowsException() {
        UUID nonExistentId = UUID.randomUUID();
        when(doctorRepository.existsById(nonExistentId)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            doctorService.deleteDoctor(nonExistentId);
        });
        assertEquals("Doctor not found", exception.getMessage());
    }

    @Test
    @DisplayName("getDoctorsByWardId - Should return doctors for valid ward ID")
    void getDoctorsByWardId_ValidWardId_ReturnsDoctors() {
        UUID wardId = testWard.getWardId();
        List<Doctor> doctors = Arrays.asList(testDoctor);
        when(doctorRepository.findByWardId(wardId)).thenReturn(doctors);

        List<Doctor> result = doctorService.getDoctorsByWardId(wardId);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("getDoctorsBySpeciality - Should return doctors for valid speciality (Equivalence Partitioning)")
    void getDoctorsBySpeciality_ValidSpeciality_ReturnsDoctors() {
        DoctorSpecialityType speciality = DoctorSpecialityType.CARDIOLOGY;
        List<Doctor> doctors = Arrays.asList(testDoctor);
        when(doctorRepository.findBySpeciality(speciality)).thenReturn(doctors);

        List<Doctor> result = doctorService.getDoctorsBySpeciality(speciality);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("getDoctorsBySpeciality - Should throw exception when null speciality provided")
    void getDoctorsBySpeciality_NullSpeciality_ThrowsException() {
        assertThrows(NullPointerException.class, () -> {
            doctorService.getDoctorsBySpeciality(null);
        });
    }

    @Test
    @DisplayName("getDoctorsByHospitalId - Should return doctors for valid hospital ID")
    void getDoctorsByHospitalId_ValidHospitalId_ReturnsDoctors() {
        UUID hospitalId = testHospital.getHospitalId();
        List<Doctor> doctors = Arrays.asList(testDoctor);
        when(doctorRepository.findByHospitalId(hospitalId)).thenReturn(doctors);

        List<Doctor> result = doctorService.getDoctorsByHospitalId(hospitalId);

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}

