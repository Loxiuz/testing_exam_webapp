package com.testing_exam_webapp.service;

import com.testing_exam_webapp.dto.DoctorRequest;
import com.testing_exam_webapp.exception.EntityNotFoundException;
import com.testing_exam_webapp.exception.ValidationException;
import com.testing_exam_webapp.model.mysql.Doctor;
import com.testing_exam_webapp.model.mysql.Hospital;
import com.testing_exam_webapp.model.mysql.Ward;
import com.testing_exam_webapp.model.types.DoctorSpecialityType;
import com.testing_exam_webapp.repository.DoctorRepository;
import com.testing_exam_webapp.repository.HospitalRepository;
import com.testing_exam_webapp.repository.WardRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final WardRepository wardRepository;
    private final HospitalRepository hospitalRepository;

    public DoctorService(DoctorRepository doctorRepository, WardRepository wardRepository,
                         HospitalRepository hospitalRepository) {
        this.doctorRepository = doctorRepository;
        this.wardRepository = wardRepository;
        this.hospitalRepository = hospitalRepository;
    }

    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    public Doctor getDoctorById(UUID id) {
        UUID doctorId = Objects.requireNonNull(id, "Doctor ID cannot be null");
        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found"));
    }

    public Doctor createDoctor(DoctorRequest request) {
        Doctor doctor = new Doctor();
        doctor.setDoctorId(UUID.randomUUID());
        doctor.setDoctorName(request.getDoctorName());
        doctor.setSpeciality(request.getSpeciality());

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
        
        doctor.setWard(ward);
        doctor.setHospital(hospital);

        return doctorRepository.save(doctor);
    }

    public Doctor updateDoctor(UUID id, DoctorRequest request) {
        UUID doctorId = Objects.requireNonNull(id, "Doctor ID cannot be null");
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found"));

        doctor.setDoctorName(request.getDoctorName());
        doctor.setSpeciality(request.getSpeciality());

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
        
        doctor.setWard(ward);
        doctor.setHospital(hospital);

        return doctorRepository.save(doctor);
    }

    public void deleteDoctor(UUID id) {
        UUID doctorId = Objects.requireNonNull(id, "Doctor ID cannot be null");
        if (!doctorRepository.existsById(doctorId)) {
            throw new EntityNotFoundException("Doctor not found");
        }
        doctorRepository.deleteById(doctorId);
    }

    // Query methods
    public List<Doctor> getDoctorsByWardId(UUID wardId) {
        Objects.requireNonNull(wardId, "Ward ID cannot be null");
        return doctorRepository.findByWardId(wardId);
    }

    public List<Doctor> getDoctorsBySpeciality(DoctorSpecialityType speciality) {
        Objects.requireNonNull(speciality, "Speciality cannot be null");
        return doctorRepository.findBySpeciality(speciality);
    }

    public List<Doctor> getDoctorsByHospitalId(UUID hospitalId) {
        Objects.requireNonNull(hospitalId, "Hospital ID cannot be null");
        return doctorRepository.findByHospitalId(hospitalId);
    }
}

