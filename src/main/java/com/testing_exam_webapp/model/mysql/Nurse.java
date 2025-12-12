package com.testing_exam_webapp.model.mysql;

import com.testing_exam_webapp.model.entity_bases.NurseBase;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "nurses")
public class Nurse extends NurseBase {
    @Id
    private UUID nurseId;
    @ManyToOne
    private Ward ward;
    @ManyToOne
    private Hospital hospital;
}
