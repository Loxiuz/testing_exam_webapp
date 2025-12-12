package com.testing_exam_webapp.model.mysql;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.testing_exam_webapp.model.entity_bases.HospitalBase;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "hospitals")
public class Hospital extends HospitalBase {
    @Id
    private UUID hospitalId;
    
    @ManyToMany
    @JoinTable(
        name = "hospitals_wards",
        joinColumns = @JoinColumn(name = "hospital_id"),
        inverseJoinColumns = @JoinColumn(name = "ward_id")
    )
    @JsonIgnoreProperties("hospitals")
    private Set<Ward> wards;
}
