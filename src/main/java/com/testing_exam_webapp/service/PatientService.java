package com.testing_exam_webapp.service;

import com.testing_exam_webapp.dto.PatientRequest;
import com.testing_exam_webapp.exception.EntityNotFoundException;
import com.testing_exam_webapp.exception.ValidationException;
import com.testing_exam_webapp.model.mysql.Diagnosis;
import com.testing_exam_webapp.model.mysql.Hospital;
import com.testing_exam_webapp.model.mysql.Patient;
import com.testing_exam_webapp.model.mysql.Ward;
import com.testing_exam_webapp.repository.DiagnosisRepository;
import com.testing_exam_webapp.repository.HospitalRepository;
import com.testing_exam_webapp.repository.PatientRepository;
import com.testing_exam_webapp.repository.WardRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final WardRepository wardRepository;
    private final HospitalRepository hospitalRepository;
    private final DiagnosisRepository diagnosisRepository;

    public PatientService(PatientRepository patientRepository,
                          WardRepository wardRepository,
                          HospitalRepository hospitalRepository, DiagnosisRepository diagnosisRepository) {
        this.patientRepository = patientRepository;
        this.wardRepository = wardRepository;
        this.hospitalRepository = hospitalRepository;
        this.diagnosisRepository = diagnosisRepository;
    }

    public List<Patient> getPatients() {
        return patientRepository.findAll();
    }

    public Patient getPatientById(UUID id) {
        UUID patientId = Objects.requireNonNull(id, "Patient ID cannot be null");
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found"));
    }

    public Patient createPatient(PatientRequest request) {
        Patient patient = new Patient();
        patient.setPatientId(UUID.randomUUID());
        patient.setPatientName(request.getPatientName());
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setGender(request.getGender());

        if (request.getDiagnosisIds() != null) {
            Set<Diagnosis> diagnoses = new HashSet<>();
            for (UUID diagnosisId : request.getDiagnosisIds()) {
                UUID diagnosisUuid = Objects.requireNonNull(diagnosisId, "Diagnosis ID cannot be null");
                Diagnosis diagnosis = diagnosisRepository.findById(diagnosisUuid)
                        .orElseThrow(() -> new EntityNotFoundException("Diagnosis not found: " + diagnosisId));
                diagnoses.add(diagnosis);
            }
            patient.setDiagnosis(diagnoses);
        }

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
        
        patient.setWard(ward);
        patient.setHospital(hospital);

        return patientRepository.save(patient);
    }

    public Patient updatePatient(UUID id, PatientRequest request) {
        UUID patientId = Objects.requireNonNull(id, "Patient ID cannot be null");
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found"));

        patient.setPatientName(request.getPatientName());
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setGender(request.getGender());

        if (request.getDiagnosisIds() != null) {
            Set<Diagnosis> patients = new HashSet<>();
            for (UUID diagnosisId : request.getDiagnosisIds()) {
                UUID diagnosisUuid = Objects.requireNonNull(diagnosisId, "Diagnosis ID cannot be null");
                Diagnosis diagnosis = diagnosisRepository.findById(diagnosisUuid)
                        .orElseThrow(() -> new EntityNotFoundException("Diagnosis not found: " + diagnosisId));
                patients.add(diagnosis);
            }
            patient.setDiagnosis(patients);
        }

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
        
        patient.setWard(ward);
        patient.setHospital(hospital);

        return patientRepository.save(patient);
    }

    public void deletePatient(UUID id) {
        UUID patientId = Objects.requireNonNull(id, "Patient ID cannot be null");
        if (!patientRepository.existsById(patientId)) {
            throw new EntityNotFoundException("Patient not found");
        }
        patientRepository.deleteById(patientId);
    }

    // Query methods
    public List<Patient> getPatientsByWardId(UUID wardId) {
        Objects.requireNonNull(wardId, "Ward ID cannot be null");
        return patientRepository.findByWardId(wardId);
    }

    public List<Patient> getPatientsByHospitalId(UUID hospitalId) {
        Objects.requireNonNull(hospitalId, "Hospital ID cannot be null");
        return patientRepository.findByHospitalId(hospitalId);
    }
}

