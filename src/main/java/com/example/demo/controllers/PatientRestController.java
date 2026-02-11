package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.models.Patient;
import com.example.demo.models.RendezVous;
import com.example.demo.repositories.PatientRepository;
import com.example.demo.repositories.RendezVousRepository;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/patient")
public class PatientRestController {

    @Autowired private PatientRepository patientRepository;
    @Autowired private RendezVousRepository rdvRepository;

    @PutMapping("/profil")
    public Patient updateProfil(@RequestBody Patient updates, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) throw new RuntimeException("Non authentifié");
        Patient p = patientRepository.findById(userId).orElseThrow();
        if (updates.getTelephone() != null) p.setTelephone(updates.getTelephone());
        if (updates.getAdresse() != null) p.setAdresse(updates.getAdresse());
        if (updates.getEmail() != null) p.setEmail(updates.getEmail());
        return patientRepository.save(p);
    }

    @PutMapping("/rdv/{id}/annuler")
    public RendezVous annulerMonRdv(@PathVariable Integer id, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) throw new RuntimeException("Non authentifié");
        RendezVous rdv = rdvRepository.findById(id).orElseThrow();
        if (rdv.getPatient() == null || !rdv.getPatient().getId().equals(userId))
            throw new RuntimeException("Ce rendez-vous ne vous appartient pas");
        rdv.setStatut("ANNULE");
        rdv.setMotifAnnulation("Annulé par le patient");
        return rdvRepository.save(rdv);
    }
}
