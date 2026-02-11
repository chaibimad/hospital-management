package com.example.demo.repositories;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.models.Medecin;
import java.util.Optional;
public interface MedecinRepository extends JpaRepository<Medecin, Integer> {
    Optional<Medecin> findByEmail(String email);
}