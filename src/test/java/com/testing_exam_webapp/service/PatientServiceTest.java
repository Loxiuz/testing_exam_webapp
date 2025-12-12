package com.testing_exam_webapp.service;

import com.testing_exam_webapp.dto.PatientRequest;
import com.testing_exam_webapp.exception.EntityNotFoundException;
import com.testing_exam_webapp.exception.ValidationException;
import com.testing_exam_webapp.model.mysql.Diagnosis;
import com.testing_exam_webapp.model.mysql.Hospital;
import com.testing_exam_webapp.model.mysql.Patient;
import com.testing_exam_webapp.model.mysql.Ward;
import com.testing_exam_webapp.repository.DiagnosisRepository;
import com.testing_exam_webapp.repository.HospitalRepository;
import com.testing_exam_webapp.repository.PatientRepository;
import com.testing_exam_webapp.repository.WardRepository;
import com.testing_exam_webapp.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Comprehensive test suite for PatientService.
 * 
 * This test class demonstrates:
 * - Black Box Testing: Equivalence partitioning, boundary value analysis, decision table testing
 * - White Box Testing: Statement coverage, branch coverage, condition coverage, path coverage
 * 
 * Test Coverage Areas:
 * 1. getPatients() - Empty and populated lists
 * 2. getPatientById() - Valid ID, null ID, non-existent ID
 * 3. createPatient() - Various validation scenarios with equivalence partitioning and decision tables
 * 4. updatePatient() - Similar to create with additional non-existent ID case
 * 5. deletePatient() - Valid ID, non-existent ID, null ID
 * 6. Query methods - getPatientsByWardId(), getPatientsByHospitalId()
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PatientService Tests")
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private WardRepository wardRepository;

    @Mock
    private HospitalRepository hospitalRepository;

    @Mock
    private DiagnosisRepository diagnosisRepository;

    @InjectMocks
    private PatientService patientService;

    private Patient testPatient;
    private Hospital testHospital;
    private Ward testWard;
    private Diagnosis testDiagnosis;

    @BeforeEach
    void setUp() {
        testPatient = TestDataBuilder.createPatient();
        testHospital = TestDataBuilder.createHospital();
        testWard = TestDataBuilder.createWard();
        testDiagnosis = TestDataBuilder.createDiagnosis();
    }

    // ==================== getPatients() Tests ====================

    @Test
    @DisplayName("getPatients - Should return empty list when no patients exist")
    void getPatients_EmptyList_ReturnsEmptyList() {
        // Arrange
        when(patientRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Patient> result = patientService.getPatients();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(patientRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getPatients - Should return populated list when patients exist")
    void getPatients_PopulatedList_ReturnsList() {
        // Arrange
        List<Patient> patients = Arrays.asList(testPatient, TestDataBuilder.createPatient());
        when(patientRepository.findAll()).thenReturn(patients);

        // Act
        List<Patient> result = patientService.getPatients();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(patientRepository, times(1)).findAll();
    }

    // ==================== getPatientById() Tests ====================

    @Test
    @DisplayName("getPatientById - Should return patient when valid ID provided")
    void getPatientById_ValidId_ReturnsPatient() {
        // Arrange
        UUID patientId = testPatient.getPatientId();
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(testPatient));

        // Act
        Patient result = patientService.getPatientById(patientId);

        // Assert
        assertNotNull(result);
        assertEquals(testPatient.getPatientId(), result.getPatientId());
        verify(patientRepository, times(1)).findById(patientId);
    }

    @Test
    @DisplayName("getPatientById - Should throw exception when null ID provided")
    void getPatientById_NullId_ThrowsException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            patientService.getPatientById(null);
        });
        verify(patientRepository, never()).findById(any());
    }

    @Test
    @DisplayName("getPatientById - Should throw EntityNotFoundException when patient not found")
    void getPatientById_NonExistentId_ThrowsException() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(patientRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            patientService.getPatientById(nonExistentId);
        });
        assertEquals("Patient not found", exception.getMessage());
        verify(patientRepository, times(1)).findById(nonExistentId);
    }

    // ==================== createPatient() Tests ====================
    // These tests demonstrate Equivalence Partitioning, Boundary Analysis, and Decision Table Testing

    @Test
    @DisplayName("createPatient - Should create patient with valid request (Equivalence Partition: Valid Input)")
    void createPatient_ValidRequest_CreatesPatient() {
        // Arrange
        PatientRequest request = new PatientRequest();
        request.setPatientName("John Doe");
        request.setDateOfBirth(LocalDate.of(1990, 5, 15));
        request.setGender("Male");

        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
            Patient p = invocation.getArgument(0);
            p.setPatientId(UUID.randomUUID());
            return p;
        });

        // Act
        Patient result = patientService.createPatient(request);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getPatientId());
        assertEquals("John Doe", result.getPatientName());
        assertEquals(LocalDate.of(1990, 5, 15), result.getDateOfBirth());
        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    @DisplayName("createPatient - Should create patient with ward and hospital when both provided and ward belongs to hospital (Decision Table: Case 4)")
    void createPatient_WardAndHospitalBothProvidedAndValid_CreatesPatient() {
        // Arrange - Decision Table Test Case 4: Both provided, ward belongs to hospital
        TestDataBuilder.associateWardWithHospital(testWard, testHospital);
        
        PatientRequest request = new PatientRequest();
        request.setPatientName("John Doe");
        request.setDateOfBirth(LocalDate.of(1990, 5, 15));
        request.setWardId(testWard.getWardId());
        request.setHospitalId(testHospital.getHospitalId());

        when(wardRepository.findById(testWard.getWardId())).thenReturn(Optional.of(testWard));
        when(hospitalRepository.findById(testHospital.getHospitalId())).thenReturn(Optional.of(testHospital));
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
            Patient p = invocation.getArgument(0);
            p.setPatientId(UUID.randomUUID());
            return p;
        });

        // Act
        Patient result = patientService.createPatient(request);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getWard());
        assertNotNull(result.getHospital());
        verify(wardRepository, times(1)).findById(testWard.getWardId());
        verify(hospitalRepository, times(1)).findById(testHospital.getHospitalId());
    }

    @Test
    @DisplayName("createPatient - Should create patient when ward is null and hospital is provided (Decision Table: Case 2)")
    void createPatient_WardNullHospitalProvided_CreatesPatient() {
        // Arrange - Decision Table Test Case 2: Ward null, Hospital provided
        PatientRequest request = new PatientRequest();
        request.setPatientName("John Doe");
        request.setDateOfBirth(LocalDate.of(1990, 5, 15));
        request.setHospitalId(testHospital.getHospitalId());

        when(hospitalRepository.findById(testHospital.getHospitalId())).thenReturn(Optional.of(testHospital));
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
            Patient p = invocation.getArgument(0);
            p.setPatientId(UUID.randomUUID());
            return p;
        });

        // Act
        Patient result = patientService.createPatient(request);

        // Assert
        assertNotNull(result);
        assertNull(result.getWard());
        assertNotNull(result.getHospital());
        verify(wardRepository, never()).findById(any());
        verify(hospitalRepository, times(1)).findById(testHospital.getHospitalId());
    }

    @Test
    @DisplayName("createPatient - Should create patient when ward is provided and hospital is null (Decision Table: Case 3)")
    void createPatient_WardProvidedHospitalNull_CreatesPatient() {
        // Arrange - Decision Table Test Case 3: Ward provided, Hospital null
        PatientRequest request = new PatientRequest();
        request.setPatientName("John Doe");
        request.setDateOfBirth(LocalDate.of(1990, 5, 15));
        request.setWardId(testWard.getWardId());

        when(wardRepository.findById(testWard.getWardId())).thenReturn(Optional.of(testWard));
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
            Patient p = invocation.getArgument(0);
            p.setPatientId(UUID.randomUUID());
            return p;
        });

        // Act
        Patient result = patientService.createPatient(request);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getWard());
        assertNull(result.getHospital());
        verify(wardRepository, times(1)).findById(testWard.getWardId());
        verify(hospitalRepository, never()).findById(any());
    }

    @Test
    @DisplayName("createPatient - Should create patient when both ward and hospital are null (Decision Table: Case 1)")
    void createPatient_BothWardAndHospitalNull_CreatesPatient() {
        // Arrange - Decision Table Test Case 1: Both null
        PatientRequest request = new PatientRequest();
        request.setPatientName("John Doe");
        request.setDateOfBirth(LocalDate.of(1990, 5, 15));

        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
            Patient p = invocation.getArgument(0);
            p.setPatientId(UUID.randomUUID());
            return p;
        });

        // Act
        Patient result = patientService.createPatient(request);

        // Assert
        assertNotNull(result);
        assertNull(result.getWard());
        assertNull(result.getHospital());
        verify(wardRepository, never()).findById(any());
        verify(hospitalRepository, never()).findById(any());
    }

    @Test
    @DisplayName("createPatient - Should throw ValidationException when ward doesn't belong to hospital (Decision Table: Case 5)")
    void createPatient_WardDoesNotBelongToHospital_ThrowsValidationException() {
        // Arrange - Decision Table Test Case 5: Both provided, ward doesn't belong to hospital
        Ward otherWard = TestDataBuilder.createWard();
        // Don't associate ward with hospital
        
        PatientRequest request = new PatientRequest();
        request.setPatientName("John Doe");
        request.setDateOfBirth(LocalDate.of(1990, 5, 15));
        request.setWardId(otherWard.getWardId());
        request.setHospitalId(testHospital.getHospitalId());

        when(wardRepository.findById(otherWard.getWardId())).thenReturn(Optional.of(otherWard));
        when(hospitalRepository.findById(testHospital.getHospitalId())).thenReturn(Optional.of(testHospital));

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            patientService.createPatient(request);
        });
        assertTrue(exception.getMessage().contains("does not belong to the selected hospital"));
        verify(patientRepository, never()).save(any());
    }

    @Test
    @DisplayName("createPatient - Should throw EntityNotFoundException when ward doesn't exist (Decision Table: Case 6)")
    void createPatient_WardNotFound_ThrowsEntityNotFoundException() {
        // Arrange - Decision Table Test Case 6: Ward doesn't exist
        UUID nonExistentWardId = UUID.randomUUID();
        PatientRequest request = new PatientRequest();
        request.setPatientName("John Doe");
        request.setDateOfBirth(LocalDate.of(1990, 5, 15));
        request.setWardId(nonExistentWardId);

        when(wardRepository.findById(nonExistentWardId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            patientService.createPatient(request);
        });
        assertEquals("Ward not found", exception.getMessage());
        verify(patientRepository, never()).save(any());
    }

    @Test
    @DisplayName("createPatient - Should throw EntityNotFoundException when hospital doesn't exist (Decision Table: Case 7)")
    void createPatient_HospitalNotFound_ThrowsEntityNotFoundException() {
        // Arrange - Decision Table Test Case 7: Hospital doesn't exist
        UUID nonExistentHospitalId = UUID.randomUUID();
        PatientRequest request = new PatientRequest();
        request.setPatientName("John Doe");
        request.setDateOfBirth(LocalDate.of(1990, 5, 15));
        request.setHospitalId(nonExistentHospitalId);

        when(hospitalRepository.findById(nonExistentHospitalId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            patientService.createPatient(request);
        });
        assertEquals("Hospital not found", exception.getMessage());
        verify(patientRepository, never()).save(any());
    }

    // ==================== Boundary Value Analysis Tests ====================

    static java.util.stream.Stream<Arguments> dateOfBirthBoundaryValues() {
        return java.util.stream.Stream.of(
            Arguments.of(LocalDate.now().minusDays(1), "Yesterday (just past boundary)"),
            Arguments.of(LocalDate.of(1900, 1, 1), "Far past (1900)"),
            Arguments.of(LocalDate.of(2000, 6, 15), "Recent past (2000)")
        );
    }

    @ParameterizedTest
    @MethodSource("dateOfBirthBoundaryValues")
    @DisplayName("createPatient - Boundary Analysis: Date of birth boundary values")
    void createPatient_DateOfBirthBoundaryValues_CreatesPatient(LocalDate dateOfBirth, String description) {
        // Arrange - Boundary Test
        PatientRequest request = new PatientRequest();
        request.setPatientName("John Doe");
        request.setDateOfBirth(dateOfBirth);
        request.setGender("Male");

        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
            Patient p = invocation.getArgument(0);
            p.setPatientId(UUID.randomUUID());
            return p;
        });

        // Act
        Patient result = patientService.createPatient(request);

        // Assert
        assertNotNull(result);
        assertEquals(dateOfBirth, result.getDateOfBirth());
    }

    @Test
    @DisplayName("createPatient - Should create patient with diagnosis IDs")
    void createPatient_WithDiagnosisIds_CreatesPatient() {
        // Arrange
        Set<UUID> diagnosisIds = Set.of(testDiagnosis.getDiagnosisId());
        PatientRequest request = new PatientRequest();
        request.setPatientName("John Doe");
        request.setDateOfBirth(LocalDate.of(1990, 5, 15));
        request.setDiagnosisIds(diagnosisIds);

        when(diagnosisRepository.findById(testDiagnosis.getDiagnosisId())).thenReturn(Optional.of(testDiagnosis));
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
            Patient p = invocation.getArgument(0);
            p.setPatientId(UUID.randomUUID());
            return p;
        });

        // Act
        Patient result = patientService.createPatient(request);

        // Assert
        assertNotNull(result);
        verify(diagnosisRepository, times(1)).findById(testDiagnosis.getDiagnosisId());
    }

    @Test
    @DisplayName("createPatient - Should throw exception when diagnosis not found")
    void createPatient_DiagnosisNotFound_ThrowsException() {
        // Arrange
        UUID nonExistentDiagnosisId = UUID.randomUUID();
        Set<UUID> diagnosisIds = Set.of(nonExistentDiagnosisId);
        PatientRequest request = new PatientRequest();
        request.setPatientName("John Doe");
        request.setDateOfBirth(LocalDate.of(1990, 5, 15));
        request.setDiagnosisIds(diagnosisIds);

        when(diagnosisRepository.findById(nonExistentDiagnosisId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            patientService.createPatient(request);
        });
        assertTrue(exception.getMessage().contains("Diagnosis not found"));
    }

    // ==================== updatePatient() Tests ====================

    @Test
    @DisplayName("updatePatient - Should update patient with valid request")
    void updatePatient_ValidRequest_UpdatesPatient() {
        // Arrange
        UUID patientId = testPatient.getPatientId();
        PatientRequest request = new PatientRequest();
        request.setPatientName("Updated Name");
        request.setDateOfBirth(LocalDate.of(1995, 6, 20));
        request.setGender("Female");

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(testPatient));
        when(patientRepository.save(any(Patient.class))).thenReturn(testPatient);

        // Act
        Patient result = patientService.updatePatient(patientId, request);

        // Assert
        assertNotNull(result);
        verify(patientRepository, times(1)).findById(patientId);
        verify(patientRepository, times(1)).save(testPatient);
    }

    @Test
    @DisplayName("updatePatient - Should throw exception when patient not found")
    void updatePatient_PatientNotFound_ThrowsException() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        PatientRequest request = new PatientRequest();
        request.setPatientName("Updated Name");

        when(patientRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            patientService.updatePatient(nonExistentId, request);
        });
        assertEquals("Patient not found", exception.getMessage());
        verify(patientRepository, never()).save(any());
    }

    @Test
    @DisplayName("updatePatient - Should throw exception when null ID provided")
    void updatePatient_NullId_ThrowsException() {
        // Arrange
        PatientRequest request = new PatientRequest();

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            patientService.updatePatient(null, request);
        });
    }

    @Test
    @DisplayName("updatePatient - Should validate ward-hospital relationship")
    void updatePatient_WardHospitalMismatch_ThrowsValidationException() {
        // Arrange
        UUID patientId = testPatient.getPatientId();
        Ward otherWard = TestDataBuilder.createWard();
        
        PatientRequest request = new PatientRequest();
        request.setPatientName("Updated Name");
        request.setDateOfBirth(LocalDate.of(1990, 5, 15));
        request.setWardId(otherWard.getWardId());
        request.setHospitalId(testHospital.getHospitalId());

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(testPatient));
        when(wardRepository.findById(otherWard.getWardId())).thenReturn(Optional.of(otherWard));
        when(hospitalRepository.findById(testHospital.getHospitalId())).thenReturn(Optional.of(testHospital));

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            patientService.updatePatient(patientId, request);
        });
        assertTrue(exception.getMessage().contains("does not belong to the selected hospital"));
    }

    // ==================== deletePatient() Tests ====================

    @Test
    @DisplayName("deletePatient - Should delete patient when valid ID provided")
    void deletePatient_ValidId_DeletesPatient() {
        // Arrange
        UUID patientId = testPatient.getPatientId();
        when(patientRepository.existsById(patientId)).thenReturn(true);
        doNothing().when(patientRepository).deleteById(patientId);

        // Act
        patientService.deletePatient(patientId);

        // Assert
        verify(patientRepository, times(1)).existsById(patientId);
        verify(patientRepository, times(1)).deleteById(patientId);
    }

    @Test
    @DisplayName("deletePatient - Should throw exception when patient not found")
    void deletePatient_PatientNotFound_ThrowsException() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(patientRepository.existsById(nonExistentId)).thenReturn(false);

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            patientService.deletePatient(nonExistentId);
        });
        assertEquals("Patient not found", exception.getMessage());
        verify(patientRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("deletePatient - Should throw exception when null ID provided")
    void deletePatient_NullId_ThrowsException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            patientService.deletePatient(null);
        });
        verify(patientRepository, never()).deleteById(any());
    }

    // ==================== Query Methods Tests ====================

    @Test
    @DisplayName("getPatientsByWardId - Should return patients for valid ward ID")
    void getPatientsByWardId_ValidWardId_ReturnsPatients() {
        // Arrange
        UUID wardId = testWard.getWardId();
        List<Patient> patients = Arrays.asList(testPatient);
        when(patientRepository.findByWardId(wardId)).thenReturn(patients);

        // Act
        List<Patient> result = patientService.getPatientsByWardId(wardId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(patientRepository, times(1)).findByWardId(wardId);
    }

    @Test
    @DisplayName("getPatientsByWardId - Should throw exception when null ward ID provided")
    void getPatientsByWardId_NullWardId_ThrowsException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            patientService.getPatientsByWardId(null);
        });
    }

    @Test
    @DisplayName("getPatientsByHospitalId - Should return patients for valid hospital ID")
    void getPatientsByHospitalId_ValidHospitalId_ReturnsPatients() {
        // Arrange
        UUID hospitalId = testHospital.getHospitalId();
        List<Patient> patients = Arrays.asList(testPatient);
        when(patientRepository.findByHospitalId(hospitalId)).thenReturn(patients);

        // Act
        List<Patient> result = patientService.getPatientsByHospitalId(hospitalId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(patientRepository, times(1)).findByHospitalId(hospitalId);
    }

    @Test
    @DisplayName("getPatientsByHospitalId - Should throw exception when null hospital ID provided")
    void getPatientsByHospitalId_NullHospitalId_ThrowsException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            patientService.getPatientsByHospitalId(null);
        });
    }
}

