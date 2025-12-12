package com.testing_exam_webapp.service;

import com.testing_exam_webapp.dto.AppointmentRequest;
import com.testing_exam_webapp.model.mysql.Appointment;
import com.testing_exam_webapp.model.mysql.Doctor;
import com.testing_exam_webapp.model.mysql.Nurse;
import com.testing_exam_webapp.model.mysql.Patient;
import com.testing_exam_webapp.model.types.AppointmentStatusType;
import com.testing_exam_webapp.repository.AppointmentRepository;
import com.testing_exam_webapp.repository.DoctorRepository;
import com.testing_exam_webapp.repository.NurseRepository;
import com.testing_exam_webapp.repository.PatientRepository;
import com.testing_exam_webapp.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
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
 * Comprehensive test suite for AppointmentService.
 * Demonstrates boundary value analysis for dates and equivalence partitioning for appointment status.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AppointmentService Tests")
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private NurseRepository nurseRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    private Appointment testAppointment;
    private Patient testPatient;
    private Doctor testDoctor;
    private Nurse testNurse;

    @BeforeEach
    void setUp() {
        testAppointment = new Appointment();
        testAppointment.setAppointmentId(UUID.randomUUID());
        testAppointment.setAppointmentDate(LocalDate.now());
        testAppointment.setReason("Checkup");
        testAppointment.setStatus(AppointmentStatusType.SCHEDULED);
        
        testPatient = TestDataBuilder.createPatient();
        testDoctor = TestDataBuilder.createDoctor();
        testNurse = new Nurse();
        testNurse.setNurseId(UUID.randomUUID());
    }

    @Test
    @DisplayName("getAppointments - Should return empty list")
    void getAppointments_EmptyList_ReturnsEmptyList() {
        when(appointmentRepository.findAll()).thenReturn(Collections.emptyList());

        List<Appointment> result = appointmentService.getAppointments();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getAppointmentById - Should return appointment for valid ID")
    void getAppointmentById_ValidId_ReturnsAppointment() {
        UUID appointmentId = testAppointment.getAppointmentId();
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(testAppointment));

        Appointment result = appointmentService.getAppointmentById(appointmentId);

        assertNotNull(result);
        assertEquals(appointmentId, result.getAppointmentId());
    }

    @Test
    @DisplayName("createAppointment - Should create appointment with valid request")
    void createAppointment_ValidRequest_CreatesAppointment() {
        AppointmentRequest request = new AppointmentRequest();
        request.setAppointmentDate(LocalDate.now().plusDays(7));
        request.setReason("Annual checkup");
        request.setStatus(AppointmentStatusType.SCHEDULED);

        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> {
            Appointment a = invocation.getArgument(0);
            a.setAppointmentId(UUID.randomUUID());
            return a;
        });

        Appointment result = appointmentService.createAppointment(request);

        assertNotNull(result);
        assertNotNull(result.getAppointmentId());
    }

    @Test
    @DisplayName("createAppointment - Should create appointment with patient, doctor, and nurse")
    void createAppointment_WithAllRelations_CreatesAppointment() {
        AppointmentRequest request = new AppointmentRequest();
        request.setAppointmentDate(LocalDate.now().plusDays(7));
        request.setReason("Surgery consultation");
        request.setStatus(AppointmentStatusType.SCHEDULED);
        request.setPatientId(testPatient.getPatientId());
        request.setDoctorId(testDoctor.getDoctorId());
        request.setNurseId(testNurse.getNurseId());

        when(patientRepository.findById(testPatient.getPatientId())).thenReturn(Optional.of(testPatient));
        when(doctorRepository.findById(testDoctor.getDoctorId())).thenReturn(Optional.of(testDoctor));
        when(nurseRepository.findById(testNurse.getNurseId())).thenReturn(Optional.of(testNurse));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> {
            Appointment a = invocation.getArgument(0);
            a.setAppointmentId(UUID.randomUUID());
            return a;
        });

        Appointment result = appointmentService.createAppointment(request);

        assertNotNull(result);
        verify(patientRepository, times(1)).findById(testPatient.getPatientId());
        verify(doctorRepository, times(1)).findById(testDoctor.getDoctorId());
        verify(nurseRepository, times(1)).findById(testNurse.getNurseId());
    }

    static java.util.stream.Stream<Arguments> appointmentDateBoundaryValues() {
        return java.util.stream.Stream.of(
            Arguments.of(LocalDate.now(), "Today"),
            Arguments.of(LocalDate.now().plusDays(1), "Tomorrow"),
            Arguments.of(LocalDate.now().plusYears(1), "Far future (1 year)")
        );
    }

    @ParameterizedTest
    @MethodSource("appointmentDateBoundaryValues")
    @DisplayName("createAppointment - Boundary Analysis: Date boundary values")
    void createAppointment_DateBoundaryValues_CreatesAppointment(LocalDate appointmentDate, String description) {
        AppointmentRequest request = new AppointmentRequest();
        request.setAppointmentDate(appointmentDate);
        request.setReason("Test appointment");
        request.setStatus(AppointmentStatusType.SCHEDULED);

        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> {
            Appointment a = invocation.getArgument(0);
            a.setAppointmentId(UUID.randomUUID());
            return a;
        });

        Appointment result = appointmentService.createAppointment(request);
        assertNotNull(result);
        assertEquals(appointmentDate, result.getAppointmentDate());
    }

    @ParameterizedTest
    @EnumSource(AppointmentStatusType.class)
    @DisplayName("createAppointment - Equivalence Partitioning: Test all status types")
    void createAppointment_AllStatusTypes_CreatesAppointment(AppointmentStatusType status) {
        AppointmentRequest request = new AppointmentRequest();
        request.setAppointmentDate(LocalDate.now().plusDays(1));
        request.setReason("Test");
        request.setStatus(status);

        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> {
            Appointment a = invocation.getArgument(0);
            a.setAppointmentId(UUID.randomUUID());
            return a;
        });

        Appointment result = appointmentService.createAppointment(request);
        assertEquals(status, result.getStatus());
    }

    @Test
    @DisplayName("getAppointmentsByDate - Boundary Analysis: Today's date")
    void getAppointmentsByDate_Today_ReturnsAppointments() {
        LocalDate today = LocalDate.now();
        List<Appointment> appointments = Arrays.asList(testAppointment);
        when(appointmentRepository.findByAppointmentDate(today)).thenReturn(appointments);

        List<Appointment> result = appointmentService.getAppointmentsByDate(today);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("getAppointmentsByDateRange - Should return appointments in date range")
    void getAppointmentsByDateRange_ValidRange_ReturnsAppointments() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(30);
        List<Appointment> appointments = Arrays.asList(testAppointment);
        when(appointmentRepository.findByAppointmentDateBetween(startDate, endDate)).thenReturn(appointments);

        List<Appointment> result = appointmentService.getAppointmentsByDateRange(startDate, endDate);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("getAppointmentsByDateRange - Boundary Analysis: Same start and end date")
    void getAppointmentsByDateRange_SameDates_ReturnsAppointments() {
        LocalDate date = LocalDate.now();
        List<Appointment> appointments = Arrays.asList(testAppointment);
        when(appointmentRepository.findByAppointmentDateBetween(date, date)).thenReturn(appointments);

        List<Appointment> result = appointmentService.getAppointmentsByDateRange(date, date);

        assertNotNull(result);
    }

    @ParameterizedTest
    @EnumSource(AppointmentStatusType.class)
    @DisplayName("getAppointmentsByStatus - Equivalence Partitioning: All status types")
    void getAppointmentsByStatus_AllStatusTypes_ReturnsAppointments(AppointmentStatusType status) {
        List<Appointment> appointments = Arrays.asList(testAppointment);
        when(appointmentRepository.findByStatus(status)).thenReturn(appointments);

        List<Appointment> result = appointmentService.getAppointmentsByStatus(status);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("getAppointmentsByPatientId - Should return appointments for valid patient ID")
    void getAppointmentsByPatientId_ValidPatientId_ReturnsAppointments() {
        UUID patientId = testPatient.getPatientId();
        List<Appointment> appointments = Arrays.asList(testAppointment);
        when(appointmentRepository.findByPatientId(patientId)).thenReturn(appointments);

        List<Appointment> result = appointmentService.getAppointmentsByPatientId(patientId);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("updateAppointment - Should update appointment with valid request")
    void updateAppointment_ValidRequest_UpdatesAppointment() {
        UUID appointmentId = testAppointment.getAppointmentId();
        AppointmentRequest request = new AppointmentRequest();
        request.setAppointmentDate(LocalDate.now().plusDays(14));
        request.setReason("Updated reason");
        request.setStatus(AppointmentStatusType.COMPLETED);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(testAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);

        Appointment result = appointmentService.updateAppointment(appointmentId, request);

        assertNotNull(result);
        verify(appointmentRepository, times(1)).save(testAppointment);
    }

    @Test
    @DisplayName("deleteAppointment - Should delete appointment when valid ID provided")
    void deleteAppointment_ValidId_DeletesAppointment() {
        UUID appointmentId = testAppointment.getAppointmentId();
        when(appointmentRepository.existsById(appointmentId)).thenReturn(true);
        doNothing().when(appointmentRepository).deleteById(appointmentId);

        appointmentService.deleteAppointment(appointmentId);

        verify(appointmentRepository, times(1)).deleteById(appointmentId);
    }
}

