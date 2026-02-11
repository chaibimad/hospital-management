package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.Consultation;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, Integer> {
    // Pour consulter l'historique médical d'un patient
    List<Consultation> findByPatient_IdOrderByDateDesc(Integer patientId);
}