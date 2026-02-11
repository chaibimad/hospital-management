package com.example.demo.controllers;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.models.Facture;
import com.example.demo.models.RendezVous;
import com.example.demo.repositories.ConsultationRepository;
import com.example.demo.repositories.FactureRepository;
import com.example.demo.repositories.MedecinRepository;
import com.example.demo.repositories.PatientRepository;
import com.example.demo.repositories.RendezVousRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/secretaire")
public class SecretaireController {

    @Autowired private PatientRepository patientRepository;
    @Autowired private MedecinRepository medecinRepository;
    @Autowired private RendezVousRepository rdvRepository;
    @Autowired private FactureRepository factureRepository;
    @Autowired private ConsultationRepository consultationRepository;

    @GetMapping("/patients")
    public String patientsPage(Model model, HttpServletRequest request, HttpSession session) {
        if ("1".equals(request.getHeader("X-App-Fragment"))) return "secretaire_patients";
        model.addAttribute("userRole", String.valueOf(session.getAttribute("userRole")));
        model.addAttribute("sidebarTitle", "Secrétariat");
        model.addAttribute("pageTitle", "Patients - Secrétariat");
        model.addAttribute("initialPath", request.getRequestURI());
        return "shell";
    }

    @GetMapping("/agenda")
    public String agenda(Model model, HttpServletRequest request, HttpSession session) {
        List<RendezVous> rdvs = rdvRepository.findAll();
        model.addAttribute("rdvs", rdvs);
        model.addAttribute("listePatients", patientRepository.findAll());
        model.addAttribute("listeMedecins", medecinRepository.findAll());
        if ("1".equals(request.getHeader("X-App-Fragment"))) return "secretaire_agenda";
        model.addAttribute("userRole", String.valueOf(session.getAttribute("userRole")));
        model.addAttribute("sidebarTitle", "Secrétariat");
        model.addAttribute("pageTitle", "Agenda - Secrétariat");
        model.addAttribute("initialPath", request.getRequestURI());
        return "shell";
    }

    @PostMapping("/rdv/save")
    public String saveRdv(@RequestParam(required = false) Integer id,
                         @RequestParam Integer patientId,
                         @RequestParam Integer medecinId,
                         @RequestParam String dateHeure,
                         @RequestParam String motif) {
        RendezVous rdv = id != null ? rdvRepository.findById(id).orElse(new RendezVous()) : new RendezVous();
        rdv.setDateHeure(LocalDateTime.parse(dateHeure));
        rdv.setMotif(motif);
        if (rdv.getStatut() == null || rdv.getStatut().isBlank()) {
            rdv.setStatut("EN_ATTENTE");
        }
        rdv.setPatient(patientRepository.findById(patientId).orElse(null));
        rdv.setMedecin(medecinRepository.findById(medecinId).orElse(null));
        rdvRepository.save(rdv);
        return "redirect:/secretaire/agenda?success=true";
    }

    @PostMapping("/rdv/confirmer/{id}")
    public String confirmerRdv(@PathVariable Integer id) {
        RendezVous rdv = rdvRepository.findById(id).orElse(null);
        if (rdv != null && !"ANNULE".equals(rdv.getStatut()) && !"TERMINE".equals(rdv.getStatut())) {
            rdv.setStatut("CONFIRMÉ");
            rdvRepository.save(rdv);
        }
        return "redirect:/secretaire/agenda?success=true";
    }

    @PostMapping("/rdv/arrive/{id}")
    public String marquerArrive(@PathVariable Integer id) {
        RendezVous rdv = rdvRepository.findById(id).orElse(null);
        if (rdv != null && !"ANNULE".equals(rdv.getStatut()) && !"TERMINE".equals(rdv.getStatut())) {
            rdv.setStatut("ARRIVE");
            rdvRepository.save(rdv);
        }
        return "redirect:/secretaire/agenda?success=true";
    }

    @GetMapping("/rdv/modifier/{id}")
    public String modifierRdv(@PathVariable Integer id, Model model, HttpServletRequest request, HttpSession session) {
        RendezVous rdv = rdvRepository.findById(id).orElse(null);
        if (rdv == null) return "redirect:/secretaire/agenda";
        model.addAttribute("rdv", rdv);
        model.addAttribute("listePatients", patientRepository.findAll());
        model.addAttribute("listeMedecins", medecinRepository.findAll());
        if ("1".equals(request.getHeader("X-App-Fragment"))) return "secretaire_agenda_edit";
        model.addAttribute("userRole", String.valueOf(session.getAttribute("userRole")));
        model.addAttribute("sidebarTitle", "Secrétariat");
        model.addAttribute("pageTitle", "Modifier RDV - Secrétariat");
        model.addAttribute("initialPath", request.getRequestURI());
        return "shell";
    }

    @GetMapping("/paiements")
    public String paiements(Model model, HttpServletRequest request, HttpSession session) {
        model.addAttribute("factures", factureRepository.findAll());
        LocalDateTime start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        List<Facture> paidToday = factureRepository.findByStatutAndDatePaiementBetweenOrderByDatePaiementDesc("PAYEE", start, end);
        model.addAttribute("paidToday", paidToday);
        model.addAttribute("totalPaidToday", paidToday.stream().mapToDouble(f -> f.getMontant() != null ? f.getMontant() : 0d).sum());
        if ("1".equals(request.getHeader("X-App-Fragment"))) return "secretaire_paiements";
        model.addAttribute("userRole", String.valueOf(session.getAttribute("userRole")));
        model.addAttribute("sidebarTitle", "Secrétariat");
        model.addAttribute("pageTitle", "Paiements - Secrétariat");
        model.addAttribute("initialPath", request.getRequestURI());
        return "shell";
    }

    @PostMapping("/paiement/valider")
    public String validerPaiement(@RequestParam Integer factureId,
                                  @RequestParam(required = false) String modePaiement) {
        Facture f = factureRepository.findById(factureId).orElse(null);
        if (f != null) {
            f.setStatut("PAYEE");
            f.setModePaiement(modePaiement != null && !modePaiement.isBlank() ? modePaiement : "ESPECES");
            f.setDatePaiement(LocalDateTime.now());
            factureRepository.save(f);
        }
        return "redirect:/secretaire/paiements?success=true";
    }

    @PostMapping("/rdv/annuler/{id}")
    public String annulerRdv(@PathVariable Integer id, @RequestParam(required = false) String motifAnnulation) {
        RendezVous rdv = rdvRepository.findById(id).orElse(null);
        if (rdv != null) {
            rdv.setStatut("ANNULE");
            rdv.setMotifAnnulation(motifAnnulation != null ? motifAnnulation : "Annulé par la secrétaire");
            rdvRepository.save(rdv);
        }
        return "redirect:/secretaire/agenda?cancelled=true";
    }

    @PostMapping("/rdv/terminer/{id}")
    public String terminerRdv(@PathVariable Integer id) {
        RendezVous rdv = rdvRepository.findById(id).orElse(null);
        if (rdv != null) {
            rdv.setStatut("TERMINE");
            rdvRepository.save(rdv);
        }
        return "redirect:/secretaire/agenda?done=true";
    }

    @GetMapping("/facture/nouvelle")
    public String nouvelleFacture(Model model, HttpServletRequest request, HttpSession session) {
        model.addAttribute("patients", patientRepository.findAll());
        model.addAttribute("consultations", consultationRepository.findAll());
        if ("1".equals(request.getHeader("X-App-Fragment"))) return "secretaire_facture_form";
        model.addAttribute("userRole", String.valueOf(session.getAttribute("userRole")));
        model.addAttribute("sidebarTitle", "Secrétariat");
        model.addAttribute("pageTitle", "Nouvelle facture - Secrétariat");
        model.addAttribute("initialPath", request.getRequestURI());
        return "shell";
    }

    @PostMapping("/facture/save")
    public String saveFacture(@RequestParam Integer patientId,
                              @RequestParam Double montant,
                              @RequestParam(required = false) Integer consultationId) {
        Facture f = new Facture();
        f.setPatient(patientRepository.findById(patientId).orElse(null));
        f.setMontant(montant);
        f.setDateEmission(LocalDateTime.now());
        f.setStatut("EN_ATTENTE");
        if (consultationId != null) {
            f.setConsultation(consultationRepository.findById(consultationId).orElse(null));
        }
        factureRepository.save(f);
        return "redirect:/secretaire/paiements?created=true";
    }
}
