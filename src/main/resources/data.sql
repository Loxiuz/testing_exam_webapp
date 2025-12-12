INSERT INTO hospitals (hospital_id, address, city, hospital_name) VALUES
                                                                      (0x11111111111111111111111111111111, '123 Main St', 'Springfield', 'Springfield General Hospital'),
                                                                      (0x22222222222222222222222222222222, '45 West Ave', 'Shelbyville', 'Shelbyville Medical Center');
INSERT INTO medications (medication_id, dosage, medication_name) VALUES
                                                                     (0xAAAA1111111111111111111111111111, '10mg', 'Ibuprofen'),
                                                                     (0xAAAA2222222222222222222222222222, '500mg', 'Amoxicillin');
INSERT INTO wards (ward_id, max_capacity, type) VALUES
                                                    (0x33333333333333333333333333333333, 20, 'NEUROLOGY'), -- General ward
                                                    (0x44444444444444444444444444444444, 10, 'CARDIOLOGY'); -- ICU
INSERT INTO doctors (doctor_id, doctor_name, speciality, ward_ward_id) VALUES
                                                                           (0x55555555555555555555555555555555, 'Dr. Alice Carter', 1, 0x33333333333333333333333333333333),
                                                                           (0x66666666666666666666666666666666, 'Dr. Bob Hayes', 2, 0x44444444444444444444444444444444);
INSERT INTO diagnosis (diagnosis_id, diagnosis_date, description, doctor_doctor_id) VALUES
                                                                                        (0x77777777777777777777777777777777, '2023-10-10', 'Seasonal flu', 0x55555555555555555555555555555555),
                                                                                        (0x88888888888888888888888888888888, '2023-11-05', 'Bronchitis', 0x66666666666666666666666666666666);
INSERT INTO nurses (nurse_id, nurse_name, speciality, ward_ward_id) VALUES
                                                                        (0x99999999999999999999999999999999, 'Nurse Maria Lopez', 'EMERGENCY', 0x33333333333333333333333333333333),
                                                                        (0xAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA, 'Nurse John Smith', 'EMERGENCY', 0x44444444444444444444444444444444);
INSERT INTO patients (patient_id, patient_name, gender, date_of_birth, ward_ward_id, hospital_hospital_id) VALUES
                                                                                                               (0xBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB, 'Tom Walker', 'Male', '1990-04-12', 0x33333333333333333333333333333333, 0x11111111111111111111111111111111),
                                                                                                               (0xCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC, 'Sarah Brown', 'Female', '1985-08-21', 0x44444444444444444444444444444444, 0x22222222222222222222222222222222);
INSERT INTO appointments
(appointment_id, appointment_date, status, reason, doctor_doctor_id, nurse_nurse_id, patient_patient_id)
VALUES
    (0xDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD, '2023-12-01', 'COMPLETED', 'Routine checkup',
     0x55555555555555555555555555555555,
     0x99999999999999999999999999999999,
     0xBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB),

    (0xEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE, '2023-12-15', 'SCHEDULED', 'Follow-up visit',
     0x66666666666666666666666666666666,
     0xAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA,
     0xCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC);
INSERT INTO patients_diagnosis (diagnosis_diagnosis_id, patient_patient_id) VALUES
                                                                                (0x77777777777777777777777777777777, 0xBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB),
                                                                                (0x88888888888888888888888888888888, 0xCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC);
INSERT INTO prescriptions
(prescription_id, start_date, end_date, doctor_doctor_id, medication_medication_id, patient_patient_id)
VALUES
    (0xFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF, '2023-12-02', '2023-12-12',
     0x55555555555555555555555555555555,
     0xAAAA1111111111111111111111111111,
     0xBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB),

    (0x12121212121212121212121212121212, '2023-12-16', '2023-12-26',
     0x66666666666666666666666666666666,
     0xAAAA2222222222222222222222222222,
     0xCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC);
INSERT INTO surgeries
(surgery_id, surgery_date, description, doctor_doctor_id, patient_patient_id)
VALUES
    (0xABABABABABABABABABABABABABABABAB, '2024-01-10', 'Appendectomy',
     0x55555555555555555555555555555555,
     0xBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB),

    (0xCDCDCDCDCDCDCDCDCDCDCDCDCDCDCDCD, '2024-02-02', 'Gallbladder removal',
     0x66666666666666666666666666666666,
     0xCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC);
INSERT INTO hospitals_wards (hospital_hospital_id, wards_ward_id) VALUES
                                                                      (0x11111111111111111111111111111111, 0x33333333333333333333333333333333),
                                                                      (0x22222222222222222222222222222222, 0x44444444444444444444444444444444);