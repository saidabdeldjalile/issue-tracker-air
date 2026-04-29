# Guide d'installation - Sans Docker

## Prérequis

| Logiciel | Version requise | Status actuel |
|----------|-----------------|---------------|
| Java | 17+ | ✅ Installé |
| Python | 3.9+ | ✅ Installé |
| Node.js | 18+ | ❌ À installer |
| PostgreSQL | 14+ | ❌ À installer |

---

## Étape 1 : Installer Node.js

1. Téléchargez Node.js : https://nodejs.org/
2. Choisissez la version **LTS**
3. Pendant l'installation, cochez **"Add to PATH"**
4. Redémarrez le terminal

---

## Étape 2 : Installer PostgreSQL

1. Téléchargez : https://www.postgresql.org/download/windows/
2. Mot de passe postgres : `postgres`
3. Port : `5432`

---

## Étape 3 : Créer la base de données

```
sql
CREATE DATABASE issue_tracker_db;
```

---

## Étape 4 : Lancer les services

### Terminal 1 - Backend (Spring Boot)
```
bash
cd c:\Users\LENOVO\Downloads\IssueTracker-main
.\mvnw spring-boot:run
```

### Terminal 2 - Service de détection de doublons (Python)
```
bash
cd c:\Users\LENOVO\Downloads\IssueTracker-main\duplicate-detection
python -m venv venv
venv\Scripts\activate
pip install -r requirements.txt
python dup.py
```

### Terminal 3 - Frontend (React)
```
bash
cd c:\Users\LENOVO\Downloads\IssueTracker-main\issue-tracker-web
npm install
npm run dev
```

---

## Accès à l'application

- Frontend : http://localhost:5173
- Backend : http://localhost:6969
