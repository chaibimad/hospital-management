# Base de données – Structure et configuration

## Configuration (Aiven MySQL)

La connexion est définie dans `src/main/resources/application.properties` :

- **URL :** MySQL Aiven (SSL activé)
- **Utilisateur / mot de passe :** variables d’environnement `AIVEN_DB_USER` et `AIVEN_DB_PASSWORD` (ou valeurs par défaut dans le fichier)
- **Création des tables :** `spring.jpa.hibernate.ddl-auto=update`  
  → Hibernate crée ou met à jour les tables à partir des entités JPA au démarrage de l’application. Aucun script SQL manuel n’est nécessaire pour la structure.

---

## Schéma des tables (généré par Hibernate)

Avec l’héritage **JOINED** sur `Utilisateur`, les tables sont les suivantes.

### 1. `utilisateur` (table de base)

| Colonne        | Type         | Description        |
|----------------|--------------|--------------------|
| id             | INT (PK)     | Identifiant        |
| email          | VARCHAR(255)  | Email / login      |
| mot_de_passe   | VARCHAR(255)  | Mot de passe       |
| role           | VARCHAR(255)  | ADMIN, MEDECIN, SECRETAIRE, PATIENT |
| nom            | VARCHAR(255) | Nom                |
| prenom         | VARCHAR(255) | Prénom             |

### 2. `patient` (hérite de utilisateur)

| Colonne           | Type         | Description           |
|-------------------|--------------|-----------------------|
| id                | INT (PK, FK → utilisateur.id) | Même id que dans `utilisateur` |
| telephone         | VARCHAR(255) | Téléphone             |
| adresse           | VARCHAR(255) | Adresse               |
| numero_securite   | VARCHAR(255) | N° sécurité sociale  |
| groupe_sanguin    | VARCHAR(255) | Groupe sanguin        |
| date_inscription  | DATE         | Date d’inscription    |
| date_naissance    | DATE         | Date de naissance     |
| sexe              | VARCHAR(255) | Sexe                  |

### 3. `medecin` (hérite de utilisateur)

| Colonne      | Type         | Description      |
|--------------|--------------|------------------|
| id           | INT (PK, FK → utilisateur.id) | Même id que dans `utilisateur` |
| specialite   | VARCHAR(255) | Spécialité       |
| numero_ordre | VARCHAR(255) | N° ordre         |

### 4. `rendez_vous`

| Colonne     | Type    | Description        |
|-------------|---------|--------------------|
| id          | INT (PK)| Identifiant        |
| date_heure  | DATETIME| Date et heure du RDV |
| motif       | VARCHAR(255) | Motif (ex. grippe) |
| statut      | VARCHAR(255) | EN_ATTENTE, CONFIRMÉ |
| patient_id  | INT (FK)| → patient.id       |
| medecin_id  | INT (FK)| → medecin.id (peut être NULL) |

### 5. `dossier_medical`

| Colonne     | Type  | Description     |
|-------------|-------|-----------------|
| id          | INT (PK) | Identifiant  |
| antecedents | TEXT  | Antécédents     |
| allergies   | TEXT  | Allergies       |
| traitements | TEXT  | Traitements     |
| patient_id  | INT (FK) | → patient.id (1 dossier par patient) |

### 6. `consultation`

| Colonne        | Type    | Description        |
|----------------|---------|--------------------|
| id             | INT (PK)| Identifiant        |
| date           | DATETIME| Date de la consultation |
| diagnostic     | TEXT    | Diagnostic         |
| observations   | TEXT    | Observations       |
| patient_id     | INT (FK)| → patient.id       |
| medecin_id     | INT (FK)| → medecin.id      |
| rendezvous_id  | INT (FK)| → rendez_vous.id   |

### 7. `prescription`

| Colonne            | Type    | Description        |
|-------------------|---------|--------------------|
| id                | INT (PK)| Identifiant        |
| medicaments       | VARCHAR(255) | Médicaments   |
| posologie         | VARCHAR(255) | Posologie     |
| duree_traitement  | INT     | Durée (jours)      |
| consultation_id   | INT (FK)| → consultation.id  |

### 8. `facture`

| Colonne          | Type    | Description        |
|------------------|---------|--------------------|
| id               | INT (PK)| Identifiant        |
| montant          | DOUBLE  | Montant (DH)       |
| date_emission     | DATETIME| Date d’émission     |
| statut           | VARCHAR(255) | EN_ATTENTE, PAYEE |
| patient_id       | INT (FK)| → patient.id       |
| consultation_id  | INT (FK)| → consultation.id (optionnel) |

### 9. `journal`

| Colonne          | Type    | Description        |
|------------------|---------|--------------------|
| id               | INT (PK)| Identifiant        |
| utilisateur_email| VARCHAR(255) | Qui a agi (ex. ADMIN) |
| action           | VARCHAR(255) | CRÉATION, MODIFICATION, SUPPRESSION |
| details          | VARCHAR(255) | Détail de l’action |
| date_heure       | DATETIME| Date et heure      |

---

## Résumé

- **Connexion :** Aiven MySQL, paramètres dans `application.properties` (et variables d’environnement pour user/password).
- **Création / mise à jour du schéma :** automatique au démarrage grâce à `ddl-auto=update` et aux entités JPA.
- **Pas de script SQL à exécuter à la main** pour la structure ; il suffit de lancer l’application pour que la base soit à jour.

Pour forcer une recréation complète des tables (attention : perte des données), on peut temporairement mettre `spring.jpa.hibernate.ddl-auto=create` puis redémarrer.
