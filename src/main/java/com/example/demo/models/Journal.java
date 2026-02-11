package com.example.demo.models;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Journal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private String utilisateurEmail; // Qui a fait l'action
    private String action;           // Nature de l'action (AJOUT, SUPPRESSION, etc.)
    private String details;          // Description précise
    private LocalDateTime dateHeure; // Quand
}