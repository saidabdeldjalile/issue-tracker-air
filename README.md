# IssueTracker - Système de Gestion de Tickets

Un système complet de gestion de tickets développé avec **Spring Boot** (backend) et **React/TypeScript** (frontend), incluant la détection de doublons par IA.

## 📋 Table des Matières

- [Fonctionnalités](#-fonctionnalités)
- [Architecture](#-architecture)
- [Prérequis](#-prérequis)
- [Installation](#-installation)
- [Démarrage](#-démarrage)
- [API Endpoints](#-api-endpoints)
- [Variables d'Environnement](#-variables-denvironnement)
- [Structure du Projet](#-structure-du-projet)
- [Développement](#-développement)
- [Dépannage](#-dépannage)
- [Contribuer](#-contribuer)
- [Licence](#-licence)

---

## ✨ Fonctionnalités

### Gestion des Tickets
- ✅ Création, modification, suppression de tickets
- ✅ Attribution de tickets aux utilisateurs
- ✅ Statuts personnalisables (Open, ToDo, InProgress, Done, Closed, Deleted)
- ✅ Priorités (Critical, High, Medium, Low)
- ✅ Catégories et types de tickets
- ✅ Recherche full-text dans les titres et descriptions
- ✅ Pagination des résultats

### Gestion des Utilisateurs
- ✅ Authentification JWT sécurisée
- ✅ Rôles (ADMIN, SUPPORT, USER)
- ✅ Gestion des départements/services
- ✅ Inscription et connexion
- ✅ Profils utilisateurs

### Fonctionnalités Avancées
- ✅ **Upload de captures d'écran** (10MB max, tous formats image)
- ✅ **Notifications en temps réel** via Server-Sent Events (SSE)
- ✅ **Détection de doublons** par IA (service Python avec Sentence Transformers)
- ✅ **Chatbot intelligent** avec Rasa NLU (reconnaissance d'intentions en français)
- ✅ **Système de feedback utilisateur** pour améliorer le chatbot
- ✅ **Base documentaire enrichie** (FAQ + procédures spécifiques Air Algérie)
- ✅ **Classification automatique** des demandes avec IA
- ✅ **Tableau de bord** avec statistiques détaillées
- ✅ **Recherche avancée** avec filtres
- ✅ **Système de commentaires** sur les tickets
- ✅ **Thèmes personnalisables** (clair/sombre)
- ✅ **Interface responsive** (mobile, tablette, desktop)

### Organisation
- ✅ Gestion des projets
- ✅ Gestion des départements/services
- ✅ Hiérarchie Projet → Tickets
- ✅ Association utilisateurs ↔ départements

---

## 🏗️ Architecture

### Backend
- **Framework** : Spring Boot 3.1.5
- **Java** : 17
- **Base de données** : PostgreSQL
- **Sécurité** : Spring Security + JWT
- **ORM** : Spring Data JPA / Hibernate
- **Build** : Maven

### Frontend
- **Framework** : React 18
- **Langage** : TypeScript
- **Build** : Vite
- **Routing** : React Router v6
- **UI** : Tailwind CSS + DaisyUI
- **HTTP** : Axios + SWR
- **Notifications** : React Toastify

### Service de Détection de Doublons
- **Langage** : Python
- **Framework** : Flask
- **IA** : Sentence Transformers (all-MiniLM-L6-v2)
- **Traitement** : NLTK + BeautifulSoup

### Service de Chatbot Intelligent (Rasa)
- **Framework** : Rasa Open Source 3.6+
- **NLU** : Reconnaissance d'intentions en français
- **Actions** : Rasa SDK (Python)
- **Modèles** : DIET Classifier + Response Selector
- **Sessions** : Gestion conversationnelle persistante
- **Feedback** : Système d'évaluation utilisateur

---

## 📦 Prérequis

### Obligatoires
- **Java 17** ou supérieur
- **Node.js 18+** et npm
- **PostgreSQL 12+**
- **Python 3.8+** (pour les services IA : détection de doublons + chatbot)

### Outils Recommandés
- **Maven 3.6+**
- **IDE** : IntelliJ IDEA, VS Code, Eclipse
- **Git**

---

## 🚀 Installation

### 1. Cloner le dépôt
```bash
git clone https://github.com/saidabdeldjalile/issue-tracker-air.git
cd IssueTracker-main
```

### 2. Configuration de la Base de Données

#### Créer la base de données PostgreSQL
```sql
CREATE DATABASE issue_tracker_db;
```

#### Modifier les identifiants dans `src/main/resources/application.properties`
```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/issue_tracker_db
spring.datasource.username=postgres
spring.datasource.password=postgres
```

#### Exécuter les scripts SQL (optionnel - les tables sont créées automatiquement par Hibernate)
```bash
# Si vous voulez initialiser avec des données de test
psql -h localhost -p 5433 -U postgres -d issue_tracker_db -f new_schema_with_data.sql
```

### 3. Installation du Backend

```bash
# Nettoyer et compiler le projet
mvn clean install

# Ou simplement télécharger les dépendances
mvn dependency:resolve
```

### 4. Installation du Frontend

```bash
cd issue-tracker-web

# Installer les dépendances
npm install

# Ou avec pnpm (recommandé pour plus de rapidité)
pnpm install
```

### 5. Installation du Service de Chatbot Intelligent (Rasa)

```bash
cd ai-service

# Créer un environnement virtuel Python
python -m venv rasa_env

# Activer l'environnement virtuel
# Sur Windows PowerShell :
.\rasa_env\Scripts\Activate.ps1
# Sur Windows CMD :
.\rasa_env\Scripts\activate.bat
# Sur Linux/Mac :
source rasa_env/bin/activate

# Installer les dépendances Python
pip install -r requirements.txt

# Télécharger le modèle spaCy français
python -m spacy download fr_core_news_sm

# Entraîner le modèle Rasa (nécessaire avant le premier lancement)
python train.py
```

### 6. Installation du Service de Détection de Doublons (Optionnel)

```bash
cd duplicate-detection

# Créer un environnement virtuel Python
python -m venv dupenv

# Activer l'environnement virtuel
# Sur Windows PowerShell :
.\dupenv\Scripts\Activate.ps1
# Sur Windows CMD :
.\dupenv\Scripts\activate.bat
# Sur Linux/Mac :
source dupenv/bin/activate

# Installer les dépendances Python
pip install -r requirements.txt

# Télécharger les données NLTK
python download_nltk.py
```

---

## ▶️ Démarrage

### Option 1 : Démarrage Manuel

#### 1. Démarrer PostgreSQL
```bash
# Assurez-vous que PostgreSQL est en cours d'exécution sur le port 5433
# Ou modifiez le port dans application.properties
```

#### 2. Démarrer le Backend (Spring Boot)
```bash
# Depuis la racine du projet
mvn spring-boot:run

# Ou utiliser le script PowerShell
.\restart_backend.bat
```

Le backend sera disponible sur : **http://localhost:6969**

#### 3. Démarrer le Frontend (React)
```bash
cd issue-tracker-web
npm run dev

# Ou avec le script batch
.\run-dev.bat
```

Le frontend sera disponible sur : **http://localhost:5173**

#### 4. Démarrer le Service de Chatbot Intelligent (Rasa)
```bash
cd ai-service
.\rasa_env\Scripts\Activate.ps1  # Activer l'environnement virtuel

# Démarrer le service principal
python run.py

# Ou séparément :
# python app.py                    # Service Flask principal
# python -m rasa_sdk --actions actions_server  # Actions Rasa
```

Le service sera disponible sur : **http://localhost:5001**

#### 5. Démarrer le Service de Détection de Doublons (Optionnel)
```bash
cd duplicate-detection
.\dupenv\Scripts\Activate.ps1  # Activer l'environnement virtuel
python dup.py

# Ou avec waitress (production-like)
waitress-serve --listen=*:5000 dup:app
```

Le service sera disponible sur : **http://localhost:5000**

### Option 2 : Démarrage avec Docker (Recommandé)

#### Démarrer tous les services
```bash
docker-compose up --build
```

Les services seront disponibles sur :
- **Frontend** : http://localhost:80
- **Backend** : http://localhost:6969
- **AI Service** : http://localhost:5001
- **Base de données** : localhost:5433

#### Arrêter les services Docker
```bash
docker-compose down
```

### Option 3 : Scripts de Démarrage Automatique

#### Script unifié (tous les services)
```bash
chmod +x start-platform.sh
./start-platform.sh
```

#### Sur Windows PowerShell
```powershell
.\restart_project.ps1
```

#### Sur Windows Batch
```batch
.\restart_project.bat
```

Ces scripts démarrent automatiquement :
1. Le backend Spring Boot
2. Le frontend React
3. Le service de chatbot Rasa
4. (Optionnel) Le service de détection de doublons

---

## 🔌 API Endpoints

### Authentification
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Inscription d'un nouvel utilisateur |
| POST | `/api/auth/login` | Connexion (retourne JWT) |

### Tickets
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/tickets` | Récupérer tous les tickets (paginé) |
| GET | `/api/tickets/{id}` | Récupérer un ticket par ID |
| GET | `/api/tickets/my-tickets` | Récupérer mes tickets |
| GET | `/api/tickets/tickets-for-user` | Récupérer les tickets selon le rôle |
| POST | `/api/tickets` | Créer un nouveau ticket |
| PUT | `/api/tickets/{id}` | Mettre à jour un ticket |
| PATCH | `/api/tickets/{id}` | Mise à jour partielle (statut, priorité) |
| DELETE | `/api/tickets/{id}` | Supprimer un ticket |
| POST | `/api/tickets/{id}/assign` | Assigner un ticket à un utilisateur |

### Commentaires
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/tickets/{ticketId}/comments` | Récupérer les commentaires d'un ticket |
| POST | `/api/tickets/{ticketId}/comments` | Ajouter un commentaire |
| PUT | `/api/comments/{id}` | Modifier un commentaire |
| DELETE | `/api/comments/{id}` | Supprimer un commentaire |

### Captures d'Écran (Screenshots)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/tickets/{ticketId}/screenshots` | Récupérer les screenshots d'un ticket |
| POST | `/api/tickets/{ticketId}/screenshots` | Upload un screenshot (max 10MB) |
| DELETE | `/api/tickets/{ticketId}/screenshots/{screenshotId}` | Supprimer un screenshot |

### Projets
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/projects` | Récupérer tous les projets |
| GET | `/api/projects/{id}` | Récupérer un projet par ID |
| GET | `/api/projects/by-department/{departmentId}` | Projets d'un département |
| POST | `/api/projects` | Créer un projet |
| PUT | `/api/projects/{id}` | Mettre à jour un projet |
| DELETE | `/api/projects/{id}` | Supprimer un projet |

### Départements/Services
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/departments` | Récupérer tous les départements |
| GET | `/api/departments/{id}` | Récupérer un département |
| POST | `/api/departments` | Créer un département |
| PUT | `/api/departments/{id}` | Mettre à jour un département |
| DELETE | `/api/departments/{id}` | Supprimer un département |

### Utilisateurs
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users` | Récupérer tous les utilisateurs |
| GET | `/api/users/{id}` | Récupérer un utilisateur par ID |
| GET | `/api/users/by-email/{email}` | Récupérer un utilisateur par email |
| POST | `/api/users` | Créer un utilisateur |
| PUT | `/api/users/{id}` | Mettre à jour un utilisateur |
| DELETE | `/api/users/{id}` | Supprimer un utilisateur |
| PUT | `/api/users/profile` | Mettre à jour son propre profil |

### Notifications
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/notifications/stream` | Stream SSE des notifications en temps réel |
| GET | `/api/notifications` | Récupérer les notifications d'un utilisateur |
| GET | `/api/notifications/unread-count` | Nombre de notifications non lues |
| POST | `/api/notifications/{id}/read` | Marquer une notification comme lue |
| POST | `/api/notifications/read-all` | Tout marquer comme lu |

### Chatbot Intelligent
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/chat` | Traitement des messages chatbot |
| POST | `/api/v1/chat/feedback` | Soumission de feedback utilisateur |
| GET | `/api/v1/chat/feedback/session/{sessionId}` | Feedback d'une session |
| GET | `/api/v1/chat/feedback/user/{userEmail}` | Feedback d'un utilisateur |
| GET | `/api/v1/chat/feedback/stats` | Statistiques de feedback |

### Tableau de Bord
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/dashboard/stats` | Statistiques générales |
| GET | `/api/dashboard/user-stats` | Statistiques par utilisateur |
| GET | `/api/dashboard/department-stats` | Statistiques par département |

---

## 🔐 Variables d'Environnement

### Backend (`application.properties`)
```properties
# Serveur
server.port=6969

# Base de données
POSTGRES_URL=jdbc:postgresql://localhost:5433/issue_tracker_db
DATABASE_USER=postgres
DATABASE_PASSWORD=postgres

# JWT
JWT_SECRET_KEY=votre_secret_jwt_tres_long_et_sec

# Service de détection de doublons
DUPLICATE_SERVICE_URL=http://localhost:5000

# Service de chatbot Rasa
CHATBOT_SERVICE_URL=http://localhost:5001
CLASSIFICATION_SERVICE_URL=http://localhost:5001

# Upload de fichiers
app.upload.path=uploads/screenshots
```

### Frontend (`.env` - optionnel)
```env
VITE_API_URL=http://localhost:6969
```

---

## 📁 Structure du Projet

```
IssueTracker-main/
├── src/                          # Backend Java/Spring Boot
├── issue-tracker-web/           # Frontend React/TypeScript (Vite)
├── ai-service/                  # Service Rasa (Chatbot NLU + Actions)
├── chatbot-service/             # Service complémentaire pour le Chatbot
├── classification-service/      # Service IA de classification des tickets
├── duplicate-detection/         # Service Python de détection de doublons
├── uploads/                     # Dossier d'upload des screenshots
├── pom.xml                      # Configuration Maven
├── docker-compose.yml           # Configuration Docker
├── start-platform.sh            # Script de démarrage unifié
└── README.md                    # Ce fichier
```

---

## 🛠️ Développement

### Backend

#### Lancer les tests
```bash
mvn test
```

#### Build pour production
```bash
mvn clean package
java -jar target/IssueTracker-0.0.1-SNAPSHOT.jar
```

#### Activer le mode debug
```properties
# Dans application.properties
debug=true
logging.level.com.suryakn.IssueTracker=DEBUG
```

### Frontend

#### Lancer en mode développement
```bash
cd issue-tracker-web
npm run dev
```

#### Build pour production
```bash
npm run build
```

#### Vérifier le code (linting)
```bash
npm run lint
```

#### Formater le code
```bash
npm run format
```

---

## 🔧 Dépannage

### Problèmes Courants

#### 1. Erreur de connexion à PostgreSQL
```
Solution : Vérifiez que PostgreSQL est en cours d'exécution et que les identifiants sont corrects.
```

#### 2. Port déjà utilisé (6969 ou 5173)
```
Solution : Changez le port dans application.properties ou arrêtez le processus utilisant le port.
```

#### 3. Erreur CORS
```
Solution : Vérifiez que le backend autorise les origines du frontend dans WebConfig.java
```

#### 4. Screenshots ne s'affichent pas
```
Solution : Vérifiez que le dossier uploads/screenshots existe et a les permissions d'écriture.
```

#### 5. Service Python ne démarre pas
```
Solution : Activez l'environnement virtuel et installez les dépendances.
```

### Logs

#### Backend
Les logs sont affichés dans la console. Pour plus de détails :
```properties
logging.level.org.springframework=DEBUG
logging.level.com.suryakn.IssueTracker=DEBUG
```

#### Frontend
Ouvrez les DevTools du navigateur (F12) et consultez l'onglet Console.

---

## 🤝 Contribuer

1. Fork le projet
2. Créez une branche pour votre fonctionnalité (`git checkout -b feature/AmazingFeature`)
3. Committez vos changements (`git commit -m 'Add some AmazingFeature'`)
4. Push vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrez une Pull Request

### Standards de Code

- **Backend** : Suivez les conventions Java/Spring Boot
- **Frontend** : Utilisez ESLint et Prettier (configurés dans le projet)
- **Commits** : Messages clairs et descriptifs en français ou anglais

---

## 📝 Licence

Ce projet est sous licence MIT. Voir le fichier `LICENSE` pour plus de détails.

---

## 📞 Support

Pour toute question ou problème :
- Ouvrez une issue sur GitHub
- Contactez l'équipe de développement

---

## 🙏 Remerciements

- **Spring Boot** pour le framework backend
- **React** pour la bibliothèque frontend
- **Sentence Transformers** pour la détection de doublons
- **Tailwind CSS** et **DaisyUI** pour le design

---

**Développé avec ❤️ par l'équipe IssueTracker**