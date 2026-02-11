package com.example.demo.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.demo.models.Medecin;
import com.example.demo.models.Patient;
import com.example.demo.models.Secretaire;
import com.example.demo.models.Utilisateur;
import com.example.demo.repositories.MedecinRepository;
import com.example.demo.repositories.PatientRepository;
import com.example.demo.repositories.SecretaireRepository;
import com.example.demo.repositories.UtilisateurRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserAccountController {

    @Autowired
    private MedecinRepository medecinRepository;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private SecretaireRepository secretaireRepository;
    @Autowired
    private UtilisateurRepository utilisateurRepository; // Fallback

    @GetMapping("/user/password")
    public String passwordPage(Model model, HttpServletRequest request, HttpSession session) {
        if ("1".equals(request.getHeader("X-App-Fragment"))) return "change_password";

        model.addAttribute("userRole", String.valueOf(session.getAttribute("userRole")));
        model.addAttribute("pageTitle", "Changer mot de passe");
        model.addAttribute("initialPath", request.getRequestURI());
        return "shell";
    }

    @PutMapping("/api/user/password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> payload, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        String userRole = (String) session.getAttribute("userRole");
        if (userId == null || userRole == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Non authentifié"));
        }

        String oldPassword = payload.getOrDefault("oldPassword", "");
        String newPassword = payload.getOrDefault("newPassword", "");

        if (newPassword == null || newPassword.isBlank() || newPassword.length() < 4) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Mot de passe invalide (min 4 caractères)."));
        }

        Utilisateur u = null;
        switch (userRole) {
            case "MEDECIN":
                u = medecinRepository.findById(userId).orElse(null);
                break;
            case "PATIENT":
                u = patientRepository.findById(userId).orElse(null);
                break;
            case "SECRETAIRE":
                u = secretaireRepository.findById(userId).orElse(null);
                break;
            default:
                u = utilisateurRepository.findById(userId).orElse(null);
                break;
        }

        if (u == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Utilisateur introuvable"));
        }

        if (u.getMotDePasse() == null || !u.getMotDePasse().equals(oldPassword)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Ancien mot de passe incorrect"));
        }

        u.setMotDePasse(newPassword);
        // Save to appropriate repository
        switch (userRole) {
            case "MEDECIN":
                medecinRepository.save((Medecin) u);
                break;
            case "PATIENT":
                patientRepository.save((Patient) u);
                break;
            case "SECRETAIRE":
                secretaireRepository.save((Secretaire) u);
                break;
            default:
                utilisateurRepository.save(u);
                break;
        }

        return ResponseEntity.ok(Map.of("message", "Mot de passe modifié avec succès"));
    }
}
