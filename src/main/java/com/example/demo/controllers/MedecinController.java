package com.example.demo.controllers;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.models.Consultation;
import com.example.demo.models.DossierMedical;
import com.example.demo.models.Medecin;
import com.example.demo.models.Patient;
import com.example.demo.models.Prescription;
import com.example.demo.models.RendezVous;
import com.example.demo.repositories.ConsultationRepository;
import com.example.demo.repositories.DossierMedicalRepository;
import com.example.demo.repositories.MedecinRepository;
import com.example.demo.repositories.PatientRepository;
import com.example.demo.repositories.PrescriptionRepository;
import com.example.demo.repositories.RendezVousRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/medecin")
public class MedecinController {

    @Autowired private RendezVousRepository rdvRepository;
    @Autowired private PatientRepository patientRepository;
    @Autowired private MedecinRepository medecinRepository;
    @Autowired private ConsultationRepository consultationRepository;
    @Autowired private DossierMedicalRepository dossierRepository;
    @Autowired private PrescriptionRepository prescriptionRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session, HttpServletRequest request) {
        Integer medecinId = (Integer) session.getAttribute("userId");
        if (medecinId == null) return "redirect:/login.html";

        LocalDateTime start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        long countToday = rdvRepository.countByMedecin_IdAndDateHeureBetween(medecinId, start, end);
        List<RendezVous> all = rdvRepository.findByMedecin_IdOrderByDateHeureAsc(medecinId);
        List<RendezVous> today = all.stream()
                .filter(r -> r.getDateHeure() != null && !r.getDateHeure().isBefore(start) && !r.getDateHeure().isAfter(end))
                .collect(Collectors.toList());

        model.addAttribute("countToday", countToday);
        model.addAttribute("todayRdv", today);

        if ("1".equals(request.getHeader("X-App-Fragment"))) return "medecin_dashboard";
        model.addAttribute("userRole", String.valueOf(session.getAttribute("userRole")));
        model.addAttribute("sidebarTitle", "Médecin");
        model.addAttribute("pageTitle", "Tableau de bord Médecin");
        model.addAttribute("initialPath", request.getRequestURI());
        return "shell";
    }

    @GetMapping("/agenda")
    public String voirAgenda(Model model, HttpSession session, HttpServletRequest request) {
        Integer medecinId = (Integer) session.getAttribute("userId");
        if (medecinId == null) return "redirect:/login.html";
        model.addAttribute("rendezvous", rdvRepository.findByMedecin_IdOrderByDateHeureAsc(medecinId));
        if ("1".equals(request.getHeader("X-App-Fragment"))) return "medecin_space";
        model.addAttribute("userRole", String.valueOf(session.getAttribute("userRole")));
        model.addAttribute("sidebarTitle", "Médecin");
        model.addAttribute("pageTitle", "Agenda Médecin");
        model.addAttribute("initialPath", request.getRequestURI());
        return "shell";
    }

    @GetMapping("/demarrer/{rdvId}")
    public String demarrerConsultation(@PathVariable Integer rdvId, Model model, HttpSession session, HttpServletRequest request) {
        if (session.getAttribute("userId") == null) return "redirect:/login.html";
        RendezVous rdv = rdvRepository.findById(rdvId).orElse(null);
        if (rdv == null || rdv.getPatient() == null) return "redirect:/medecin/agenda";
        Patient p = rdv.getPatient();
        DossierMedical dossier = dossierRepository.findByPatient_Id(p.getId());
        if (dossier == null) {
            dossier = new DossierMedical();
            dossier.setPatient(p);
            dossier.setAntecedents("");
            dossier.setAllergies("");
            dossier.setTraitements("");
            dossierRepository.save(dossier);
        }
        model.addAttribute("rdv", rdv);
        model.addAttribute("patient", p);
        model.addAttribute("dossier", dossier);

        // Historique des consultations pour affichage dans la page examen
        model.addAttribute("consultations", consultationRepository.findByPatient_IdOrderByDateDesc(p.getId()));

        if ("1".equals(request.getHeader("X-App-Fragment"))) return "saisir_consultation";
        model.addAttribute("userRole", String.valueOf(session.getAttribute("userRole")));
        model.addAttribute("sidebarTitle", "Médecin");
        model.addAttribute("pageTitle", "Saisir consultation");
        model.addAttribute("initialPath", request.getRequestURI());
        return "shell";
    }

    @PostMapping("/save-consultation")
    public String saveConsultation(@RequestParam Integer patientId,
                                   @RequestParam Integer rdvId,
                                   @RequestParam String diagnostic,
                                   @RequestParam(required = false) String antecedents,
                                   @RequestParam(required = false) String observations,
                                   HttpSession session) {
        Integer medecinId = (Integer) session.getAttribute("userId");
        if (medecinId == null) return "redirect:/login.html";
        Medecin medecin = medecinRepository.findById(medecinId).orElse(null);
        Patient patient = patientRepository.findById(patientId).orElse(null);
        RendezVous rdv = rdvRepository.findById(rdvId).orElse(null);

        DossierMedical dossier = dossierRepository.findByPatient_Id(patientId);
        if (dossier != null && antecedents != null) {
            dossier.setAntecedents(antecedents);
            dossierRepository.save(dossier);
        }

        Consultation c = new Consultation();
        c.setDate(LocalDateTime.now());
        c.setDiagnostic(diagnostic);
        c.setObservations(observations);
        c.setPatient(patient);
        c.setMedecin(medecin);
        c.setRendezVous(rdv);
        consultationRepository.save(c);

        return "redirect:/medecin/consultation/" + c.getId() + "/ordonnance";
    }

    @GetMapping("/consultation/{id}/ordonnance")
    public String ordonnance(@PathVariable Integer id, Model model, HttpSession session, HttpServletRequest request) {
        if (session.getAttribute("userId") == null) return "redirect:/login.html";
        Consultation c = consultationRepository.findById(id).orElse(null);
        if (c == null) return "redirect:/medecin/agenda";

        model.addAttribute("consultation", c);
        model.addAttribute("patient", c.getPatient());
        model.addAttribute("prescriptions", prescriptionRepository.findByConsultation_IdOrderByIdDesc(id));

        if ("1".equals(request.getHeader("X-App-Fragment"))) return "ordonnance";
        model.addAttribute("userRole", String.valueOf(session.getAttribute("userRole")));
        model.addAttribute("sidebarTitle", "Médecin");
        model.addAttribute("pageTitle", "Ordonnance");
        model.addAttribute("initialPath", request.getRequestURI());
        return "shell";
    }

    @PostMapping("/consultation/{id}/ordonnance/add")
    public String addPrescription(@PathVariable Integer id,
                                 @RequestParam String medicaments,
                                 @RequestParam(required = false) String posologie,
                                 @RequestParam(required = false) Integer dureeTraitement,
                                 HttpSession session) {
        if (session.getAttribute("userId") == null) return "redirect:/login.html";
        Consultation c = consultationRepository.findById(id).orElse(null);
        if (c == null) return "redirect:/medecin/agenda";

        Prescription pres = new Prescription();
        pres.setMedicaments(medicaments);
        pres.setPosologie(posologie != null ? posologie : "");
        pres.setDureeTraitement(dureeTraitement != null ? dureeTraitement : 0);
        pres.setConsultation(c);
        prescriptionRepository.save(pres);

        return "redirect:/medecin/consultation/" + id + "/ordonnance?success=true";
    }

    @GetMapping("/patient/{id}/dossier")
    public String dossierPatient(@PathVariable Integer id, Model model, HttpSession session, HttpServletRequest request) {
        if (session.getAttribute("userId") == null) return "redirect:/login.html";

        Patient p = patientRepository.findById(id).orElse(null);
        if (p == null) return "redirect:/medecin/agenda";

        DossierMedical dossier = dossierRepository.findByPatient_Id(id);
        model.addAttribute("patient", p);
        model.addAttribute("dossier", dossier);
        model.addAttribute("consultations", consultationRepository.findByPatient_IdOrderByDateDesc(id));
        model.addAttribute("backUrl", "/medecin/agenda");

        if ("1".equals(request.getHeader("X-App-Fragment"))) return "patient_dossier";
        model.addAttribute("userRole", String.valueOf(session.getAttribute("userRole")));
        model.addAttribute("sidebarTitle", "Médecin");
        model.addAttribute("pageTitle", "Dossier patient");
        model.addAttribute("initialPath", request.getRequestURI());
        return "shell";
    }
}