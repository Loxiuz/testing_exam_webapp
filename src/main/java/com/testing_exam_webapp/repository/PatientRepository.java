package com.testing_exam_webapp.repository;

import com.testing_exam_webapp.model.mysql.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {
    @Query("SELECT p FROM Patient p WHERE p.ward.wardId = :wardId")
    List<Patient> findByWardId(@Param("wardId") UUID wardId);
    
    @Query("SELECT p FROM Patient p WHERE p.hospital.hospitalId = :hospitalId")
    List<Patient> findByHospitalId(@Param("hospitalId") UUID hospitalId);
}

