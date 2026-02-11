package com.example.demo.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String medicaments;
    private String posologie;
    private Integer dureeTraitement;

    @ManyToOne
    @JoinColumn(name = "consultation_id")
    private Consultation consultation;
}