# Guide de Déploiement en Production

Ce guide explique comment déployer la plateforme Air Algérie Issue Tracker en environnement de production.

## Prérequis

- Serveur Linux/Ubuntu 20.04+
- Docker & Docker Compose
- Nginx (reverse proxy)
- Certificat SSL (Let's Encrypt recommandé)
- Au moins 4GB RAM, 2 CPU cores
- 20GB espace disque

## Architecture de Production

```
Internet → Nginx (SSL/TLS) → Services Docker
                              ├── Frontend (React)
                              ├── Backend (Spring Boot)
                              ├── AI Service (Flask + Rasa)
                              ├── PostgreSQL
                              └── Redis (optionnel)
```

## 1. Préparation du Serveur

### Installation des dépendances système

```bash
# Ubuntu/Debian
sudo apt update
sudo apt install -y docker.io docker-compose nginx certbot python3-certbot-nginx

# Démarrer Docker
sudo systemctl start docker
sudo systemctl enable docker

# Ajouter l'utilisateur au groupe docker
sudo usermod -aG docker $USER
```

### Configuration du Firewall

```bash
# Ouvrir les ports nécessaires
sudo ufw allow 80
sudo ufw allow 443
sudo ufw allow 22
sudo ufw --force enable
```

## 2. Configuration SSL/TLS

```bash
# Obtenir un certificat Let's Encrypt
sudo certbot --nginx -d votre-domaine.com

# Configuration automatique Nginx
sudo certbot --nginx
```

## 3. Configuration de l'Application

### Cloner le repository

```bash
cd /opt
git clone <repository-url> issue-tracker
cd issue-tracker
```

### Configuration des variables d'environnement

```bash
cp .env.example .env
nano .env
```

Ajuster les valeurs dans `.env` :
```env
# Production settings
NODE_ENV=production
FLASK_ENV=production
SPRING_PROFILES_ACTIVE=production

# Database (utiliser une base dédiée)
POSTGRES_HOST=db.votre-domaine.com
POSTGRES_PASSWORD=<mot-de-passe-fort>

# JWT (générer une nouvelle clé)
JWT_SECRET_KEY=<clé-sécurisée-256-bits>

# Domain
CORS_ALLOWED_ORIGINS=https://votre-domaine.com
```

### Configuration Nginx

Créer `/etc/nginx/sites-available/issue-tracker` :

```nginx
server {
    listen 80;
    server_name votre-domaine.com;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name votre-domaine.com;

    # SSL Configuration
    ssl_certificate /etc/letsencrypt/live/votre-domaine.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/votre-domaine.com/privkey.pem;

    # Security headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header Referrer-Policy "no-referrer-when-downgrade" always;
    add_header Content-Security-Policy "default-src 'self' http: https: data: blob: 'unsafe-inline'" always;

    # Gzip compression
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_types text/plain text/css text/xml text/javascript application/javascript application/xml+rss application/json;

    # Frontend
    location / {
        proxy_pass http://localhost:80;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_cache_bypass $http_upgrade;
    }

    # Backend API
    location /api/ {
        proxy_pass http://localhost:6969;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_cache_bypass $http_upgrade;
    }

    # Rate limiting
    limit_req_zone $binary_remote_addr zone=api:10m rate=10r/s;
    limit_req zone=api burst=20 nodelay;
}
```

Activer le site :
```bash
sudo ln -s /etc/nginx/sites-available/issue-tracker /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

## 4. Déploiement avec Docker

### Build et déploiement

```bash
# Build des images
docker-compose build --no-cache

# Démarrage en arrière-plan
docker-compose up -d

# Vérification
docker-compose ps
docker-compose logs -f
```

### Migration de la base de données

```bash
# Accéder au conteneur backend
docker-compose exec backend bash

# Exécuter les migrations si nécessaire
# Les tables sont créées automatiquement par Hibernate
```

## 5. Configuration des Services IA

### Entraînement du modèle Rasa

```bash
# Accéder au conteneur AI
docker-compose exec ai-chatbot bash

# Entraîner le modèle
python train.py

# Le modèle sera sauvegardé dans le volume models/
```

### Optimisation des performances

```bash
# Configuration production pour Rasa
export RASA_ENVIRONMENT=production
export RASA_MODEL_SERVER_ENDPOINTS='{"sentiment": {"url": "http://localhost:5001"}}'
```

## 6. Monitoring et Maintenance

### Logs

```bash
# Logs des services
docker-compose logs -f backend
docker-compose logs -f ai-chatbot
docker-compose logs -f frontend

# Logs Nginx
sudo tail -f /var/log/nginx/access.log
sudo tail -f /var/log/nginx/error.log
```

### Sauvegarde

Créer un script de sauvegarde `/opt/issue-tracker/backup.sh` :

```bash
#!/bin/bash
BACKUP_DIR="/opt/backups"
DATE=$(date +%Y%m%d_%H%M%S)

# Sauvegarde base de données
docker-compose exec -T postgres pg_dump -U postgres issue_tracker_db > $BACKUP_DIR/db_$DATE.sql

# Sauvegarde volumes
tar -czf $BACKUP_DIR/uploads_$DATE.tar.gz uploads/

# Nettoyage ancien backups (garder 7 jours)
find $BACKUP_DIR -name "*.sql" -mtime +7 -delete
find $BACKUP_DIR -name "*.tar.gz" -mtime +7 -delete
```

### Mise à jour

```bash
# Arrêt des services
docker-compose down

# Mise à jour du code
git pull origin main

# Reconstruction
docker-compose build --no-cache

# Redémarrage
docker-compose up -d

# Migration si nécessaire
docker-compose exec backend ./migrate.sh
```

## 7. Sécurité

### Configuration de sécurité

```bash
# Désactiver l'exposition des ports non nécessaires
# Modifier docker-compose.yml pour ne garder que le port 80 exposé

# Configuration fail2ban pour Nginx
sudo apt install fail2ban
sudo cp /etc/fail2ban/jail.conf /etc/fail2ban/jail.local
sudo systemctl enable fail2ban
sudo systemctl start fail2ban
```

### Audit de sécurité

```bash
# Scanner les vulnérabilités Docker
docker scan issue-tracker-backend:latest

# Vérification des dépendances
# Backend: mvn dependency-check:aggregate
# Frontend: npm audit
# Python: safety check
```

## 8. Performance et Optimisation

### Configuration JVM (Backend)

```bash
# Dans Dockerfile.backend
ENV JAVA_OPTS="-Xmx2g -Xms512m -XX:+UseG1GC -XX:+UseContainerSupport"
```

### Cache et CDN

```bash
# Configuration Redis pour le cache
# Utiliser Cloudflare ou AWS CloudFront pour les assets statiques
```

### Monitoring

```bash
# Installation Prometheus + Grafana
docker run -d -p 9090:9090 prom/prometheus
docker run -d -p 3000:3000 grafana/grafana
```

## 9. Résolution des Problèmes Courants

### Service AI ne démarre pas
```bash
# Vérifier les logs
docker-compose logs ai-chatbot

# Vérifier la connectivité
docker-compose exec ai-chatbot curl -f http://rasa-actions:5055/health
```

### Erreur CORS
```bash
# Vérifier la configuration CORS dans application.properties
# S'assurer que les origines correspondent au domaine
```

### Performance lente
```bash
# Vérifier l'utilisation des ressources
docker stats

# Optimiser la base de données
docker-compose exec postgres psql -U postgres -d issue_tracker_db -c "VACUUM ANALYZE;"
```

## Support

Pour toute question ou problème en production :
1. Consulter les logs des conteneurs
2. Vérifier l'état des services avec `docker-compose ps`
3. Tester les endpoints API avec curl
4. Ouvrir un ticket dans le système lui-même ! 🤖