package com.example.demo.models;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Secretaire extends Utilisateur {
    private String codeEmploye; // Optional: internal employee code
}
