package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.DossierMedical;

@Repository
public interface DossierMedicalRepository extends JpaRepository<DossierMedical, Integer> {
    DossierMedical findByPatient_Id(Integer patientId);
}