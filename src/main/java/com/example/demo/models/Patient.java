package com.example.demo.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Patient extends Utilisateur {
    private String telephone;
    private String adresse;
    private String numeroSecurite;
    private String groupeSanguin;
    private LocalDate dateInscription;
    private LocalDate dateNaissance;
    private String sexe;

    @OneToMany(mappedBy = "patient")
    private List<RendezVous> rendezVous = new ArrayList<>();
}