package com.testing_exam_webapp.repository;

import com.testing_exam_webapp.model.mysql.Ward;
import com.testing_exam_webapp.model.types.WardType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface WardRepository extends JpaRepository<Ward, UUID> {
    List<Ward> findByType(WardType type);
    
    @Query("SELECT w FROM Ward w JOIN w.hospitals h WHERE h.hospitalId = :hospitalId")
    List<Ward> findByHospitalId(@Param("hospitalId") UUID hospitalId);
}

