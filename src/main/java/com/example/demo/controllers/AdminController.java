package com.example.demo.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.models.Journal;
import com.example.demo.models.Medecin;
import com.example.demo.models.Patient;
import com.example.demo.models.Utilisateur;
import com.example.demo.repositories.ConsultationRepository;
import com.example.demo.repositories.FactureRepository;
import com.example.demo.repositories.JournalRepository;
import com.example.demo.repositories.MedecinRepository;
import com.example.demo.repositories.PatientRepository;
import com.example.demo.repositories.RendezVousRepository;
import com.example.demo.repositories.SecretaireRepository;
import com.example.demo.repositories.UtilisateurRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {

    @Autowired private UtilisateurRepository utilisateurRepository;
    @Autowired private SecretaireRepository secretaireRepository;
    @Autowired private JournalRepository journalRepository;
    @Autowired private MedecinRepository medecinRepository;
    @Autowired private PatientRepository patientRepository;
    @Autowired private RendezVousRepository rdvRepository;
    @Autowired private ConsultationRepository consultationRepository;
    @Autowired private FactureRepository factureRepository;

    @GetMapping("/dashboard")
    public String showDashboard(Model model, HttpServletRequest request, HttpSession session) {
        try {
            model.addAttribute("countPatients", patientRepository.count());
            model.addAttribute("countMedecins", medecinRepository.count());
            LocalDateTime debutJour = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
            LocalDateTime finJour = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
            model.addAttribute("countRdv", rdvRepository.countByDateHeureBetween(debutJour, finJour));
            model.addAttribute("countConsultations", consultationRepository.count());
            Double revenus = factureRepository.sumMontantPaye();
            model.addAttribute("sumRevenus", revenus != null ? revenus : 0.0);
            // Aggregate users from specific repositories
            java.util.List<Utilisateur> listeUsers = new java.util.ArrayList<>();
            listeUsers.addAll(medecinRepository.findAll());
            listeUsers.addAll(patientRepository.findAll());
            listeUsers.addAll(secretaireRepository.findAll());
            model.addAttribute("listeUsers", listeUsers);
        } catch (Exception e) {
            model.addAttribute("countPatients", 0);
            model.addAttribute("countMedecins", 0);
            model.addAttribute("countRdv", 0);
            model.addAttribute("countConsultations", 0L);
            model.addAttribute("sumRevenus", 0.0);
            model.addAttribute("listeUsers", new java.util.ArrayList<>());
        }

        if ("1".equals(request.getHeader("X-App-Fragment"))) return "dashboard";
        model.addAttribute("userRole", String.valueOf(session.getAttribute("userRole")));
        model.addAttribute("sidebarTitle", "Hôpital Admin");
        model.addAttribute("pageTitle", "Hôpital Admin - Gestion");
        model.addAttribute("initialPath", request.getRequestURI());
        return "shell";
    }

    @PostMapping("/admin/saveUser")
    public String saveUser(@ModelAttribute Medecin medecin, @RequestParam String typeUser) {
        String actionType = (medecin.getId() == null) ? "CRÉATION" : "MODIFICATION";
        if ("MEDECIN".equals(typeUser)) {
            medecin.setRole("MEDECIN");
            medecinRepository.save(medecin);
        } else if ("PATIENT".equals(typeUser)) {
            Patient p = medecin.getId() != null ? patientRepository.findById(medecin.getId()).orElse(new Patient()) : new Patient();
            if (p.getDateInscription() == null) p.setDateInscription(LocalDate.now());
            p.setNom(medecin.getNom());
            p.setPrenom(medecin.getPrenom());
            p.setEmail(medecin.getEmail());
            p.setMotDePasse(medecin.getMotDePasse());
            p.setRole("PATIENT");
            patientRepository.save(p);
        } else if ("SECRETAIRE".equals(typeUser)) {
            com.example.demo.models.Secretaire s = medecin.getId() != null ? secretaireRepository.findById(medecin.getId()).orElse(new com.example.demo.models.Secretaire()) : new com.example.demo.models.Secretaire();
            s.setNom(medecin.getNom());
            s.setPrenom(medecin.getPrenom());
            s.setEmail(medecin.getEmail());
            s.setMotDePasse(medecin.getMotDePasse());
            s.setRole("SECRETAIRE");
            secretaireRepository.save(s);
        } else {
            // Fallback to generic Utilisateur for other roles if needed
            Utilisateur u = medecin.getId() != null ? utilisateurRepository.findById(medecin.getId()).orElse(new Utilisateur()) : new Utilisateur();
            if (medecin.getId() != null) u.setId(medecin.getId());
            u.setNom(medecin.getNom());
            u.setPrenom(medecin.getPrenom());
            u.setEmail(medecin.getEmail());
            u.setMotDePasse(medecin.getMotDePasse());
            u.setRole(typeUser);
            utilisateurRepository.save(u);
        }
        enregistrerAction(actionType, "Utilisateur : " + medecin.getEmail() + " (" + typeUser + ")");
        return "redirect:/dashboard?success=true";
    }

    @GetMapping("/admin/user/delete/{id}")
    public String deleteUser(@PathVariable Integer id) {
        // Try specific repositories first
        if (medecinRepository.existsById(id)) {
            medecinRepository.deleteById(id);
            enregistrerAction("SUPPRESSION", "Compte supprimé : MEDECIN id " + id);
        } else if (patientRepository.existsById(id)) {
            patientRepository.deleteById(id);
            enregistrerAction("SUPPRESSION", "Compte supprimé : PATIENT id " + id);
        } else if (secretaireRepository.existsById(id)) {
            secretaireRepository.deleteById(id);
            enregistrerAction("SUPPRESSION", "Compte supprimé : SECRETAIRE id " + id);
        } else {
            // Fallback to generic Utilisateur
            Utilisateur u = utilisateurRepository.findById(id).orElse(null);
            if (u != null) {
                enregistrerAction("SUPPRESSION", "Compte supprimé : " + u.getEmail());
                utilisateurRepository.deleteById(id);
            }
        }
        return "redirect:/dashboard?deleted=true";
    }

    @GetMapping("/admin/journal")
    public String voirJournal(Model model,
                             @RequestParam(required = false) String utilisateur,
                             @RequestParam(required = false) String action,
                             @RequestParam(required = false) String dateDebut,
                             @RequestParam(required = false) String dateFin,
                             HttpServletRequest request,
                             HttpSession session) {
        LocalDateTime start = null, end = null;
        if (dateDebut != null && !dateDebut.isBlank()) {
            try { start = LocalDate.parse(dateDebut).atStartOfDay(); } catch (Exception ignored) {}
        }
        if (dateFin != null && !dateFin.isBlank()) {
            try { end = LocalDate.parse(dateFin).atTime(LocalTime.MAX); } catch (Exception ignored) {}
        }
        List<Journal> logs = journalRepository.findFiltered(
            (utilisateur != null && !utilisateur.isBlank()) ? utilisateur : null,
            (action != null && !action.isBlank()) ? action : null, start, end);
        model.addAttribute("logs", logs);
        model.addAttribute("filterUser", utilisateur);
        model.addAttribute("filterAction", action);
        model.addAttribute("filterDateDebut", dateDebut);
        model.addAttribute("filterDateFin", dateFin);

        if ("1".equals(request.getHeader("X-App-Fragment"))) return "admin_journal";
        model.addAttribute("userRole", String.valueOf(session.getAttribute("userRole")));
        model.addAttribute("sidebarTitle", "Hôpital Admin");
        model.addAttribute("pageTitle", "Journal d'activité - Traçabilité");
        model.addAttribute("initialPath", request.getRequestURI());
        return "shell";
    }

    @GetMapping("/admin/rapports")
    public String rapports(Model model, HttpServletRequest request, HttpSession session) {
        model.addAttribute("countPatients", patientRepository.count());
        model.addAttribute("countMedecins", medecinRepository.count());
        model.addAttribute("countRdv", rdvRepository.count());
        model.addAttribute("countConsultations", consultationRepository.count());
        Double revenus = factureRepository.sumMontantPaye();
        model.addAttribute("sumRevenus", revenus != null ? revenus : 0.0);

        if ("1".equals(request.getHeader("X-App-Fragment"))) return "admin_rapports";
        model.addAttribute("userRole", String.valueOf(session.getAttribute("userRole")));
        model.addAttribute("sidebarTitle", "Hôpital Admin");
        model.addAttribute("pageTitle", "Rapports - Administration");
        model.addAttribute("initialPath", request.getRequestURI());
        return "shell";
    }

    @GetMapping("/admin/rapports/export")
    public void exportRapportsCsv(HttpServletResponse response) throws Exception {
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=rapport_clinique.csv");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        var w = response.getWriter();
        w.write("Indicateur;Valeur\n");
        w.write("Patients;" + patientRepository.count() + "\n");
        w.write("Médecins;" + medecinRepository.count() + "\n");
        w.write("Rendez-vous (total);" + rdvRepository.count() + "\n");
        w.write("Consultations;" + consultationRepository.count() + "\n");
        Double r = factureRepository.sumMontantPaye();
        w.write("Revenus (factures payées DH);" + (r != null ? r : 0) + "\n");
        w.write("\nJournal (dernières actions)\nDate;Auteur;Action;Détails\n");
        for (Journal j : journalRepository.findAllByOrderByDateHeureDesc()) {
            if (j.getDateHeure() != null) w.write(j.getDateHeure().format(dtf) + ";");
            w.write((j.getUtilisateurEmail() != null ? j.getUtilisateurEmail() : "") + ";");
            w.write((j.getAction() != null ? j.getAction() : "") + ";");
            w.write((j.getDetails() != null ? j.getDetails().replace(";", ",") : "") + "\n");
        }
        w.flush();
    }

    private void enregistrerAction(String action, String details) {
        Journal log = new Journal();
        log.setUtilisateurEmail("ADMIN");
        log.setAction(action);
        log.setDetails(details);
        log.setDateHeure(LocalDateTime.now());
        journalRepository.save(log);
    }
}