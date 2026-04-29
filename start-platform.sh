#!/bin/bash
# Startup script for Air Algérie Issue Tracker Platform
# Démarre tous les services nécessaires

echo "🚀 Démarrage de la plateforme Air Algérie Issue Tracker"
echo "=================================================="

# Fonction pour vérifier si un port est disponible
check_port() {
    if lsof -Pi :$1 -sTCP:LISTEN -t >/dev/null ; then
        echo "❌ Port $1 déjà utilisé"
        return 1
    else
        echo "✅ Port $1 disponible"
        return 0
    fi
}

# Vérifier les ports nécessaires
echo "Vérification des ports..."
check_port 6969 || exit 1  # Backend Spring Boot
check_port 5173 || exit 1  # Frontend React
check_port 5001 || exit 1  # AI Service
check_port 5055 || exit 1  # Rasa Actions (default)

echo ""

# Démarrer le backend Spring Boot
echo "🔧 Démarrage du backend Spring Boot (port 6969)..."
cd "$(dirname "$0")"
if [ -f "mvnw" ]; then
    ./mvnw spring-boot:run &
    BACKEND_PID=$!
    echo "Backend PID: $BACKEND_PID"
else
    echo "❌ mvnw non trouvé. Assurez-vous d'être dans le répertoire du projet Spring Boot."
    exit 1
fi

# Attendre que le backend démarre
echo "⏳ Attente du démarrage du backend..."
sleep 30

# Démarrer les services AI
echo "🤖 Démarrage des services IA..."

# Service AI principal
cd ai-service
python run.py &
AI_SERVICE_PID=$!
echo "AI Service PID: $AI_SERVICE_PID"

# Attendre un peu
sleep 5

# Service Rasa Actions (dans un nouveau terminal/processus)
python -m rasa_sdk --actions actions_server --port 5055 &
RASA_ACTIONS_PID=$!
echo "Rasa Actions PID: $RASA_ACTIONS_PID"

cd ..

# Démarrer le frontend
echo "🌐 Démarrage du frontend React (port 5173)..."
cd issue-tracker-web
if [ -f "package.json" ]; then
    npm run dev &
    FRONTEND_PID=$!
    echo "Frontend PID: $FRONTEND_PID"
else
    echo "❌ package.json non trouvé. Assurez-vous d'être dans le répertoire du frontend."
fi

cd ..

echo ""
echo "🎉 Tous les services sont démarrés !"
echo ""
echo "Services actifs :"
echo "• Backend: http://localhost:6969"
echo "• Frontend: http://localhost:5173"
echo "• AI Service: http://localhost:5001"
echo "• Rasa Actions: http://localhost:5055"
echo ""
echo "Pour entraîner le modèle Rasa :"
echo "cd ai-service && python train.py"
echo ""
echo "Pour arrêter tous les services :"
echo "kill $BACKEND_PID $AI_SERVICE_PID $RASA_ACTIONS_PID $FRONTEND_PID"
echo ""
echo "Ou utilisez Ctrl+C pour arrêter ce script (arrêtera tous les services)"

# Fonction de nettoyage
cleanup() {
    echo ""
    echo "🛑 Arrêt de tous les services..."
    kill $BACKEND_PID $AI_SERVICE_PID $RASA_ACTIONS_PID $FRONTEND_PID 2>/dev/null
    echo "✅ Services arrêtés"
    exit 0
}

# Capturer Ctrl+C
trap cleanup SIGINT

# Attendre indéfiniment
wait