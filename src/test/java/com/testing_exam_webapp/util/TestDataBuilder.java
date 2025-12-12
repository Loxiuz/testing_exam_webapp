package com.testing_exam_webapp.util;

import com.testing_exam_webapp.model.mysql.*;
import com.testing_exam_webapp.model.types.DoctorSpecialityType;
import com.testing_exam_webapp.model.types.WardType;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.UUID;

/**
 * Utility class for building test data objects.
 * Provides factory methods for creating test entities with default or custom values.
 */
public class TestDataBuilder {

    /**
     * Creates a Hospital entity with default test values.
     * Can be customized using setters.
     */
    public static Hospital createHospital() {
        Hospital hospital = new Hospital();
        hospital.setHospitalId(UUID.randomUUID());
        hospital.setHospitalName("Test Hospital");
        hospital.setAddress("123 Test Street");
        hospital.setCity("Test City");
        hospital.setWards(new HashSet<>());
        return hospital;
    }

    /**
     * Creates a Hospital entity with specific values.
     */
    public static Hospital createHospital(String name, String address, String city) {
        Hospital hospital = createHospital();
        hospital.setHospitalName(name);
        hospital.setAddress(address);
        hospital.setCity(city);
        return hospital;
    }

    /**
     * Creates a Ward entity with default test values.
     */
    public static Ward createWard() {
        Ward ward = new Ward();
        ward.setWardId(UUID.randomUUID());
        ward.setType(WardType.CARDIOLOGY);
        ward.setMaxCapacity(30);
        ward.setHospitals(new HashSet<>());
        return ward;
    }

    /**
     * Creates a Ward entity with specific values.
     */
    public static Ward createWard(WardType type, int maxCapacity) {
        Ward ward = createWard();
        ward.setType(type);
        ward.setMaxCapacity(maxCapacity);
        return ward;
    }

    /**
     * Creates a Patient entity with default test values.
     */
    public static Patient createPatient() {
        Patient patient = new Patient();
        patient.setPatientId(UUID.randomUUID());
        patient.setPatientName("Test Patient");
        patient.setDateOfBirth(LocalDate.of(1990, 1, 1));
        patient.setGender("Male");
        return patient;
    }

    /**
     * Creates a Patient entity with specific values.
     */
    public static Patient createPatient(String name, LocalDate dateOfBirth, String gender) {
        Patient patient = createPatient();
        patient.setPatientName(name);
        patient.setDateOfBirth(dateOfBirth);
        patient.setGender(gender);
        return patient;
    }

    /**
     * Creates a Doctor entity with default test values.
     */
    public static Doctor createDoctor() {
        Doctor doctor = new Doctor();
        doctor.setDoctorId(UUID.randomUUID());
        doctor.setDoctorName("Test Doctor");
        doctor.setSpeciality(DoctorSpecialityType.CARDIOLOGY);
        return doctor;
    }

    /**
     * Creates a Doctor entity with specific values.
     */
    public static Doctor createDoctor(String name, DoctorSpecialityType speciality) {
        Doctor doctor = createDoctor();
        doctor.setDoctorName(name);
        doctor.setSpeciality(speciality);
        return doctor;
    }

    /**
     * Associates a ward with a hospital (Many-to-Many relationship).
     */
    public static void associateWardWithHospital(Ward ward, Hospital hospital) {
        if (ward.getHospitals() == null) {
            ward.setHospitals(new HashSet<>());
        }
        if (hospital.getWards() == null) {
            hospital.setWards(new HashSet<>());
        }
        ward.getHospitals().add(hospital);
        hospital.getWards().add(ward);
    }

    /**
     * Creates a Diagnosis entity with default test values.
     */
    public static Diagnosis createDiagnosis() {
        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setDiagnosisId(UUID.randomUUID());
        diagnosis.setDiagnosisDate(LocalDate.now());
        diagnosis.setDescription("Test Diagnosis");
        return diagnosis;
    }
}

