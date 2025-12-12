package com.testing_exam_webapp.repository;

import com.testing_exam_webapp.model.mysql.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HospitalRepository extends JpaRepository<Hospital, UUID> {
    List<Hospital> findByCity(String city);
}

