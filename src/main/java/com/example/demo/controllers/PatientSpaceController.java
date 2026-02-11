package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.models.DossierMedical;
import com.example.demo.models.Patient;
import com.example.demo.repositories.PrescriptionRepository;
import com.example.demo.repositories.RendezVousRepository;
import com.example.demo.repositories.ConsultationRepository;
import com.example.demo.repositories.DossierMedicalRepository;
import com.example.demo.repositories.FactureRepository;
import com.example.demo.repositories.PatientRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class PatientSpaceController {

    @Autowired private DossierMedicalRepository dossierMedicalRepository;
    @Autowired private PatientRepository patientRepository;
    @Autowired private FactureRepository factureRepository;
    @Autowired private ConsultationRepository consultationRepository;
    @Autowired private RendezVousRepository rendezVousRepository;
    @Autowired private PrescriptionRepository prescriptionRepository;

    @GetMapping("/patient/space")
    public String afficherEspace(Model model, HttpSession session, HttpServletRequest request) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login.html";
        Patient p = patientRepository.findById(userId).orElse(null);
        if (p == null) return "redirect:/login.html";
        model.addAttribute("patient", p);
        model.addAttribute("rdvs", rendezVousRepository.findByPatient_IdOrderByDateHeureDesc(userId));
        if ("1".equals(request.getHeader("X-App-Fragment"))) return "patient_space";
        model.addAttribute("userRole", String.valueOf(session.getAttribute("userRole")));
        model.addAttribute("sidebarTitle", "Patient");
        model.addAttribute("pageTitle", "Mon Espace Santé - Patient");
        model.addAttribute("initialPath", request.getRequestURI());
        return "shell";
    }

    @GetMapping("/patient/ordonnances")
    public String afficherOrdonnancesPage(Model model, HttpSession session, HttpServletRequest request) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login.html";
        Patient p = patientRepository.findById(userId).orElse(null);
        if (p == null) return "redirect:/login.html";

        model.addAttribute("patient", p);
        model.addAttribute("prescriptions", prescriptionRepository.findByConsultation_Patient_IdOrderByIdDesc(userId));
        if ("1".equals(request.getHeader("X-App-Fragment"))) return "patient_ordonnances";
        model.addAttribute("userRole", String.valueOf(session.getAttribute("userRole")));
        model.addAttribute("sidebarTitle", "Patient");
        model.addAttribute("pageTitle", "Mes ordonnances - Patient");
        model.addAttribute("initialPath", request.getRequestURI());
        return "shell";
    }

    @GetMapping("/patient/dossier")
    public String afficherDossier(Model model, HttpSession session, HttpServletRequest request) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login.html";
        DossierMedical dossier = dossierMedicalRepository.findByPatient_Id(userId);
        model.addAttribute("dossier", dossier);
        model.addAttribute("patient", patientRepository.findById(userId).orElse(null));
        model.addAttribute("consultations", consultationRepository.findByPatient_IdOrderByDateDesc(userId));
        if ("1".equals(request.getHeader("X-App-Fragment"))) return "patient_dossier";
        model.addAttribute("userRole", String.valueOf(session.getAttribute("userRole")));
        model.addAttribute("sidebarTitle", "Patient");
        model.addAttribute("pageTitle", "Dossier médical - Patient");
        model.addAttribute("initialPath", request.getRequestURI());
        return "shell";
    }

    @GetMapping("/patient/factures")
    public String afficherFactures(Model model, HttpSession session, HttpServletRequest request) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login.html";
        model.addAttribute("factures", factureRepository.findByPatient_IdOrderByDateEmissionDesc(userId));
        if ("1".equals(request.getHeader("X-App-Fragment"))) return "patient_factures";
        model.addAttribute("userRole", String.valueOf(session.getAttribute("userRole")));
        model.addAttribute("sidebarTitle", "Patient");
        model.addAttribute("pageTitle", "Mes factures - Patient");
        model.addAttribute("initialPath", request.getRequestURI());
        return "shell";
    }

    @GetMapping("/patient/profil")
    public String profil(Model model, HttpSession session, HttpServletRequest request) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login.html";
        Patient p = patientRepository.findById(userId).orElse(null);
        if (p == null) return "redirect:/login.html";
        model.addAttribute("patient", p);
        if ("1".equals(request.getHeader("X-App-Fragment"))) return "patient_profil";
        model.addAttribute("userRole", String.valueOf(session.getAttribute("userRole")));
        model.addAttribute("sidebarTitle", "Patient");
        model.addAttribute("pageTitle", "Profil - Patient");
        model.addAttribute("initialPath", request.getRequestURI());
        return "shell";
    }
}