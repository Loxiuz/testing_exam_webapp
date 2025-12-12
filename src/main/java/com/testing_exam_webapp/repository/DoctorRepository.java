package com.testing_exam_webapp.repository;

import com.testing_exam_webapp.model.mysql.Doctor;
import com.testing_exam_webapp.model.types.DoctorSpecialityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface DoctorRepository extends JpaRepository<Doctor, UUID> {
    @Query("SELECT d FROM Doctor d WHERE d.ward.wardId = :wardId")
    List<Doctor> findByWardId(@Param("wardId") UUID wardId);
    
    List<Doctor> findBySpeciality(DoctorSpecialityType speciality);
    
    @Query("SELECT d FROM Doctor d WHERE d.hospital.hospitalId = :hospitalId")
    List<Doctor> findByHospitalId(@Param("hospitalId") UUID hospitalId);
}

