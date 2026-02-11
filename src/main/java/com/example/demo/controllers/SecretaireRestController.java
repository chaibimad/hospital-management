package com.example.demo.controllers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.models.DossierMedical;
import com.example.demo.models.Medecin;
import com.example.demo.models.Patient;
import com.example.demo.repositories.DossierMedicalRepository;
import com.example.demo.repositories.MedecinRepository;
import com.example.demo.repositories.PatientRepository;
import com.example.demo.repositories.RendezVousRepository;

@RestController
@RequestMapping("/api")
public class SecretaireRestController {

    @Autowired private PatientRepository patientRepository;
    @Autowired private MedecinRepository medecinRepository;
    @Autowired private RendezVousRepository rdvRepository;
    @Autowired private DossierMedicalRepository dossierMedicalRepository;

    @GetMapping("/secretaire/patients")
    public List<Patient> getPatients(@RequestParam(required = false) String search) {
        List<Patient> all = patientRepository.findAll();
        if (search == null || search.isBlank()) return all;
        String s = search.toLowerCase().trim();
        return all.stream()
                .filter(p -> (p.getId() != null && String.valueOf(p.getId()).contains(s))
                        || (p.getNom() != null && p.getNom().toLowerCase().contains(s))
                        || (p.getPrenom() != null && p.getPrenom().toLowerCase().contains(s))
                        || (p.getTelephone() != null && p.getTelephone().contains(s))
                        || (p.getNumeroSecurite() != null && p.getNumeroSecurite().contains(s))
                        || (p.getEmail() != null && p.getEmail().toLowerCase().contains(s)))
                .collect(Collectors.toList());
    }

    @GetMapping("/secretaire/patients/{id}")
    public Patient getPatient(@PathVariable Integer id) {
        return patientRepository.findById(id).orElseThrow();
    }

    @PostMapping("/secretaire/patients")
    public Patient savePatient(@RequestBody Patient patient) {
        if (patient.getRole() == null || patient.getRole().isBlank()) patient.setRole("PATIENT");
        if (patient.getDateInscription() == null) patient.setDateInscription(LocalDate.now());
        return patientRepository.save(patient);
    }

    @PutMapping("/secretaire/patients/{id}")
    public Patient updatePatient(@PathVariable Integer id, @RequestBody Patient patient) {
        Patient existing = patientRepository.findById(id).orElseThrow();
        existing.setNom(patient.getNom());
        existing.setPrenom(patient.getPrenom());
        existing.setEmail(patient.getEmail());
        if (patient.getMotDePasse() != null && !patient.getMotDePasse().isBlank()) existing.setMotDePasse(patient.getMotDePasse());
        existing.setNumeroSecurite(patient.getNumeroSecurite());
        existing.setGroupeSanguin(patient.getGroupeSanguin());
        existing.setTelephone(patient.getTelephone());
        existing.setAdresse(patient.getAdresse());
        if (patient.getDateNaissance() != null) existing.setDateNaissance(patient.getDateNaissance());
        return patientRepository.save(existing);
    }

    @DeleteMapping("/secretaire/patients/{id}")
    public void deletePatient(@PathVariable Integer id) {
        Patient p = patientRepository.findById(id).orElseThrow();
        DossierMedical d = dossierMedicalRepository.findByPatient_Id(id);
        if (d != null) dossierMedicalRepository.delete(d);
        patientRepository.delete(p);
    }

    @GetMapping("/secretaire/patients/rdv-counts")
    public Map<Integer, Long> getRdvCountsByPatient() {
        Map<Integer, Long> counts = new HashMap<>();
        for (Patient p : patientRepository.findAll()) {
            counts.put(p.getId(), rdvRepository.countByPatient_Id(p.getId()));
        }
        return counts;
    }

    @GetMapping("/medecins")
    public List<Medecin> getMedecins() {
        return medecinRepository.findAll();
    }
}