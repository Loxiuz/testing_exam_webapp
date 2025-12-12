package com.testing_exam_webapp.repository;

import com.testing_exam_webapp.model.mysql.Appointment;
import com.testing_exam_webapp.model.types.AppointmentStatusType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    @Query("SELECT a FROM Appointment a WHERE a.patient.patientId = :patientId")
    List<Appointment> findByPatientId(@Param("patientId") UUID patientId);
    
    @Query("SELECT a FROM Appointment a WHERE a.doctor.doctorId = :doctorId")
    List<Appointment> findByDoctorId(@Param("doctorId") UUID doctorId);
    
    @Query("SELECT a FROM Appointment a WHERE a.nurse.nurseId = :nurseId")
    List<Appointment> findByNurseId(@Param("nurseId") UUID nurseId);
    
    List<Appointment> findByStatus(AppointmentStatusType status);
    List<Appointment> findByAppointmentDate(LocalDate date);
    List<Appointment> findByAppointmentDateBetween(LocalDate startDate, LocalDate endDate);
}
