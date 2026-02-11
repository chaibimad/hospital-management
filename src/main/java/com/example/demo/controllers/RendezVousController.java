package com.example.demo.controllers;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.models.Patient;
import com.example.demo.models.RendezVous;
import com.example.demo.repositories.MedecinRepository;
import com.example.demo.repositories.PatientRepository;
import com.example.demo.repositories.RendezVousRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class RendezVousController {

    @Autowired private RendezVousRepository rdvRepository;
    @Autowired private PatientRepository patientRepository;
    @Autowired private MedecinRepository medecinRepository;

    @PostMapping("/api/patient/rdv/save")
    public String enregistrerRendezVous(@RequestParam String dateHeure,
                                        @RequestParam String motif,
                                        @RequestParam(required = false) Integer medecinId,
                                        HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login.html";
        Patient p = patientRepository.findById(userId).orElse(null);
        if (p == null) return "redirect:/login.html";
        RendezVous rdv = new RendezVous();
        rdv.setDateHeure(LocalDateTime.parse(dateHeure));
        rdv.setMotif(motif);
        rdv.setStatut("EN_ATTENTE");
        rdv.setPatient(p);
        if (medecinId != null) {
            rdv.setMedecin(medecinRepository.findById(medecinId).orElse(null));
        }
        rdvRepository.save(rdv);
        return "redirect:/patient/space?success=true";
    }
}