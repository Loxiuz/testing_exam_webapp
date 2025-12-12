package com.testing_exam_webapp.config;

import com.testing_exam_webapp.model.mysql.Hospital;
import com.testing_exam_webapp.model.mysql.User;
import com.testing_exam_webapp.model.mysql.Ward;
import com.testing_exam_webapp.model.types.Role;
import com.testing_exam_webapp.model.types.WardType;
import com.testing_exam_webapp.repository.HospitalRepository;
import com.testing_exam_webapp.repository.UserRepository;
import com.testing_exam_webapp.repository.WardRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final HospitalRepository hospitalRepository;
    private final WardRepository wardRepository;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder,
                          HospitalRepository hospitalRepository, WardRepository wardRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.hospitalRepository = hospitalRepository;
        this.wardRepository = wardRepository;
    }

    @Override
    public void run(String... args) {
        // Initialize admin user
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUserId(UUID.randomUUID());
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
        }

        // Initialize test user
        if (userRepository.findByUsername("testUser").isEmpty()) {
            User testUser = new User();
            testUser.setUserId(UUID.randomUUID());
            testUser.setUsername("testUser");
            testUser.setPassword(passwordEncoder.encode("password"));
            testUser.setRole(Role.ADMIN);
            userRepository.save(testUser);
        }

        // Initialize hospitals if they don't exist
        if (hospitalRepository.count() == 0) {
            // Create Rigshospitalet
            Hospital rigshospitalet = new Hospital();
            rigshospitalet.setHospitalId(UUID.randomUUID());
            rigshospitalet.setHospitalName("Rigshospitalet");
            rigshospitalet.setAddress("Blegdamsvej 9");
            rigshospitalet.setCity("København");
            rigshospitalet = hospitalRepository.save(rigshospitalet);

            // Create Aarhus Universitetshospital
            Hospital aarhusHospital = new Hospital();
            aarhusHospital.setHospitalId(UUID.randomUUID());
            aarhusHospital.setHospitalName("Aarhus Universitetshospital");
            aarhusHospital.setAddress("Palle Juul-Jensens Boulevard 99");
            aarhusHospital.setCity("Aarhus");
            aarhusHospital = hospitalRepository.save(aarhusHospital);

            // Initialize wards if they don't exist
            if (wardRepository.count() == 0) {
                // Cardiology Ward (associated with Rigshospitalet)
                Ward cardiologyWard = new Ward();
                cardiologyWard.setWardId(UUID.randomUUID());
                cardiologyWard.setType(WardType.CARDIOLOGY);
                cardiologyWard.setMaxCapacity(30);
                cardiologyWard = wardRepository.save(cardiologyWard);
                
                // Add ward to Rigshospitalet
                Set<Ward> rigshospitaletWards = rigshospitalet.getWards();
                if (rigshospitaletWards == null) {
                    rigshospitaletWards = new HashSet<>();
                }
                rigshospitaletWards.add(cardiologyWard);
                rigshospitalet.setWards(rigshospitaletWards);
                hospitalRepository.save(rigshospitalet);

                // Neurology Ward (associated with Rigshospitalet)
                Ward neurologyWard = new Ward();
                neurologyWard.setWardId(UUID.randomUUID());
                neurologyWard.setType(WardType.NEUROLOGY);
                neurologyWard.setMaxCapacity(25);
                neurologyWard = wardRepository.save(neurologyWard);
                
                // Add ward to Rigshospitalet
                rigshospitaletWards.add(neurologyWard);
                rigshospitalet.setWards(rigshospitaletWards);
                hospitalRepository.save(rigshospitalet);

                // General Medicine Ward (associated with Aarhus Universitetshospital)
                Ward generalMedicineWard = new Ward();
                generalMedicineWard.setWardId(UUID.randomUUID());
                generalMedicineWard.setType(WardType.GENERAL_MEDICINE);
                generalMedicineWard.setMaxCapacity(20);
                generalMedicineWard = wardRepository.save(generalMedicineWard);
                
                // Add ward to Aarhus Universitetshospital
                Set<Ward> aarhusWards = aarhusHospital.getWards();
                if (aarhusWards == null) {
                    aarhusWards = new HashSet<>();
                }
                aarhusWards.add(generalMedicineWard);
                aarhusHospital.setWards(aarhusWards);
                hospitalRepository.save(aarhusHospital);
            }
        }
    }
}

