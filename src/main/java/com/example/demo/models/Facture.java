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
public class Facture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Double montant;
    private LocalDateTime dateEmission;
    private String statut; // EN_ATTENTE | PAYEE
    private String modePaiement; // ESPECES | CARTE | CHEQUE | VIREMENT
    private LocalDateTime datePaiement;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "consultation_id")
    private Consultation consultation;
}