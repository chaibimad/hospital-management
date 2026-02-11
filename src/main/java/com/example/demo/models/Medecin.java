package com.example.demo.models;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Medecin extends Utilisateur {
    private String specialite;
    private String numeroOrdre; // Ajouté pour correspondre au formulaire du dashboard
    
    @OneToMany(mappedBy = "medecin") 
    private List<Consultation> consultations;
}