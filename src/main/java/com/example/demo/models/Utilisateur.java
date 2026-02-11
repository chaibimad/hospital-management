package com.example.demo.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import lombok.Data;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data 
public class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private String email;
    private String motDePasse;
    private String role;
    private String nom;
    private String prenom;

    /** Pour l'édition dans le dashboard (médecin : spécialité / n° ordre). */
    public String getSpecialite() { return null; }
    public String getNumeroOrdre() { return null; }
}