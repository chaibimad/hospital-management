package com.example.demo.repositories;

import com.example.demo.models.Secretaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SecretaireRepository extends JpaRepository<Secretaire, Integer> {
    Optional<Secretaire> findByEmail(String email);
}
