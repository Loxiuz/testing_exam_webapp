package com.testing_exam_webapp.service;

import com.testing_exam_webapp.dto.PatientRequest;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * WHITE BOX TESTING - PatientService
 * 
 * This test class explicitly tests internal implementation details:
 * - Branch Coverage: All if/else branches
 * - Condition Coverage: All compound conditions (AND, OR)
 * - Path Coverage: All possible execution paths
 * - Statement Coverage: Every statement executed
 * 
 * White Box Testing Techniques Demonstrated:
 * 1. Branch Coverage: Tests every branch in conditional statements
 * 2. Condition Coverage: Tests all combinations of compound conditions
 * 3. Path Coverage: Tests all possible execution paths through the code
 * 4. Statement Coverage: Ensures every line of code is executed
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PatientService - White Box Tests")
class PatientServiceWhiteBoxTest {

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

    private Ward testWard;
    private Hospital testHospital;
    private Diagnosis testDiagnosis;

    @BeforeEach
    void setUp() {
        testWard = TestDataBuilder.createWard();
        testHospital = TestDataBuilder.createHospital();
        testDiagnosis = TestDataBuilder.createDiagnosis();
    }

    // ==================== BRANCH COVERAGE TESTS ====================
    // Testing all branches in createPatient() method

    /**
     * WHITE BOX: Branch Coverage Test
     * Branch: if (request.getDiagnosisIds() != null) - FALSE branch
     * Tests the path where diagnosisIds is null (branch not taken)
     */
    @Test
    @DisplayName("createPatient - Branch Coverage: diagnosisIds null branch (FALSE)")
    void createPatient_DiagnosisIdsNull_BranchNotTaken() {
        // Arrange - Branch: if (request.getDiagnosisIds() != null) -> FALSE
        PatientRequest request = new PatientRequest();
        request.setPatientName("John Doe");
        request.setDateOfBirth(java.time.LocalDate.of(1990, 1, 1));
        request.setGender("Male");
        request.setDiagnosisIds(null); // Explicitly null to test FALSE branch

        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
            Patient p = invocation.getArgument(0);
            p.setPatientId(UUID.randomUUID());
            return p;
        });

        // Act
        Patient result = patientService.createPatient(request);

        // Assert - Verify the FALSE branch was taken (no diagnosis processing)
        assertNotNull(result);
        assertNull(result.getDiagnosis()); // Branch not taken, diagnosis remains null
        verify(diagnosisRepository, never()).findById(any()); // Verify diagnosis branch not executed
    }

    /**
     * WHITE BOX: Branch Coverage Test
     * Branch: if (request.getDiagnosisIds() != null) - TRUE branch
     * Tests the path where diagnosisIds is not null (branch taken)
     */
    @Test
    @DisplayName("createPatient - Branch Coverage: diagnosisIds not null branch (TRUE)")
    void createPatient_DiagnosisIdsNotNull_BranchTaken() {
        // Arrange - Branch: if (request.getDiagnosisIds() != null) -> TRUE
        PatientRequest request = new PatientRequest();
        request.setPatientName("John Doe");
        request.setDateOfBirth(java.time.LocalDate.of(1990, 1, 1));
        request.setGender("Male");
        request.setDiagnosisIds(new HashSet<>(Arrays.asList(testDiagnosis.getDiagnosisId()))); // TRUE branch

        when(diagnosisRepository.findById(testDiagnosis.getDiagnosisId()))
                .thenReturn(Optional.of(testDiagnosis));
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
            Patient p = invocation.getArgument(0);
            p.setPatientId(UUID.randomUUID());
            return p;
        });

        // Act
        Patient result = patientService.createPatient(request);

        // Assert - Verify the TRUE branch was taken (diagnosis processing executed)
        assertNotNull(result);
        assertNotNull(result.getDiagnosis()); // Branch taken, diagnosis set
        assertEquals(1, result.getDiagnosis().size()); // Verify diagnosis was added
        verify(diagnosisRepository, times(1)).findById(testDiagnosis.getDiagnosisId()); // Verify branch executed
    }

    /**
     * WHITE BOX: Branch Coverage Test
     * Branch: if (wardId != null) - FALSE branch
     * Tests the path where wardId is null
     */
    @Test
    @DisplayName("createPatient - Branch Coverage: wardId null branch (FALSE)")
    void createPatient_WardIdNull_BranchNotTaken() {
        // Arrange - Branch: if (wardId != null) -> FALSE
        PatientRequest request = new PatientRequest();
        request.setPatientName("John Doe");
        request.setDateOfBirth(java.time.LocalDate.of(1990, 1, 1));
        request.setGender("Male");
        request.setWardId(null); // Explicitly null to test FALSE branch

        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
            Patient p = invocation.getArgument(0);
            p.setPatientId(UUID.randomUUID());
            return p;
        });

        // Act
        Patient result = patientService.createPatient(request);

        // Assert - Verify the FALSE branch was taken
        assertNotNull(result);
        assertNull(result.getWard()); // Branch not taken, ward remains null
        verify(wardRepository, never()).findById(any()); // Verify ward branch not executed
    }

    /**
     * WHITE BOX: Branch Coverage Test
     * Branch: if (wardId != null) - TRUE branch
     * Tests the path where wardId is not null
     */
    @Test
    @DisplayName("createPatient - Branch Coverage: wardId not null branch (TRUE)")
    void createPatient_WardIdNotNull_BranchTaken() {
        // Arrange - Branch: if (wardId != null) -> TRUE
        PatientRequest request = new PatientRequest();
        request.setPatientName("John Doe");
        request.setDateOfBirth(java.time.LocalDate.of(1990, 1, 1));
        request.setGender("Male");
        request.setWardId(testWard.getWardId()); // TRUE branch

        when(wardRepository.findById(testWard.getWardId()))
                .thenReturn(Optional.of(testWard));
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
            Patient p = invocation.getArgument(0);
            p.setPatientId(UUID.randomUUID());
            return p;
        });

        // Act
        Patient result = patientService.createPatient(request);

        // Assert - Verify the TRUE branch was taken
        assertNotNull(result);
        assertNotNull(result.getWard()); // Branch taken, ward set
        verify(wardRepository, times(1)).findById(testWard.getWardId()); // Verify branch executed
    }

    /**
     * WHITE BOX: Branch Coverage Test
     * Branch: if (hospitalId != null) - FALSE branch
     */
    @Test
    @DisplayName("createPatient - Branch Coverage: hospitalId null branch (FALSE)")
    void createPatient_HospitalIdNull_BranchNotTaken() {
        // Arrange - Branch: if (hospitalId != null) -> FALSE
        PatientRequest request = new PatientRequest();
        request.setPatientName("John Doe");
        request.setDateOfBirth(java.time.LocalDate.of(1990, 1, 1));
        request.setGender("Male");
        request.setHospitalId(null); // Explicitly null to test FALSE branch

        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
            Patient p = invocation.getArgument(0);
            p.setPatientId(UUID.randomUUID());
            return p;
        });

        // Act
        Patient result = patientService.createPatient(request);

        // Assert
        assertNotNull(result);
        assertNull(result.getHospital()); // Branch not taken
        verify(hospitalRepository, never()).findById(any()); // Verify branch not executed
    }

    /**
     * WHITE BOX: Branch Coverage Test
     * Branch: if (hospitalId != null) - TRUE branch
     */
    @Test
    @DisplayName("createPatient - Branch Coverage: hospitalId not null branch (TRUE)")
    void createPatient_HospitalIdNotNull_BranchTaken() {
        // Arrange - Branch: if (hospitalId != null) -> TRUE
        PatientRequest request = new PatientRequest();
        request.setPatientName("John Doe");
        request.setDateOfBirth(java.time.LocalDate.of(1990, 1, 1));
        request.setGender("Male");
        request.setHospitalId(testHospital.getHospitalId()); // TRUE branch

        when(hospitalRepository.findById(testHospital.getHospitalId()))
                .thenReturn(Optional.of(testHospital));
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
            Patient p = invocation.getArgument(0);
            p.setPatientId(UUID.randomUUID());
            return p;
        });

        // Act
        Patient result = patientService.createPatient(request);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getHospital()); // Branch taken
        verify(hospitalRepository, times(1)).findById(testHospital.getHospitalId()); // Verify branch executed
    }

    // ==================== CONDITION COVERAGE TESTS ====================
    // Testing compound conditions: (ward != null && hospital != null)

    /**
     * WHITE BOX: Condition Coverage Test
     * Condition: (ward != null && hospital != null)
     * Test Case: ward = null, hospital = null -> (FALSE && FALSE) = FALSE
     */
    @Test
    @DisplayName("createPatient - Condition Coverage: ward null AND hospital null (FALSE && FALSE)")
    void createPatient_WardNullAndHospitalNull_ConditionFalse() {
        // Arrange - Condition: (ward != null && hospital != null) -> (FALSE && FALSE) = FALSE
        PatientRequest request = new PatientRequest();
        request.setPatientName("John Doe");
        request.setDateOfBirth(java.time.LocalDate.of(1990, 1, 1));
        request.setGender("Male");
        request.setWardId(null);
        request.setHospitalId(null);

        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
            Patient p = invocation.getArgument(0);
            p.setPatientId(UUID.randomUUID());
            return p;
        });

        // Act
        Patient result = patientService.createPatient(request);

        // Assert - Condition evaluates to FALSE, validation branch not taken
        assertNotNull(result);
        assertNull(result.getWard());
        assertNull(result.getHospital());
        // Verify validation logic (ward-hospital check) was NOT executed
        verify(wardRepository, never()).findById(any());
        verify(hospitalRepository, never()).findById(any());
    }

    /**
     * WHITE BOX: Condition Coverage Test
     * Condition: (ward != null && hospital != null)
     * Test Case: ward = not null, hospital = null -> (TRUE && FALSE) = FALSE
     */
    @Test
    @DisplayName("createPatient - Condition Coverage: ward not null AND hospital null (TRUE && FALSE)")
    void createPatient_WardNotNullAndHospitalNull_ConditionFalse() {
        // Arrange - Condition: (ward != null && hospital != null) -> (TRUE && FALSE) = FALSE
        PatientRequest request = new PatientRequest();
        request.setPatientName("John Doe");
        request.setDateOfBirth(java.time.LocalDate.of(1990, 1, 1));
        request.setGender("Male");
        request.setWardId(testWard.getWardId());
        request.setHospitalId(null);

        when(wardRepository.findById(testWard.getWardId()))
                .thenReturn(Optional.of(testWard));
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
            Patient p = invocation.getArgument(0);
            p.setPatientId(UUID.randomUUID());
            return p;
        });

        // Act
        Patient result = patientService.createPatient(request);

        // Assert - Condition evaluates to FALSE, validation branch not taken
        assertNotNull(result);
        assertNotNull(result.getWard());
        assertNull(result.getHospital());
        // Validation logic should NOT execute because condition is FALSE
    }

    /**
     * WHITE BOX: Condition Coverage Test
     * Condition: (ward != null && hospital != null)
     * Test Case: ward = null, hospital = not null -> (FALSE && TRUE) = FALSE
     */
    @Test
    @DisplayName("createPatient - Condition Coverage: ward null AND hospital not null (FALSE && TRUE)")
    void createPatient_WardNullAndHospitalNotNull_ConditionFalse() {
        // Arrange - Condition: (ward != null && hospital != null) -> (FALSE && TRUE) = FALSE
        PatientRequest request = new PatientRequest();
        request.setPatientName("John Doe");
        request.setDateOfBirth(java.time.LocalDate.of(1990, 1, 1));
        request.setGender("Male");
        request.setWardId(null);
        request.setHospitalId(testHospital.getHospitalId());

        when(hospitalRepository.findById(testHospital.getHospitalId()))
                .thenReturn(Optional.of(testHospital));
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
            Patient p = invocation.getArgument(0);
            p.setPatientId(UUID.randomUUID());
            return p;
        });

        // Act
        Patient result = patientService.createPatient(request);

        // Assert - Condition evaluates to FALSE, validation branch not taken
        assertNotNull(result);
        assertNull(result.getWard());
        assertNotNull(result.getHospital());
    }

    /**
     * WHITE BOX: Condition Coverage Test
     * Condition: (ward != null && hospital != null)
     * Test Case: ward = not null, hospital = not null -> (TRUE && TRUE) = TRUE
     * Sub-condition: ward.getHospitals() != null -> TRUE
     * Sub-condition: stream().anyMatch(...) -> TRUE (ward belongs to hospital)
     */
    @Test
    @DisplayName("createPatient - Condition Coverage: ward not null AND hospital not null, ward belongs (TRUE && TRUE, validation TRUE)")
    void createPatient_WardNotNullAndHospitalNotNull_WardBelongs_ConditionTrue() {
        // Arrange - Condition: (ward != null && hospital != null) -> (TRUE && TRUE) = TRUE
        // Sub-condition: ward.getHospitals() != null -> TRUE
        // Sub-condition: anyMatch returns TRUE (ward belongs to hospital)
        TestDataBuilder.associateWardWithHospital(testWard, testHospital);
        
        PatientRequest request = new PatientRequest();
        request.setPatientName("John Doe");
        request.setDateOfBirth(java.time.LocalDate.of(1990, 1, 1));
        request.setGender("Male");
        request.setWardId(testWard.getWardId());
        request.setHospitalId(testHospital.getHospitalId());

        when(wardRepository.findById(testWard.getWardId()))
                .thenReturn(Optional.of(testWard));
        when(hospitalRepository.findById(testHospital.getHospitalId()))
                .thenReturn(Optional.of(testHospital));
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
            Patient p = invocation.getArgument(0);
            p.setPatientId(UUID.randomUUID());
            return p;
        });

        // Act
        Patient result = patientService.createPatient(request);

        // Assert - Condition TRUE, validation passed (ward belongs to hospital)
        assertNotNull(result);
        assertNotNull(result.getWard());
        assertNotNull(result.getHospital());
        // No exception thrown, validation passed
    }

    /**
     * WHITE BOX: Condition Coverage Test
     * Condition: (ward != null && hospital != null) -> TRUE
     * Sub-condition: ward.getHospitals() != null -> FALSE (null hospitals set)
     */
    @Test
    @DisplayName("createPatient - Condition Coverage: ward hospitals null (sub-condition FALSE)")
    void createPatient_WardHospitalsNull_SubConditionFalse() {
        // Arrange - Condition: (ward != null && hospital != null) -> TRUE
        // Sub-condition: ward.getHospitals() != null -> FALSE (hospitals is null)
        testWard.setHospitals(null); // Explicitly set to null
        
        PatientRequest request = new PatientRequest();
        request.setPatientName("John Doe");
        request.setDateOfBirth(java.time.LocalDate.of(1990, 1, 1));
        request.setGender("Male");
        request.setWardId(testWard.getWardId());
        request.setHospitalId(testHospital.getHospitalId());

        when(wardRepository.findById(testWard.getWardId()))
                .thenReturn(Optional.of(testWard));
        when(hospitalRepository.findById(testHospital.getHospitalId()))
                .thenReturn(Optional.of(testHospital));

        // Act & Assert - Sub-condition FALSE, validation fails
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            patientService.createPatient(request);
        });
        assertTrue(exception.getMessage().contains("does not belong to the selected hospital"));
    }

    /**
     * WHITE BOX: Condition Coverage Test
     * Condition: (ward != null && hospital != null) -> TRUE
     * Sub-condition: ward.getHospitals() != null -> TRUE
     * Sub-condition: stream().anyMatch(...) -> FALSE (ward doesn't belong to hospital)
     */
    @Test
    @DisplayName("createPatient - Condition Coverage: ward doesn't belong to hospital (anyMatch FALSE)")
    void createPatient_WardDoesNotBelongToHospital_AnyMatchFalse() {
        // Arrange - Condition: (ward != null && hospital != null) -> TRUE
        // Sub-condition: ward.getHospitals() != null -> TRUE
        // Sub-condition: anyMatch returns FALSE (ward doesn't belong to hospital)
        // Ward has hospitals, but not the one we're checking
        Hospital otherHospital = TestDataBuilder.createHospital();
        otherHospital.setHospitalName("Other Hospital");
        TestDataBuilder.associateWardWithHospital(testWard, otherHospital);
        // testHospital is NOT associated with testWard
        
        PatientRequest request = new PatientRequest();
        request.setPatientName("John Doe");
        request.setDateOfBirth(java.time.LocalDate.of(1990, 1, 1));
        request.setGender("Male");
        request.setWardId(testWard.getWardId());
        request.setHospitalId(testHospital.getHospitalId());

        when(wardRepository.findById(testWard.getWardId()))
                .thenReturn(Optional.of(testWard));
        when(hospitalRepository.findById(testHospital.getHospitalId()))
                .thenReturn(Optional.of(testHospital));

        // Act & Assert - anyMatch returns FALSE, validation fails
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            patientService.createPatient(request);
        });
        assertTrue(exception.getMessage().contains("does not belong to the selected hospital"));
    }

    // ==================== PATH COVERAGE TESTS ====================
    // Testing all possible execution paths through createPatient()

    /**
     * WHITE BOX: Path Coverage Test
     * Path 1: No diagnosis, no ward, no hospital
     * Executes: diagnosisIds null branch, wardId null branch, hospitalId null branch, validation branch not taken
     */
    @Test
    @DisplayName("createPatient - Path Coverage: Path 1 (no diagnosis, no ward, no hospital)")
    void createPatient_Path1_NoDiagnosisNoWardNoHospital() {
        // Arrange - Path: diagnosisIds=null, wardId=null, hospitalId=null
        PatientRequest request = new PatientRequest();
        request.setPatientName("John Doe");
        request.setDateOfBirth(java.time.LocalDate.of(1990, 1, 1));
        request.setGender("Male");
        // All optional fields null

        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
            Patient p = invocation.getArgument(0);
            p.setPatientId(UUID.randomUUID());
            return p;
        });

        // Act
        Patient result = patientService.createPatient(request);

        // Assert - Verify this specific path was taken
        assertNotNull(result);
        assertNull(result.getDiagnosis());
        assertNull(result.getWard());
        assertNull(result.getHospital());
        verify(diagnosisRepository, never()).findById(any());
        verify(wardRepository, never()).findById(any());
        verify(hospitalRepository, never()).findById(any());
    }

    /**
     * WHITE BOX: Path Coverage Test
     * Path 2: With diagnosis, no ward, no hospital
     */
    @Test
    @DisplayName("createPatient - Path Coverage: Path 2 (with diagnosis, no ward, no hospital)")
    void createPatient_Path2_WithDiagnosisNoWardNoHospital() {
        // Arrange - Path: diagnosisIds=not null, wardId=null, hospitalId=null
        PatientRequest request = new PatientRequest();
        request.setPatientName("John Doe");
        request.setDateOfBirth(java.time.LocalDate.of(1990, 1, 1));
        request.setGender("Male");
        request.setDiagnosisIds(new HashSet<>(Arrays.asList(testDiagnosis.getDiagnosisId())));

        when(diagnosisRepository.findById(testDiagnosis.getDiagnosisId()))
                .thenReturn(Optional.of(testDiagnosis));
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
            Patient p = invocation.getArgument(0);
            p.setPatientId(UUID.randomUUID());
            return p;
        });

        // Act
        Patient result = patientService.createPatient(request);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getDiagnosis());
        assertNull(result.getWard());
        assertNull(result.getHospital());
        verify(diagnosisRepository, times(1)).findById(any());
        verify(wardRepository, never()).findById(any());
        verify(hospitalRepository, never()).findById(any());
    }

    /**
     * WHITE BOX: Path Coverage Test
     * Path 3: No diagnosis, with ward, no hospital
     */
    @Test
    @DisplayName("createPatient - Path Coverage: Path 3 (no diagnosis, with ward, no hospital)")
    void createPatient_Path3_NoDiagnosisWithWardNoHospital() {
        // Arrange - Path: diagnosisIds=null, wardId=not null, hospitalId=null
        PatientRequest request = new PatientRequest();
        request.setPatientName("John Doe");
        request.setDateOfBirth(java.time.LocalDate.of(1990, 1, 1));
        request.setGender("Male");
        request.setWardId(testWard.getWardId());

        when(wardRepository.findById(testWard.getWardId()))
                .thenReturn(Optional.of(testWard));
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
            Patient p = invocation.getArgument(0);
            p.setPatientId(UUID.randomUUID());
            return p;
        });

        // Act
        Patient result = patientService.createPatient(request);

        // Assert
        assertNotNull(result);
        assertNull(result.getDiagnosis());
        assertNotNull(result.getWard());
        assertNull(result.getHospital());
        verify(diagnosisRepository, never()).findById(any());
        verify(wardRepository, times(1)).findById(any());
        verify(hospitalRepository, never()).findById(any());
    }

    /**
     * WHITE BOX: Path Coverage Test
     * Path 4: No diagnosis, no ward, with hospital
     */
    @Test
    @DisplayName("createPatient - Path Coverage: Path 4 (no diagnosis, no ward, with hospital)")
    void createPatient_Path4_NoDiagnosisNoWardWithHospital() {
        // Arrange - Path: diagnosisIds=null, wardId=null, hospitalId=not null
        PatientRequest request = new PatientRequest();
        request.setPatientName("John Doe");
        request.setDateOfBirth(java.time.LocalDate.of(1990, 1, 1));
        request.setGender("Male");
        request.setHospitalId(testHospital.getHospitalId());

        when(hospitalRepository.findById(testHospital.getHospitalId()))
                .thenReturn(Optional.of(testHospital));
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
            Patient p = invocation.getArgument(0);
            p.setPatientId(UUID.randomUUID());
            return p;
        });

        // Act
        Patient result = patientService.createPatient(request);

        // Assert
        assertNotNull(result);
        assertNull(result.getDiagnosis());
        assertNull(result.getWard());
        assertNotNull(result.getHospital());
        verify(diagnosisRepository, never()).findById(any());
        verify(wardRepository, never()).findById(any());
        verify(hospitalRepository, times(1)).findById(any());
    }

    /**
     * WHITE BOX: Path Coverage Test
     * Path 5: No diagnosis, with ward, with hospital (ward belongs) - validation passes
     */
    @Test
    @DisplayName("createPatient - Path Coverage: Path 5 (no diagnosis, with ward, with hospital, validation passes)")
    void createPatient_Path5_NoDiagnosisWithWardWithHospital_ValidationPasses() {
        // Arrange - Path: diagnosisIds=null, wardId=not null, hospitalId=not null, validation passes
        TestDataBuilder.associateWardWithHospital(testWard, testHospital);
        
        PatientRequest request = new PatientRequest();
        request.setPatientName("John Doe");
        request.setDateOfBirth(java.time.LocalDate.of(1990, 1, 1));
        request.setGender("Male");
        request.setWardId(testWard.getWardId());
        request.setHospitalId(testHospital.getHospitalId());

        when(wardRepository.findById(testWard.getWardId()))
                .thenReturn(Optional.of(testWard));
        when(hospitalRepository.findById(testHospital.getHospitalId()))
                .thenReturn(Optional.of(testHospital));
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
            Patient p = invocation.getArgument(0);
            p.setPatientId(UUID.randomUUID());
            return p;
        });

        // Act
        Patient result = patientService.createPatient(request);

        // Assert - Validation path taken and passed
        assertNotNull(result);
        assertNull(result.getDiagnosis());
        assertNotNull(result.getWard());
        assertNotNull(result.getHospital());
        // No exception, validation passed
    }

    /**
     * WHITE BOX: Path Coverage Test
     * Path 6: No diagnosis, with ward, with hospital (ward doesn't belong) - validation fails
     */
    @Test
    @DisplayName("createPatient - Path Coverage: Path 6 (no diagnosis, with ward, with hospital, validation fails)")
    void createPatient_Path6_NoDiagnosisWithWardWithHospital_ValidationFails() {
        // Arrange - Path: diagnosisIds=null, wardId=not null, hospitalId=not null, validation fails
        // Ward is not associated with hospital
        testWard.setHospitals(new HashSet<>()); // Empty set
        
        PatientRequest request = new PatientRequest();
        request.setPatientName("John Doe");
        request.setDateOfBirth(java.time.LocalDate.of(1990, 1, 1));
        request.setGender("Male");
        request.setWardId(testWard.getWardId());
        request.setHospitalId(testHospital.getHospitalId());

        when(wardRepository.findById(testWard.getWardId()))
                .thenReturn(Optional.of(testWard));
        when(hospitalRepository.findById(testHospital.getHospitalId()))
                .thenReturn(Optional.of(testHospital));

        // Act & Assert - Validation path taken and failed
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            patientService.createPatient(request);
        });
        assertTrue(exception.getMessage().contains("does not belong to the selected hospital"));
    }

    /**
     * WHITE BOX: Path Coverage Test
     * Path 7: With diagnosis, with ward, with hospital (ward belongs) - all branches taken
     */
    @Test
    @DisplayName("createPatient - Path Coverage: Path 7 (with diagnosis, with ward, with hospital, validation passes)")
    void createPatient_Path7_WithDiagnosisWithWardWithHospital_AllBranchesTaken() {
        // Arrange - Path: All optional fields provided, validation passes
        TestDataBuilder.associateWardWithHospital(testWard, testHospital);
        
        PatientRequest request = new PatientRequest();
        request.setPatientName("John Doe");
        request.setDateOfBirth(java.time.LocalDate.of(1990, 1, 1));
        request.setGender("Male");
        request.setDiagnosisIds(new HashSet<>(Arrays.asList(testDiagnosis.getDiagnosisId())));
        request.setWardId(testWard.getWardId());
        request.setHospitalId(testHospital.getHospitalId());

        when(diagnosisRepository.findById(testDiagnosis.getDiagnosisId()))
                .thenReturn(Optional.of(testDiagnosis));
        when(wardRepository.findById(testWard.getWardId()))
                .thenReturn(Optional.of(testWard));
        when(hospitalRepository.findById(testHospital.getHospitalId()))
                .thenReturn(Optional.of(testHospital));
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
            Patient p = invocation.getArgument(0);
            p.setPatientId(UUID.randomUUID());
            return p;
        });

        // Act
        Patient result = patientService.createPatient(request);

        // Assert - All branches executed
        assertNotNull(result);
        assertNotNull(result.getDiagnosis());
        assertNotNull(result.getWard());
        assertNotNull(result.getHospital());
        verify(diagnosisRepository, times(1)).findById(any());
        verify(wardRepository, times(1)).findById(any());
        verify(hospitalRepository, times(1)).findById(any());
    }

    // ==================== STATEMENT COVERAGE TESTS ====================
    // Ensuring every statement is executed

    /**
     * WHITE BOX: Statement Coverage Test
     * Tests that the for-loop in diagnosis processing executes (statement coverage)
     */
    @Test
    @DisplayName("createPatient - Statement Coverage: for-loop with multiple diagnoses")
    void createPatient_MultipleDiagnoses_ForLoopStatementsExecuted() {
        // Arrange - Multiple diagnoses to test for-loop statement coverage
        Diagnosis diagnosis1 = TestDataBuilder.createDiagnosis();
        Diagnosis diagnosis2 = TestDataBuilder.createDiagnosis();
        diagnosis2.setDiagnosisId(UUID.randomUUID());
        
        PatientRequest request = new PatientRequest();
        request.setPatientName("John Doe");
        request.setDateOfBirth(java.time.LocalDate.of(1990, 1, 1));
        request.setGender("Male");
        request.setDiagnosisIds(new HashSet<>(Arrays.asList(diagnosis1.getDiagnosisId(), diagnosis2.getDiagnosisId())));

        when(diagnosisRepository.findById(diagnosis1.getDiagnosisId()))
                .thenReturn(Optional.of(diagnosis1));
        when(diagnosisRepository.findById(diagnosis2.getDiagnosisId()))
                .thenReturn(Optional.of(diagnosis2));
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
            Patient p = invocation.getArgument(0);
            p.setPatientId(UUID.randomUUID());
            return p;
        });

        // Act
        Patient result = patientService.createPatient(request);

        // Assert - Verify for-loop statements executed (both iterations)
        assertNotNull(result);
        assertNotNull(result.getDiagnosis());
        assertEquals(2, result.getDiagnosis().size()); // Both diagnoses added
        verify(diagnosisRepository, times(1)).findById(diagnosis1.getDiagnosisId());
        verify(diagnosisRepository, times(1)).findById(diagnosis2.getDiagnosisId());
    }
}

