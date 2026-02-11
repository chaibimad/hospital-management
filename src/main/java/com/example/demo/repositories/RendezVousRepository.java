package com.example.demo.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.RendezVous;

@Repository
public interface RendezVousRepository extends JpaRepository<RendezVous, Integer> {
    List<RendezVous> findByMedecin_IdOrderByDateHeureAsc(Integer medecinId);
    List<RendezVous> findByPatient_IdOrderByDateHeureDesc(Integer patientId);
    long countByDateHeureBetween(LocalDateTime start, LocalDateTime end);
    long countByMedecin_IdAndDateHeureBetween(Integer medecinId, LocalDateTime start, LocalDateTime end);
    long countByPatient_Id(Integer patientId);
}