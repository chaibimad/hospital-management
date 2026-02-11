# Vérification – Fonctions Administrateur

Ce document fait le lien entre les exigences et les éléments implémentés dans l’application.

---

## 1. Gestion des utilisateurs

**Exigence :** Créer, modifier et supprimer les comptes (médecins, secrétaires, autres admins). Gérer les informations personnelles et professionnelles.

**Implémenté :**
- **Dashboard** (`/dashboard`) : formulaire « Ajouter / Modifier Utilisateur » avec rôle (Médecin, Secrétaire, Administrateur, Patient).
- **Création :** choix du rôle, nom, prénom, email, mot de passe ; pour **Médecin** : spécialité et N° Ordre.
- **Modification :** bouton ✏️ sur chaque ligne ; préremplissage du formulaire (y compris spécialité et N° Ordre pour un médecin).
- **Suppression :** lien ❌ avec confirmation « Supprimer ? ».
- **Informations gérées :** nom, prénom, email, mot de passe, rôle ; pour médecin : spécialité, numeroOrdre.

---

## 2. Gestion des rôles et permissions

**Exigence :** Attribuer et modifier les rôles (administrateur, médecin, secrétaire). Définir les permissions par rôle. Contrôler l’accès aux modules.

**Implémenté :**
- **Rôles :** ADMIN, MEDECIN, SECRETAIRE, PATIENT. Attribués et modifiables via le formulaire utilisateur (liste déroulante).
- **Permissions (accès par rôle) :**
  - **ADMIN :** Dashboard, Journal, Rapports, gestion des comptes.
  - **SECRETAIRE :** Patients, Agenda, Paiements.
  - **MEDECIN :** Agenda médecin, consultations, prescriptions.
  - **PATIENT :** Espace patient, dossier médical, factures, prise de RDV.
- **Contrôle d’accès :** `SessionAuthInterceptor` vérifie la présence d’une session (userId) pour `/dashboard`, `/admin/**`, `/secretaire/**`, `/medecin/**`, `/patient/**`, `/api/patient/**`, `/api/secretaire/**`. Sans session → redirection vers `/login.html`.

---

## 3. Consultation du journal d’activité

**Exigence :** Historique des actions, qui a fait quoi et quand. Filtrer par utilisateur, date, type d’action ou module.

**Implémenté :**
- **Page** `/admin/journal` : liste de toutes les actions (date/heure, auteur, action, détails).
- **Filtres (formulaire) :**
  - **Auteur (email)** : champ texte (ex. ADMIN).
  - **Type d’action** : CRÉATION, MODIFICATION, SUPPRESSION.
  - **Date début / Date fin** : filtre sur la date des actions.
- **Traçabilité :** chaque création, modification et suppression de compte enregistrée dans la table `journal` (utilisateurEmail, action, details, dateHeure).

---

## 4. Génération de rapports

**Exigence :** Rapports statistiques (consultations, rendez-vous, paiements). Exporter les données pour analyse externe.

**Implémenté :**
- **Page Rapports** `/admin/rapports` : indicateurs (patients, médecins, RDV total, consultations total, revenus factures payées).
- **Export CSV** : lien « Exporter en CSV » → `/admin/rapports/export` télécharge `rapport_clinique.csv` avec :
  - Indicateurs (patients, médecins, RDV, consultations, revenus).
  - Extrait du journal (date, auteur, action, détails).

---

## 5. Consultation des statistiques (KPI)

**Exigence :** Visualiser les KPI, suivre patients, consultations, revenus, tendances.

**Implémenté :**
- **Dashboard** : 5 cartes KPI :
  - Nombre de patients
  - Nombre de médecins
  - RDV du jour
  - Nombre total de consultations
  - Revenus (somme des factures au statut PAYEE, en DH).
- **Rapports** : mêmes indicateurs + RDV total et export CSV pour analyse externe.

---

## 6. Contrôle d’accès et sécurité

**Exigence :** Règles d’accès aux données sensibles, paramètres de sécurité, supervision des accès non autorisés.

**Implémenté :**
- **Contrôle d’accès :** toutes les URLs « métier » (dashboard, admin, secretaire, medecin, patient, API associées) passent par `SessionAuthInterceptor` ; sans session → redirection vers `/login.html?session=expired`.
- **Connexion :** uniquement via formulaire (email + mot de passe) ; après succès, `userId` et `userRole` sont stockés en session.
- **Déconnexion :** `/logout` invalide la session.
- **Données sensibles :** accès aux pages et API selon la présence d’une session valide (pas d’accès anonyme aux modules protégés).

---

## Récapitulatif des URLs Admin

| URL | Description |
|-----|-------------|
| `/dashboard` | Tableau de bord, KPI, liste des comptes, formulaire utilisateur |
| `/admin/saveUser` | POST – Création / modification d’un utilisateur |
| `/admin/user/delete/{id}` | Suppression d’un compte |
| `/admin/journal` | Journal d’activité avec filtres |
| `/admin/rapports` | Rapports statistiques |
| `/admin/rapports/export` | Export CSV des rapports et du journal |
