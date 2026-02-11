# Vérification – Secrétaire et Patient

## SECRÉTAIRE

### Gestion des patients

| Exigence | Implémenté | Détail |
|----------|------------|--------|
| **Créer un patient** | Oui | Formulaire « + Ajouter un patient » : nom, prénom, email, mot de passe, N° sécurité sociale, groupe sanguin, date de naissance, téléphone, adresse. **Dossier médical vierge créé automatiquement** à la première sauvegarde. |
| **Modifier un patient** | Oui | Bouton « Modifier » par ligne : formulaire prérempli, mise à jour de toutes les infos (y compris date de naissance). |
| **Supprimer un patient** | Oui | Bouton « Supprimer » avec **confirmation** et mention **RGPD** (données personnelles effacées). Suppression du dossier médical puis du patient. |
| **Rechercher un patient** | Oui | Champ « Rechercher (nom, prénom, tél, n° sécu, email) » : filtre côté serveur via `?search=`. |

### Rendez-vous

| Exigence | Implémenté | Détail |
|----------|------------|--------|
| **Planifier un rendez-vous** | Oui | Formulaire Agenda : choix **patient**, **médecin**, **date/heure**, **motif**. Statut mis à « CONFIRMÉ ». (Disponibilité médecin : à affiner manuellement ; pas d’envoi email/SMS pour l’instant.) |
| **Modifier un rendez-vous** | Oui | Lien « Modifier » par RDV → page d’édition (patient, médecin, date/heure, motif). |
| **Annuler un rendez-vous** | Oui | Bouton « Annuler » avec **raison optionnelle** (`motifAnnulation`). Statut passé à **ANNULE** → créneau libéré dans l’agenda. |
| **Consulter l’agenda** | Oui | Page Agenda : tous les RDV de la clinique, avec patient, médecin, motif, **statut** (EN_ATTENTE, CONFIRMÉ, ANNULE, TERMINE). |
| **Suivre l’état des rendez-vous** | Oui | Statuts : **EN_ATTENTE**, **CONFIRMÉ**, **ANNULE**, **TERMINE**. Bouton « Terminer » pour marquer un RDV comme **TERMINE**. |

### Facturation et paiements

| Exigence | Implémenté | Détail |
|----------|------------|--------|
| **Générer une facture** | Oui | « + Nouvelle facture » : patient, montant, lien optionnel à une consultation. Facture créée en **EN_ATTENTE**. (Calcul selon type de consultation : à étendre si besoin.) |
| **Enregistrer un paiement** | Oui | Bouton « Enregistrer paiement » avec **mode de paiement** : Espèces, Carte bancaire, Chèque, Virement. **Date de paiement** enregistrée automatiquement. Statut passé à **PAYEE**. |
| **Consulter l’historique des paiements** | Oui | Page Paiements : liste de toutes les factures (patient, montant, date, statut, mode si payé). |
| **Rappels de paiement** | Partiel | Pas d’envoi automatique email/SMS ; les factures **EN_ATTENTE** sont visibles dans la liste pour relance manuelle. |

---

## PATIENT

### Connexion et portail

| Exigence | Implémenté | Détail |
|----------|------------|--------|
| **Se connecter au portail** | Oui | Connexion par **email + mot de passe** (page login). Session avec `userId` / `userRole`. Accès 24h/24 dès que l’app est disponible. (Réinitialisation mot de passe : non implémentée.) |

### Rendez-vous (self-service)

| Exigence | Implémenté | Détail |
|----------|------------|--------|
| **Planifier un rendez-vous** | Oui | « Prendre RDV » : choix du **médecin** (liste), **date/heure**, **motif**. Enregistrement avec statut EN_ATTENTE. (Créneaux disponibles en temps réel et confirmation email/SMS : non implémentés.) |
| **Consulter l’agenda** | Oui | « Mes prochains rendez-vous » sur l’espace patient : date, heure, motif, **statut** (EN_ATTENTE, CONFIRMÉ, ANNULE, TERMINE). |
| **Modifier / Annuler un rendez-vous** | Oui | Bouton **« Annuler ce RDV »** pour chaque RDV non annulé/non terminé. Annulation enregistrée (statut **ANNULE**). (Reporter / modifier date-heure : via la secrétaire pour l’instant.) |

### Factures et paiements

| Exigence | Implémenté | Détail |
|----------|------------|--------|
| **Consulter l’historique des paiements** | Oui | Page « Mes factures » : liste des factures (date, montant, statut). (Téléchargement PDF : non implémenté.) |

### Dossier médical

| Exigence | Implémenté | Détail |
|----------|------------|--------|
| **Accéder à son dossier médical** | Oui | « Consulter mon dossier » : **lecture seule** – antécédents, allergies, traitements. Historique des consultations et prescriptions côté médecin (saisie consultation). |

### Informations personnelles

| Exigence | Implémenté | Détail |
|----------|------------|--------|
| **Mettre à jour ses informations** | Oui | « Modifier mes infos » : mise à jour **adresse**, **téléphone**, **email** (coordonnées de contact). |

### Notifications

| Exigence | Implémenté | Détail |
|----------|------------|--------|
| **Recevoir des notifications** | Non | Pas d’envoi email/SMS (rappels RDV, confirmations, alertes paiement). À ajouter avec un module email/SMS si besoin. |

---

## Récapitulatif technique

- **Secrétaire** : création/modification/suppression patient (avec dossier médical créé à la création, recherche, RGPD), agenda complet (création, modification, annulation, marquer terminé), factures (création, enregistrement paiement avec mode et date).
- **Patient** : connexion, prise de RDV (médecin + date + motif), consultation des RDV et annulation, consultation factures et dossier médical, modification des coordonnées (profil).
- **Non couvert** : envoi email/SMS (confirmations, rappels), réinitialisation mot de passe, export PDF des factures, affichage « créneaux disponibles » en temps réel.
