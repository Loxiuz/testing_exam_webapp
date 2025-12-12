package com.testing_exam_webapp.model.mysql;

import com.testing_exam_webapp.model.entity_bases.DoctorBase;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "doctors")
public class Doctor extends DoctorBase {
    @Id
    private UUID doctorId;
    @ManyToOne
    private Ward ward;
    @ManyToOne
    private Hospital hospital;
}
