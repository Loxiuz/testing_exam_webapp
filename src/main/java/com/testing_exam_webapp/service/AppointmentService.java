package com.testing_exam_webapp.service;

import com.testing_exam_webapp.dto.AppointmentRequest;
import com.testing_exam_webapp.exception.EntityNotFoundException;
import com.testing_exam_webapp.model.mysql.Appointment;
import com.testing_exam_webapp.model.mysql.Doctor;
import com.testing_exam_webapp.model.mysql.Nurse;
import com.testing_exam_webapp.model.mysql.Patient;
import com.testing_exam_webapp.model.types.AppointmentStatusType;
import com.testing_exam_webapp.repository.AppointmentRepository;
import com.testing_exam_webapp.repository.DoctorRepository;
import com.testing_exam_webapp.repository.NurseRepository;
import com.testing_exam_webapp.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final NurseRepository nurseRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository,
                              NurseRepository nurseRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.nurseRepository = nurseRepository;
    }

    public List<Appointment> getAppointments() {
        return appointmentRepository.findAll();
    }

    public Appointment getAppointmentById(UUID id) {
        UUID appointmentId = Objects.requireNonNull(id, "Appointment ID cannot be null");
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));
    }

    public Appointment createAppointment(AppointmentRequest request) {
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(UUID.randomUUID());
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setReason(request.getReason());
        appointment.setStatus(request.getStatus());

        UUID patientId = request.getPatientId();
        if (patientId != null) {
            Patient patient = patientRepository.findById(patientId)
                    .orElseThrow(() -> new EntityNotFoundException("Patient not found"));
            appointment.setPatient(patient);
        }

        UUID doctorId = request.getDoctorId();
        if (doctorId != null) {
            Doctor doctor = doctorRepository.findById(doctorId)
                    .orElseThrow(() -> new EntityNotFoundException("Doctor not found"));
            appointment.setDoctor(doctor);
        }

        UUID nurseId = request.getNurseId();
        if (nurseId != null) {
            Nurse nurse = nurseRepository.findById(nurseId)
                    .orElseThrow(() -> new EntityNotFoundException("Nurse not found"));
            appointment.setNurse(nurse);
        }

        return appointmentRepository.save(appointment);
    }

    public Appointment updateAppointment(UUID id, AppointmentRequest request) {
        UUID appointmentId = Objects.requireNonNull(id, "Appointment ID cannot be null");
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));

        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setReason(request.getReason());
        appointment.setStatus(request.getStatus());

        UUID patientId = request.getPatientId();
        if (patientId != null) {
            Patient patient = patientRepository.findById(patientId)
                    .orElseThrow(() -> new EntityNotFoundException("Patient not found"));
            appointment.setPatient(patient);
        }

        UUID doctorId = request.getDoctorId();
        if (doctorId != null) {
            Doctor doctor = doctorRepository.findById(doctorId)
                    .orElseThrow(() -> new EntityNotFoundException("Doctor not found"));
            appointment.setDoctor(doctor);
        }

        UUID nurseId = request.getNurseId();
        if (nurseId != null) {
            Nurse nurse = nurseRepository.findById(nurseId)
                    .orElseThrow(() -> new EntityNotFoundException("Nurse not found"));
            appointment.setNurse(nurse);
        }

        return appointmentRepository.save(appointment);
    }

    public void deleteAppointment(UUID id) {
        UUID appointmentId = Objects.requireNonNull(id, "Appointment ID cannot be null");
        if (!appointmentRepository.existsById(appointmentId)) {
            throw new EntityNotFoundException("Appointment not found");
        }
        appointmentRepository.deleteById(appointmentId);
    }

    // Query methods
    public List<Appointment> getAppointmentsByPatientId(UUID patientId) {
        Objects.requireNonNull(patientId, "Patient ID cannot be null");
        return appointmentRepository.findByPatientId(patientId);
    }

    public List<Appointment> getAppointmentsByDoctorId(UUID doctorId) {
        Objects.requireNonNull(doctorId, "Doctor ID cannot be null");
        return appointmentRepository.findByDoctorId(doctorId);
    }

    public List<Appointment> getAppointmentsByNurseId(UUID nurseId) {
        Objects.requireNonNull(nurseId, "Nurse ID cannot be null");
        return appointmentRepository.findByNurseId(nurseId);
    }

    public List<Appointment> getAppointmentsByStatus(AppointmentStatusType status) {
        Objects.requireNonNull(status, "Status cannot be null");
        return appointmentRepository.findByStatus(status);
    }

    public List<Appointment> getAppointmentsByDate(LocalDate date) {
        Objects.requireNonNull(date, "Date cannot be null");
        return appointmentRepository.findByAppointmentDate(date);
    }

    public List<Appointment> getAppointmentsByDateRange(LocalDate startDate, LocalDate endDate) {
        Objects.requireNonNull(startDate, "Start date cannot be null");
        Objects.requireNonNull(endDate, "End date cannot be null");
        return appointmentRepository.findByAppointmentDateBetween(startDate, endDate);
    }
}
