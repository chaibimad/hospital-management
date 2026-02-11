package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.Prescription;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Integer> {
    List<Prescription> findByConsultation_IdOrderByIdDesc(Integer consultationId);

    List<Prescription> findByConsultation_Patient_IdOrderByIdDesc(Integer patientId);
}