from flask import Flask, request, jsonify
from flask_cors import CORS
import requests
import json

app = Flask(__name__)
CORS(app)

BACKEND_URL = "http://localhost:6969/api"
FAQ_API = f"{BACKEND_URL}/faqs"
TICKET_API = f"{BACKEND_URL}/tickets"

INTENTS = {
    "creer_ticket": {
        "keywords": ["créer", "nouveau", "ticket", "demande", "signalé", "signaler", "soumettre", "incident", "problème", "besoin", "demander"],
        "responses": [
            "Je vais créer un ticket pour vous. Décrivez-moi votre problème en détail.",
            "Je peux créer un ticket pour vous. Veuillez me donner les détails."
        ]
    },
    "suivre_ticket": {
        "keywords": ["suivre", "statut", "état", "avancement", "ticket", "numéro", "résolution"],
        "responses": [
            "Pour suivre un ticket, consultez la page 'Mes Tickets' ou donnez-moi le numéro.",
            "Donnez-moi le numéro de ticket pour vérifier son état."
        ]
    },
    "faq_mot_passe": {
        "keywords": ["mot de passe", "mdp", "password", "oublié", "réinitialiser", "connexion"],
        "responses": ["Pour réinitialiser votre mot de passe: Paramètres > Sécurité > Changer le mot de passe."]
    },
    "faq_wifi": {
        "keywords": ["wifi", "internet", "réseau", "connexion"],
        "responses": ["Le Wi-Fi 'AirAlgerie-Employee' est disponible. Votre mot de passe est votre matricule."]
    },
    "faq_vpn": {
        "keywords": ["vpn", "accès distant", "remote", "télétravail"],
        "responses": ["Pour un accès VPN: créez un ticket catégorie 'Informatique' avec demande d'accès VPN."]
    },
    "faq_materiel": {
        "keywords": ["matériel", "ordinateur", "imprimante", "écran", "souris"],
        "responses": ["Pour demander du matériel: créez un ticket catégorie 'Matériel'."]
    },
    "faq_conge": {
        "keywords": ["congé", "vacances", "absence", "permissions"],
        "responses": ["Les demandes de congés: créez un ticket catégorie 'Administratif'."]
    },
    "saluer": {
        "keywords": ["bonjour", "salut", "hello", "hi", "coucou"],
        "responses": ["Bonjour ! Je suis prêt à vous aider."]
    },
    "remercier": {
        "keywords": ["merci", "remercie", "bravo", "super"],
        "responses": ["De rien ! N'hésitez pas pour d'autres questions."]
    },
    "autres": {
        "keywords": [],
        "responses": ["Je vais chercher dans la base de connaissances..."]
    }
}

CATEGORY_KEYWORDS = {
    "informatique": ["wifi", "ordinateur", "mot de passe", "email", "logiciel", "réseau", "VPN", "serveur", "pc", "bug", "incident", "application", "informatique", "IT"],
    "materiel": ["imprimante", "écran", "souris", "clavier", "chaise", "bureau", "téléphone", "matériel", "équipement"],
    "administratif": ["congé", "vacances", "salaire", "bulletin", "contrat", "document", "RH", "ressources humaines", "paie", "administratif"],
    "maintenance": ["climatisation", "éclairage", "portes", "serrure", "plomberie", "ascenseur", "réparation", "bâtiment", "maintenance"],
    "achat": ["fourniture", "achats", "commande", "budget", "achat"],
    "formation": ["formation", "cours", "apprentissage", "diplôme", "certification", "stage"],
}


def search_faq(query):
    """Cherche dans la FAQ du backend"""
    try:
        response = requests.get(f"{FAQ_API}/search?query={query}&page=0&size=5", timeout=5)
        if response.status_code == 200:
            data = response.json()
            if data.get('content') and len(data['content']) > 0:
                faq = data['content'][0]
                return {
                    'found': True,
                    'question': faq.get('question', ''),
                    'answer': faq.get('answer', '')
                }
    except Exception as e:
        print(f"FAQ search error: {e}")
    return {'found': False}


def create_ticket_from_chat(title, description, category, priority, user_email, token):
    """Crée un ticket via l'API backend"""
    try:
        headers = {'Content-Type': 'application/json'}
        if token:
            headers['Authorization'] = f'Bearer {token}'
        
        ticket_data = {
            "title": title,
            "description": description,
            "category": category,
            "priority": priority,
            "reporter": user_email
        }
        
        response = requests.post(TICKET_API, json=ticket_data, headers=headers, timeout=10)
        if response.status_code in [200, 201]:
            ticket = response.json()
            return {'success': True, 'ticketId': ticket.get('id'), 'message': 'Ticket créé avec succès!'}
        else:
            return {'success': False, 'message': f'Erreur: {response.status_code}'}
    except Exception as e:
        return {'success': False, 'message': str(e)}


def add_solution_to_faq(question, answer, category):
    """Ajoute une nouvelle solution à la FAQ"""
    try:
        faq_data = {
            "question": question,
            "answer": answer,
            "category": category,
            "active": True
        }
        response = requests.post(FAQ_API, json=faq_data, timeout=5)
        return response.status_code in [200, 201]
    except Exception as e:
        print(f"FAQ add error: {e}")
        return False


def detect_intent(text):
    if not text:
        return "autres"
    text_lower = text.lower()
    scores = {}
    for intent, data in INTENTS.items():
        if intent == "autres":
            continue
        score = sum(1 for kw in data["keywords"] if kw in text_lower)
        if score > 0:
            scores[intent] = score
    if scores:
        return max(scores.keys(), key=lambda k: scores[k])
    return "autres"


def get_response(intent):
    return INTENTS.get(intent, INTENTS["autres"])["responses"][0]


def classify_category(text):
    if not text:
        return "autres"
    text_lower = text.lower()
    scores = {}
    for category, keywords in CATEGORY_KEYWORDS.items():
        score = sum(1 for kw in keywords if kw in text_lower)
        if score > 0:
            scores[category] = score
    if scores:
        return max(scores.keys(), key=lambda k: scores[k])
    return "autres"


def determine_priority(text):
    urgent = ["urgent", "critique", "bloquant", "immédiat", "panne", "hors service", "grave"]
    return "High" if any(kw in text.lower() for kw in urgent) else "Medium"


@app.route('/chat', methods=['POST'])
def chat():
    data = request.json or {}
    message = data.get('message', '')
    user_email = data.get('userEmail', 'admin@airalgerie.dz')
    token = data.get('token', '')
    
    intent = detect_intent(message)
    
    # D'abord chercher dans la FAQ
    faq_result = search_faq(message)
    
    if faq_result.get('found'):
        response_text = f"Résultat trouvé dans la base de connaissances:\n\n{faq_result['answer']}\n\nCette réponse provient de la base de connaissances."
        return jsonify({
            'response': response_text,
            'intent': intent,
            'faqFound': True,
            'suggestedCategory': None,
            'suggestedPriority': None,
            'needsTicketCreation': False
        })
    
    # Pas de FAQ, vérifier si on doit créer un ticket
    needs_ticket = intent in ["creer_ticket", "autres"]
    category = classify_category(message) if needs_ticket else None
    priority = determine_priority(message)
    
    if needs_ticket:
        # Extraire le titre et description du message
        title = message[:100]  # Prendre les 100 premiers caractères comme titre
        description = message
        
        # Créer le ticket
        ticket_result = create_ticket_from_chat(title, description, category, priority, user_email, token)
        
        if ticket_result['success']:
            response_text = f" ticket créé! Numéro: #{ticket_result['ticketId']}\n\nJe vais suivre ce ticket et une fois résolu, j'enregistrerai la solution dans la FAQ pour aider les autres employés."
        else:
            response_text = f"Je n'ai pas pu créer le ticket. {ticket_result.get('message', '')}"
        
        return jsonify({
            'response': response_text,
            'intent': intent,
            'faqFound': False,
            'needsTicketCreation': True,
            'ticketCreated': ticket_result.get('success', False),
            'ticketId': ticket_result.get('ticketId'),
            'suggestedCategory': category,
            'suggestedPriority': priority
        })
    
    response_text = get_response(intent)
    return jsonify({
        'response': response_text,
        'intent': intent,
        'faqFound': False,
        'needsTicketCreation': False,
        'suggestedCategory': category,
        'suggestedPriority': priority
    })


@app.route('/ticket/<ticket_id>/track', methods=['GET'])
def track_ticket(ticket_id):
    """Suivre un ticket et proposer d'ajouter la solution à la FAQ"""
    try:
        headers = {'Content-Type': 'application/json'}
        response = requests.get(f"{TICKET_API}/{ticket_id}", headers=headers, timeout=5)
        
        if response.status_code == 200:
            ticket = response.json()
            status = ticket.get('status', 'Unknown')
            
            response_text = f"Ticket #{ticket_id} - Statut: {status}\n"
            
            if status in ['Done', 'Closed']:
                response_text += "\nJe peux enregistrer la solution dans la FAQ pour ce ticket."
                response_text += " La solution sera ajoutée à la base de connaissances."
                return jsonify({
                    'response': response_text,
                    'ticketStatus': status,
                    'resolved': True,
                    'canAddToFaq': True
                })
            else:
                response_text += "\nLe ticket est toujours en cours de traitement."
                return jsonify({
                    'response': response_text,
                    'ticketStatus': status,
                    'resolved': False,
                    'canAddToFaq': False
                })
        else:
            return jsonify({'error': 'Ticket non trouvé'}), 404
    except Exception as e:
        return jsonify({'error': str(e)}), 500


@app.route('/faq/add', methods=['POST'])
def add_faq():
    """Ajouter une solution à la FAQ"""
    data = request.json or {}
    question = data.get('question', '')
    answer = data.get('answer', '')
    category = data.get('category', 'autres')
    
    if add_solution_to_faq(question, answer, category):
        return jsonify({
            'success': True,
            'message': 'Solution ajoutée à la FAQ!'
        })
    return jsonify({'success': False, 'message': 'Erreur lors de l\'ajout'}), 500


@app.route('/classify', methods=['POST'])
def classify():
    data = request.json or {}
    text = data.get('text', '')
    title = data.get('title', '')
    full_text = f"{title} {text}"
    
    category = classify_category(full_text)
    priority = determine_priority(full_text)
    
    dept_map = {
        "informatique": "IT",
        "materiel": "Achats",
        "administratif": "RH",
        "maintenance": "Maintenance",
        "achat": "Achats",
        "formation": "RH",
        "autres": "Support"
    }
    
    return jsonify({
        'category': category,
        'suggestedDepartment': dept_map.get(category, "Support"),
        'suggestedPriority': priority,
        'keywords': [],
        'confidence': 0.85
    })


@app.route('/health', methods=['GET'])
def health():
    return jsonify({'status': 'ok'})


if __name__ == "__main__":
    app.run(host='0.0.0.0', port=5001)