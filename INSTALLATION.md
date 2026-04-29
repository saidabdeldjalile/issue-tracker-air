# Guide d'Installation Détaillé - IssueTracker

Ce guide vous accompagnera pas à pas dans l'installation et la configuration du projet IssueTracker.

## 📋 Prérequis

Aviez de commencer, assurez-vous d'avoir installé :

- **Java 17+** : [Télécharger JDK 17](https://www.oracle.com/java/technologies/downloads/#java17)
- **Node.js 18+** : [Télécharger Node.js](https://nodejs.org/)
- **PostgreSQL 12+** : [Télécharger PostgreSQL](https://www.postgresql.org/download/)
- **Python 3.8+** (optionnel, pour la détection de doublons) : [Télécharger Python](https://www.python.org/downloads/)
- **Git** : [Télécharger Git](https://git-scm.com/)

---

## 🗄️ Étape 1 : Configuration de PostgreSQL

### 1.1 Installer PostgreSQL

1. Téléchargez et installez PostgreSQL depuis le site officiel
2. Pendant l'installation, notez le mot de passe que vous définissez pour l'utilisateur `postgres`
3. **Port par défaut** : PostgreSQL utilise le port `5432` par défaut, mais ce projet est configuré pour utiliser le port `5433`

### 1.2 Changer le port de PostgreSQL (Recommandé)

**Pourquoi changer le port ?**
- Éviter les conflits avec d'autres installations PostgreSQL
- Correspondre à la configuration du projet

**Comment changer le port :**

#### Sur Windows :
1. Allez dans le répertoire d'installation de PostgreSQL (ex: `C:\Program Files\PostgreSQL\15\data`)
2. Ouvrez le fichier `postgresql.conf`
3. Trouvez la ligne `#port = 5432` et modifiez-la en `port = 5433`
4. Redémarrez le service PostgreSQL :
   - Ouvrez "Services" (Win + R, tapez `services.msc`)
   - Trouvez "postgresql-x64-15" (ou votre version)
   - Clic droit → Redémarrer

#### Sur Mac/Linux :
```bash
# Éditez le fichier de configuration
sudo nano /etc/postgresql/15/main/postgresql.conf

# Modifiez la ligne :
port = 5433

# Redémarrez PostgreSQL
sudo systemctl restart postgresql
```

### 1.3 Créer la base de données

Ouvrez pgAdmin ou l'outil en ligne de commande `psql` :

```bash
# Se connecter à PostgreSQL (entrez le mot de passe quand demandé)
psql -U postgres -p 5433

# Créer la base de données
CREATE DATABASE issue_tracker_db;

# Vérifier que la base a été créée
\l

# Quitter psql
\q
```

### 1.4 Vérifier la connexion

```bash
# Tester la connexion
psql -U postgres -p 5433 -d issue_tracker_db

# Si ça fonctionne, vous verrez : issue_tracker_db=>
```

---

## 🔧 Étape 2 : Configuration du Backend (Spring Boot)

### 2.1 Cloner le projet

```bash
# Ouvrez un terminal dans un dossier approprié
git clone <URL_DU_REPOSITORY>
cd IssueTracker-main
```

### 2.2 Configurer les variables d'environnement

1. Copiez le fichier d'exemple :
   ```bash
   copy .env.example .env
   ```

2. Éditez le fichier `.env` avec vos paramètres :
   ```properties
   # Si vous avez gardé le port 5432 par défaut, modifiez :
   POSTGRES_URL=jdbc:postgresql://localhost:5432/issue_tracker_db
   
   # Sinon, laissez le port 5433 :
   POSTGRES_URL=jdbc:postgresql://localhost:5433/issue_tracker_db
   
   # Mettez le mot de passe que vous avez défini lors de l'installation de PostgreSQL
   DATABASE_PASSWORD=votre_mot_de_passe
   
   # Générez une nouvelle clé JWT (optionnel mais recommandé)
   # Sur Windows PowerShell :
   [System.Convert]::ToBase64String([System.Text.Encoding]::UTF8.GetBytes((New-Object Security.Cryptography.RNGCryptoServiceProvider).GetBytes(32)))
   
   # Sur Mac/Linux :
   openssl rand -base64 32
   ```

### 2.3 Vérifier la configuration Maven

Le projet utilise Maven. Vérifiez que vous avez Maven ou utilisez le wrapper inclus :

```bash
# Vérifier la version de Maven
mvn --version

# Si Maven n'est pas installé, le projet inclut mvnw (Maven Wrapper)
# Il téléchargera Maven automatiquement
```

### 2.4 Compiler le projet

```bash
# Nettoyer et compiler
mvn clean compile

# Ou avec le wrapper Maven
./mvnw clean compile  # Sur Mac/Linux
mvnw.cmd clean compile  # Sur Windows
```

### 2.5 Lancer le backend

```bash
# Méthode 1 : Utiliser Maven
mvn spring-boot:run

# Méthode 2 : Utiliser le script fourni
.\restart_backend.bat  # Windows
```

**Vérification :**
- Le backend devrait démarrer sur `http://localhost:6969`
- Dans les logs, cherchez : `Started IssueTrackerApplication in X seconds`

---

## 🎨 Étape 3 : Configuration du Frontend (React)

### 3.1 Accéder au dossier frontend

```bash
cd issue-tracker-web
```

### 3.2 Installer les dépendances

```bash
# Avec npm (recommandé)
npm install

# Ou avec pnpm (plus rapide)
pnpm install
```

### 3.3 Configurer le frontend

1. Créez un fichier `.env` dans `issue-tracker-web/` :
   ```bash
   copy .env.example .env
   ```

2. Le fichier `.env` doit contenir :
   ```env
   VITE_API_URL=http://localhost:6969
   ```

### 3.4 Lancer le frontend

```bash
# Dans le dossier issue-tracker-web
npm run dev
```

**Vérification :**
- Le frontend devrait démarrer sur `http://localhost:5173`
- Dans le terminal, vous verrez : `Local: http://localhost:5173/`

---

## 🐍 Étape 4 : Configuration du Service Python (Optionnel)

### 4.1 Accéder au dossier duplicate-detection

```bash
cd duplicate-detection
```

### 4.2 Créer un environnement virtuel

```bash
# Créer l'environnement virtuel
python -m venv dupenv

# Activer l'environnement
# Sur Windows PowerShell :
.\dupenv\Scripts\Activate.ps1

# Sur Windows CMD :
dupenv\Scripts\activate

# Sur Mac/Linux :
source dupenv/bin/activate
```

### 4.3 Installer les dépendances

```bash
pip install -r requirements.txt
```

### 4.4 Télécharger les données NLTK

```bash
python download_nltk.py
```

### 4.5 Lancer le service

```bash
# Méthode 1 : Développement
python dup.py

# Méthode 2 : Production-like
waitress-serve --listen=*:5000 dup:app
```

**Vérification :**
- Le service devrait être accessible sur `http://localhost:5000`

---

## 🚀 Étape 5 : Démarrage Complet

### Option A : Démarrage Manuel

Ouvrez 3 terminaux séparés :

**Terminal 1 - Backend :**
```bash
cd IssueTracker-main
mvn spring-boot:run
```

**Terminal 2 - Frontend :**
```bash
cd issue-tracker-web
npm run dev
```

**Terminal 3 - Service Python (optionnel) :**
```bash
cd duplicate-detection
.\dupenv\Scripts\Activate.ps1  # Windows PowerShell
python dup.py
```

### Option B : Utiliser les Scripts de Démarrage

```bash
# Sur Windows PowerShell
.\restart_project.ps1

# Sur Windows Batch
.\restart_project.bat
```

---

## 🔍 Vérification de l'Installation

### 1. Vérifier le Backend
- Ouvrez votre navigateur sur `http://localhost:6969/api/notifications/health`
- Vous devriez voir : `{"status":"ok","service":"notifications"}`

### 2. Vérifier le Frontend
- Ouvrez `http://localhost:5173`
- Vous devriez voir la page de connexion

### 3. Créer un compte test
1. Cliquez sur "S'inscrire"
2. Remplissez le formulaire
3. Connectez-vous avec vos identifiants

---

## 🛠️ Dépannage des Erreurs Courantes

### Erreur : "Connection refused" ou "Cannot connect to database"

**Cause** : PostgreSQL n'est pas en cours d'exécution ou le port est incorrect.

**Solution** :
1. Vérifiez que PostgreSQL est démarré :
   ```bash
   # Windows : Vérifiez dans "Services"
   # Mac/Linux :
   sudo systemctl status postgresql
   ```
2. Vérifiez le port dans `application.properties` ou `.env`
3. Testez la connexion manuellement :
   ```bash
   psql -U postgres -p 5433 -d issue_tracker_db
   ```

### Erreur : "Port 6969 already in use"

**Cause** : Un autre processus utilise déjà le port 6969.

**Solution** :
```bash
# Windows : Trouver le processus
netstat -ano | findstr :6969
# Tuez le processus (remplacez PID par le numéro trouvé)
taskkill /PID <PID> /F

# Mac/Linux :
lsof -i :6969
kill -9 <PID>
```

### Erreur : "Port 5173 already in use"

**Cause** : Un autre serveur de développement utilise le port 5173.

**Solution** :
- Arrêtez l'autre serveur de développement
- Ou changez le port dans `issue-tracker-web/vite.config.ts` :
  ```typescript
  export default defineConfig({
    server: {
      port: 5174, // Changez le port ici
    },
  })
  ```

### Erreur : "JWT secret key must be at least 256 bits"

**Cause** : La clé JWT dans `.env` est trop courte.

**Solution** :
1. Générez une nouvelle clé (voir section 2.2)
2. Mettez à jour `.env` avec la nouvelle clé
3. Redémarrez le backend

### Erreur : "User not found" lors de la connexion SSE

**Cause** : L'email utilisé pour SSE n'existe pas dans la base.

**Solution** :
- Assurez-vous d'être connecté avec un compte valide
- Ou créez un compte via la page d'inscription

### Erreur : "Failed to compile" ou erreurs TypeScript

**Cause** : Problème de dépendances npm.

**Solution** :
```bash
cd issue-tracker-web
# Supprimez node_modules et package-lock.json
rm -rf node_modules package-lock.json

# Réinstallez
npm install
```

### Erreur : "Duplicate service not available"

**Cause** : Le service Python n'est pas démarré.

**Solution** :
- Ce n'est pas bloquant, le projet fonctionne sans
- Si vous voulez la détection de doublons, suivez l'étape 4

---

## 📝 Notes Importantes

### Ports Utilisés
- **PostgreSQL** : 5433 (ou 5432 par défaut)
- **Backend Spring Boot** : 6969
- **Frontend React** : 5173
- **Service Python** : 5000

### Fichiers de Configuration Clés
- Backend : `.env` (racine) et `src/main/resources/application.properties`
- Frontend : `issue-tracker-web/.env`
- Python : `duplicate-detection/requirements.txt`

### Comptes par Défaut
Après l'installation, aucun compte n'est créé par défaut. Vous devez :
1. Vous inscrire via l'interface
2. Le premier compte inscrit peut s'auto-attribuer le rôle ADMIN

### Données de Test
Le projet inclut des scripts SQL optionnels pour peupler la base :
```bash
# Exécuter le script de données de test
psql -U postgres -p 5433 -d issue_tracker_db -f new_schema_with_data.sql
```

---

## 📞 Besoin d'Aide ?

Si vous rencontrez des problèmes non listés ici :

1. Vérifiez les logs du backend (terminal où tourne `mvn spring-boot:run`)
2. Vérifiez la console du navigateur (F12) pour le frontend
3. Consultez le README principal
4. Ouvrez une issue sur le repository GitHub

---

**Dernière mise à jour** : Avril 2026