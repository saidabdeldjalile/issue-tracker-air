# Air Algérie Intelligent Chatbot Service

Ce service fournit un chatbot intelligent pour la plateforme de gestion des demandes d'Air Algérie, utilisant Rasa pour la reconnaissance d'intentions conversationnelles.

## Fonctionnalités

- 🤖 Reconnaissance d'intentions en français avec Rasa NLU
- 📝 Création automatique de tickets
- 🔍 Recherche dans la base de connaissances FAQ
- 🎯 Classification automatique des demandes
- 💬 Gestion des sessions conversationnelles
- ⭐ Système de feedback utilisateur
- 🔄 Intégration avec Spring Boot backend

## Installation

### Prérequis

- Python 3.8+
- pip
- Virtual environment (recommandé)

### Installation des dépendances

```bash
cd ai-service
pip install -r requirements.txt
python -m spacy download fr_core_news_sm
```

### Configuration Rasa

1. **Entraîner le modèle Rasa :**
```bash
python train.py
```

2. **Démarrer le service Rasa actions :**
```bash
python -m rasa_sdk --actions actions_server
```

3. **Démarrer le service principal :**
```bash
python run.py
```

Le service sera disponible sur `http://localhost:5001`

## Architecture

### Fichiers de configuration Rasa

- `config.yml` : Configuration du pipeline Rasa
- `domain.yml` : Définition des intents, entités, slots et réponses
- `data/nlu.yml` : Données d'entraînement NLU
- `data/stories.yml` : Scénarios de conversation
- `data/rules.yml` : Règles conversationnelles

### Services

- **ai-service/app.py** : Service principal Flask avec intégration Rasa
- **ai-service/actions_server.py** : Actions Rasa personnalisées
- **ai-service/train.py** : Script d'entraînement du modèle

### Intégration Backend

Le chatbot s'intègre avec le backend Spring Boot via :

- `/api/v1/chat` : Traitement des messages
- `/api/v1/chat/feedback` : Soumission de feedback
- `/api/v1/chat/search` : Recherche dans la FAQ

## Utilisation

### Endpoints API

#### POST /chat
Traite un message utilisateur et retourne une réponse.

**Request :**
```json
{
  "message": "J'ai un problème avec mon ordinateur",
  "userEmail": "user@airalgerie.dz",
  "sessionId": "optional-session-id"
}
```

**Response :**
```json
{
  "response": "Je vais créer un ticket pour vous...",
  "intent": "create_ticket",
  "category": "informatique",
  "sentiment": {...},
  "entities": {...}
}
```

#### POST /chat/feedback
Soumet un feedback utilisateur.

```json
{
  "sessionId": "session-id",
  "userEmail": "user@airalgerie.dz",
  "message": "User message",
  "response": "Bot response",
  "rating": 5,
  "helpful": true
}
```

#### POST /train
Déclenche l'entraînement du modèle Rasa (asynchrone).

## Développement

### Ajouter de nouveaux intents

1. Ajouter l'intent dans `data/nlu.yml`
2. Définir les réponses dans `domain.yml`
3. Créer des stories dans `data/stories.yml`
4. Implémenter les actions si nécessaire dans `actions_server.py`

### Personnalisation

- **Classification :** Modifier `CATEGORY_KEYWORDS` dans `actions_server.py`
- **Réponses :** Éditer les réponses dans `domain.yml`
- **Pipeline :** Ajuster `config.yml` pour la performance

## Métriques et Monitoring

### Statistiques de feedback

GET `/api/v1/chat/feedback/stats`

Retourne :
- Note moyenne sur 30 jours
- Nombre de feedbacks "utiles"

### Santé du service

GET `/health`

Vérifie l'état du service et des composants (Rasa, embeddings, etc.)

## Déploiement

### Production

1. Entraîner le modèle en production
2. Configurer les variables d'environnement
3. Utiliser un serveur WSGI (gunicorn)
4. Configurer la persistance des sessions

### Variables d'environnement

```bash
export FLASK_ENV=production
export BACKEND_URL=http://your-backend-url
```

## Support

Pour les problèmes ou améliorations :
- Vérifier les logs du service
- Tester avec `rasa shell` pour le débogage
- Consulter la documentation Rasa officielle

## Anciens Services (Legacy)

### Classification Service
- Port: 5001 (legacy)
- Utilise la classification par mots-clés

### Duplicate Detection Service
- Port: 5000
- Détection de tickets similaires
