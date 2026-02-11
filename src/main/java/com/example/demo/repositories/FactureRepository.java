package com.example.demo.repositories;

import java.util.List;
import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.models.Facture;

@Repository
public interface FactureRepository extends JpaRepository<Facture, Integer> {
    List<Facture> findByPatient_IdOrderByDateEmissionDesc(Integer patientId);

    List<Facture> findByStatutAndDatePaiementBetweenOrderByDatePaiementDesc(String statut, LocalDateTime start, LocalDateTime end);

    @Query("SELECT COALESCE(SUM(f.montant), 0) FROM Facture f WHERE f.statut = 'PAYEE'")
    Double sumMontantPaye();
}