package com.example.demo.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.models.Medecin;
import com.example.demo.models.Patient;
import com.example.demo.models.Secretaire;
import com.example.demo.models.Utilisateur;
import com.example.demo.repositories.MedecinRepository;
import com.example.demo.repositories.PatientRepository;
import com.example.demo.repositories.SecretaireRepository;
import com.example.demo.repositories.UtilisateurRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    @Autowired
    private MedecinRepository medecinRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private SecretaireRepository secretaireRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @GetMapping("/")
    public String index() {
        return "redirect:/login.html";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session) {
        // Try Admin
        Optional<Utilisateur> admin = utilisateurRepository.findByEmail(username);
        if (admin.isPresent() && admin.get().getMotDePasse().equals(password) && "ADMIN".equals(admin.get().getRole())) {
            session.setAttribute("userId", admin.get().getId());
            session.setAttribute("userRole", "ADMIN");
            session.setAttribute("userNom", admin.get().getNom());
            return "redirect:/dashboard";
        }

        // Try Medecin
        Optional<Medecin> medecin = medecinRepository.findByEmail(username);
        if (medecin.isPresent() && medecin.get().getMotDePasse().equals(password)) {
            session.setAttribute("userId", medecin.get().getId());
            session.setAttribute("userRole", "MEDECIN");
            session.setAttribute("userNom", medecin.get().getNom());
            return "redirect:/medecin/dashboard";
        }

        // Try Patient
        Optional<Patient> patient = patientRepository.findByEmail(username);
        if (patient.isPresent() && patient.get().getMotDePasse().equals(password)) {
            session.setAttribute("userId", patient.get().getId());
            session.setAttribute("userRole", "PATIENT");
            session.setAttribute("userNom", patient.get().getNom());
            return "redirect:/patient/space";
        }

        // Try Secretaire
        Optional<Secretaire> secretaire = secretaireRepository.findByEmail(username);
        if (secretaire.isPresent() && secretaire.get().getMotDePasse().equals(password)) {
            session.setAttribute("userId", secretaire.get().getId());
            session.setAttribute("userRole", "SECRETAIRE");
            session.setAttribute("userNom", secretaire.get().getNom());
            return "redirect:/secretaire/patients";
        }

        return "redirect:/login.html?error=true";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login.html";
    }
}