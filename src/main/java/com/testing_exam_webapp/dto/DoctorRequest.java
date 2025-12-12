package com.testing_exam_webapp.dto;

import com.testing_exam_webapp.model.types.DoctorSpecialityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DoctorRequest {
    @NotBlank(message = "Doctor name is required")
    private String doctorName;
    
    @NotNull(message = "Speciality is required")
    private DoctorSpecialityType speciality;
    
    private UUID wardId;
    
    private UUID hospitalId;
}

