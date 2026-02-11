package com.example.demo.models;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class RendezVous {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private LocalDateTime dateHeure;
    private String motif;
    private String statut; // EN_ATTENTE | CONFIRMÉ | ANNULE | TERMINE
    private String motifAnnulation;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient; // Nécessaire pour le mappedBy de Patient.java

    @ManyToOne
    @JoinColumn(name = "medecin_id")
    private Medecin medecin; // Nécessaire pour le mappedBy de Medecin.java
}