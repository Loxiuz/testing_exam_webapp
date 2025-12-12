package com.testing_exam_webapp.service;

import com.testing_exam_webapp.dto.WardRequest;
import com.testing_exam_webapp.exception.EntityNotFoundException;
import com.testing_exam_webapp.model.mysql.Ward;
import com.testing_exam_webapp.model.types.WardType;
import com.testing_exam_webapp.repository.WardRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class WardService {
    private final WardRepository wardRepository;

    public WardService(WardRepository wardRepository) {
        this.wardRepository = wardRepository;
    }

    public List<Ward> getWards() {
        return wardRepository.findAll();
    }

    public Ward getWardById(UUID id) {
        UUID wardId = Objects.requireNonNull(id, "Ward ID cannot be null");
        return wardRepository.findById(wardId)
                .orElseThrow(() -> new EntityNotFoundException("Ward not found"));
    }

    public Ward createWard(WardRequest request) {
        Ward ward = new Ward();
        ward.setWardId(UUID.randomUUID());
        ward.setType(request.getType());
        ward.setMaxCapacity(request.getMaxCapacity());

        return wardRepository.save(ward);
    }

    public Ward updateWard(UUID id, WardRequest request) {
        UUID wardId = Objects.requireNonNull(id, "Ward ID cannot be null");
        Ward ward = wardRepository.findById(wardId)
                .orElseThrow(() -> new EntityNotFoundException("Ward not found"));

        ward.setType(request.getType());
        ward.setMaxCapacity(request.getMaxCapacity());

        return wardRepository.save(ward);
    }

    public void deleteWard(UUID id) {
        UUID wardId = Objects.requireNonNull(id, "Ward ID cannot be null");
        if (!wardRepository.existsById(wardId)) {
            throw new EntityNotFoundException("Ward not found");
        }
        wardRepository.deleteById(wardId);
    }

    // Query methods
    public List<Ward> getWardsByType(WardType type) {
        return wardRepository.findByType(type);
    }

    public List<Ward> getWardsByHospitalId(UUID hospitalId) {
        Objects.requireNonNull(hospitalId, "Hospital ID cannot be null");
        return wardRepository.findByHospitalId(hospitalId);
    }
}

