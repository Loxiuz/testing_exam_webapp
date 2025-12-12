package com.testing_exam_webapp.service;

import com.testing_exam_webapp.dto.HospitalRequest;
import com.testing_exam_webapp.exception.EntityNotFoundException;
import com.testing_exam_webapp.model.mysql.Hospital;
import com.testing_exam_webapp.model.mysql.Ward;
import com.testing_exam_webapp.repository.HospitalRepository;
import com.testing_exam_webapp.repository.WardRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class HospitalService {
    private final HospitalRepository hospitalRepository;
    private final WardRepository wardRepository;

    public HospitalService(HospitalRepository hospitalRepository, WardRepository wardRepository) {
        this.hospitalRepository = hospitalRepository;
        this.wardRepository = wardRepository;
    }

    public List<Hospital> getHospitals() {
        return hospitalRepository.findAll();
    }

    public Hospital getHospitalById(UUID id) {
        UUID hospitalId = Objects.requireNonNull(id, "Hospital ID cannot be null");
        return hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new EntityNotFoundException("Hospital not found"));
    }

    public Hospital createHospital(HospitalRequest request) {
        Hospital hospital = new Hospital();
        hospital.setHospitalId(UUID.randomUUID());
        hospital.setHospitalName(request.getHospitalName());
        hospital.setAddress(request.getAddress());
        hospital.setCity(request.getCity());

        if (request.getWardIds() != null && !request.getWardIds().isEmpty()) {
            Set<Ward> wards = new HashSet<>();
            for (UUID wardId : request.getWardIds()) {
                UUID wardUuid = Objects.requireNonNull(wardId, "Ward ID cannot be null");
                Ward ward = wardRepository.findById(wardUuid)
                        .orElseThrow(() -> new EntityNotFoundException("Ward not found: " + wardId));
                wards.add(ward);
            }
            hospital.setWards(wards);
        }

        return hospitalRepository.save(hospital);
    }

    public Hospital updateHospital(UUID id, HospitalRequest request) {
        UUID hospitalId = Objects.requireNonNull(id, "Hospital ID cannot be null");
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new EntityNotFoundException("Hospital not found"));

        hospital.setHospitalName(request.getHospitalName());
        hospital.setAddress(request.getAddress());
        hospital.setCity(request.getCity());

        if (request.getWardIds() != null && !request.getWardIds().isEmpty()) {
            Set<Ward> wards = new HashSet<>();
            for (UUID wardId : request.getWardIds()) {
                UUID wardUuid = Objects.requireNonNull(wardId, "Ward ID cannot be null");
                Ward ward = wardRepository.findById(wardUuid)
                        .orElseThrow(() -> new EntityNotFoundException("Ward not found: " + wardId));
                wards.add(ward);
            }
            hospital.setWards(wards);
        }

        return hospitalRepository.save(hospital);
    }

    public void deleteHospital(UUID id) {
        UUID hospitalId = Objects.requireNonNull(id, "Hospital ID cannot be null");
        if (!hospitalRepository.existsById(hospitalId)) {
            throw new EntityNotFoundException("Hospital not found");
        }
        hospitalRepository.deleteById(hospitalId);
    }

    // Query methods
    public List<Hospital> getHospitalsByCity(String city) {
        Objects.requireNonNull(city, "City cannot be null");
        return hospitalRepository.findByCity(city);
    }
}

