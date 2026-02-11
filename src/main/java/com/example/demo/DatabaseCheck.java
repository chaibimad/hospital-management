package com.example.demo;

import com.example.demo.models.Secretaire;
import com.example.demo.models.Utilisateur;
import com.example.demo.repositories.MedecinRepository;
import com.example.demo.repositories.PatientRepository;
import com.example.demo.repositories.SecretaireRepository;
import com.example.demo.repositories.UtilisateurRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseCheck {

    @Bean
    public ApplicationRunner checkConnection(PatientRepository patientRepo, MedecinRepository medecinRepo, SecretaireRepository secretaireRepo, UtilisateurRepository userRepo) {
        return args -> {
            try {
                System.out.println("✅ CONNEXION RÉUSSIE À AIVEN SQL !");
                System.out.println("📊 Nombre de patients : " + patientRepo.count());
                System.out.println("📊 Nombre de médecins : " + medecinRepo.count());
                System.out.println("📊 Nombre de secrétaires : " + secretaireRepo.count());
                if (userRepo.count() == 0) {
                    Utilisateur admin = new Utilisateur();
                    admin.setEmail("admin@hopital.ma");
                    admin.setMotDePasse("1234");
                    admin.setRole("ADMIN");
                    admin.setNom("System");
                    admin.setPrenom("Admin");
                    userRepo.save(admin);
                    System.out.println("👤 Compte admin créé : admin@hopital.ma / 1234");
                }

                // Créer un compte secrétaire par défaut si aucun n'existe
                if (secretaireRepo.count() == 0) {
                    Secretaire secretaire = new Secretaire();
                    secretaire.setEmail("secretariat@hopital.ma");
                    secretaire.setMotDePasse("1234");
                    secretaire.setRole("SECRETAIRE");
                    secretaire.setNom("Secrétaire");
                    secretaire.setPrenom("Principal");
                    secretaire.setCodeEmploye("SEC001");
                    secretaireRepo.save(secretaire);
                    System.out.println("👩 Compte secrétaire créé : secretariat@hopital.ma / 1234");
                }
            } catch (Exception e) {
                System.err.println("❌ Erreur au démarrage (base de données) : " + e.getMessage());
                e.printStackTrace(System.err);
                // Ne pas relancer l'exception : l'app reste démarrée pour débogage
            }
        };
    }
}