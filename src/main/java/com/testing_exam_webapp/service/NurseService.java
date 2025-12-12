package com.testing_exam_webapp.service;

import com.testing_exam_webapp.dto.NurseRequest;
import com.testing_exam_webapp.exception.EntityNotFoundException;
import com.testing_exam_webapp.exception.ValidationException;
import com.testing_exam_webapp.model.mysql.Hospital;
import com.testing_exam_webapp.model.mysql.Nurse;
import com.testing_exam_webapp.model.mysql.Ward;
import com.testing_exam_webapp.repository.HospitalRepository;
import com.testing_exam_webapp.repository.NurseRepository;
import com.testing_exam_webapp.repository.WardRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class NurseService {
    private final NurseRepository nurseRepository;
    private final WardRepository wardRepository;
    private final HospitalRepository hospitalRepository;

    public NurseService(NurseRepository nurseRepository, WardRepository wardRepository,
                        HospitalRepository hospitalRepository) {
        this.nurseRepository = nurseRepository;
        this.wardRepository = wardRepository;
        this.hospitalRepository = hospitalRepository;
    }

    public List<Nurse> getNurses() {
        return nurseRepository.findAll();
    }

    public Nurse getNurseById(UUID id) {
        UUID nurseId = Objects.requireNonNull(id, "Nurse ID cannot be null");
        return nurseRepository.findById(nurseId)
                .orElseThrow(() -> new EntityNotFoundException("Nurse not found"));
    }

    public Nurse createNurse(NurseRequest request) {
        Nurse nurse = new Nurse();
        nurse.setNurseId(UUID.randomUUID());
        nurse.setNurseName(request.getNurseName());
        nurse.setSpeciality(request.getSpeciality());

        UUID wardId = request.getWardId();
        UUID hospitalId = request.getHospitalId();
        
        Ward ward = null;
        Hospital hospital = null;
        
        if (wardId != null) {
            ward = wardRepository.findById(wardId)
                    .orElseThrow(() -> new EntityNotFoundException("Ward not found"));
        }
        
        if (hospitalId != null) {
            hospital = hospitalRepository.findById(hospitalId)
                    .orElseThrow(() -> new EntityNotFoundException("Hospital not found"));
        }
        
        // Validate that ward belongs to hospital if both are provided
        if (ward != null && hospital != null) {
            final UUID hospitalIdToCheck = hospital.getHospitalId();
            final String hospitalName = hospital.getHospitalName();
            boolean wardBelongsToHospital = ward.getHospitals() != null && 
                    ward.getHospitals().stream()
                            .anyMatch(h -> h.getHospitalId().equals(hospitalIdToCheck));
            
            if (!wardBelongsToHospital) {
                throw new ValidationException(
                    "The selected ward does not belong to the selected hospital. " +
                    "Please select a ward that exists in " + hospitalName + "."
                );
            }
        }
        
        nurse.setWard(ward);
        nurse.setHospital(hospital);

        return nurseRepository.save(nurse);
    }

    public Nurse updateNurse(UUID id, NurseRequest request) {
        UUID nurseId = Objects.requireNonNull(id, "Nurse ID cannot be null");
        Nurse nurse = nurseRepository.findById(nurseId)
                .orElseThrow(() -> new EntityNotFoundException("Nurse not found"));

        nurse.setNurseName(request.getNurseName());
        nurse.setSpeciality(request.getSpeciality());

        UUID wardId = request.getWardId();
        UUID hospitalId = request.getHospitalId();
        
        Ward ward = null;
        Hospital hospital = null;
        
        if (wardId != null) {
            ward = wardRepository.findById(wardId)
                    .orElseThrow(() -> new EntityNotFoundException("Ward not found"));
        }
        
        if (hospitalId != null) {
            hospital = hospitalRepository.findById(hospitalId)
                    .orElseThrow(() -> new EntityNotFoundException("Hospital not found"));
        }
        
        // Validate that ward belongs to hospital if both are provided
        if (ward != null && hospital != null) {
            final UUID hospitalIdToCheck = hospital.getHospitalId();
            final String hospitalName = hospital.getHospitalName();
            boolean wardBelongsToHospital = ward.getHospitals() != null && 
                    ward.getHospitals().stream()
                            .anyMatch(h -> h.getHospitalId().equals(hospitalIdToCheck));
            
            if (!wardBelongsToHospital) {
                throw new ValidationException(
                    "The selected ward does not belong to the selected hospital. " +
                    "Please select a ward that exists in " + hospitalName + "."
                );
            }
        }
        
        nurse.setWard(ward);
        nurse.setHospital(hospital);

        return nurseRepository.save(nurse);
    }

    public void deleteNurse(UUID id) {
        UUID nurseId = Objects.requireNonNull(id, "Nurse ID cannot be null");
        if (!nurseRepository.existsById(nurseId)) {
            throw new EntityNotFoundException("Nurse not found");
        }
        nurseRepository.deleteById(nurseId);
    }
}

