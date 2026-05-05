import nltk
import json
import re
import torch
import numpy as np
from bs4 import BeautifulSoup
from flask import Flask, request, jsonify
from flask_cors import CORS
from sentence_transformers import SentenceTransformer
from sentence_transformers import util
import random

app = Flask(__name__)
CORS(app)
model = SentenceTransformer('all-MiniLM-L6-v2')

nltk.download('stopwords')

from nltk.corpus import stopwords

stop_words = set(stopwords.words('english'))


def parse_vector(vector_str):
    """
    Safely parse a vector string representation without using eval().
    Handles various formats: JSON arrays, string representations of lists.
    Returns a numpy array or None if parsing fails.
    """
    if vector_str is None:
        return None

    # If it's already a list, convert to numpy array
    if isinstance(vector_str, list):
        return np.array(vector_str, dtype=np.float32)

    # If it's a string, try to parse it
    if isinstance(vector_str, str):
        # Try JSON parsing first (safest)
        try:
            parsed = json.loads(vector_str)
            if isinstance(parsed, list):
                return np.array(parsed, dtype=np.float32)
        except (json.JSONDecodeError, ValueError):
            pass

        # Try to parse string representation of list like "[0.1, 0.2, ...]"
        # Remove whitespace and brackets
        cleaned = vector_str.strip()
        if cleaned.startswith('[') and cleaned.endswith(']'):
            # Extract numbers using regex (handles floats, negatives, scientific notation)
            numbers = re.findall(r'[-+]?\d*\.?\d+(?:[eE][-+]?\d+)?', cleaned)
            if numbers:
                return np.array([float(n) for n in numbers], dtype=np.float32)

        # Try ast.literal_eval as last resort (safe for literals only)
        try:
            import ast
            parsed = ast.literal_eval(vector_str)
            if isinstance(parsed, (list, tuple)):
                return np.array(parsed, dtype=np.float32)
        except (ValueError, SyntaxError):
            pass

    return None


def validate_vector(vector, expected_dim=None):
    """
    Validate that a vector is a valid numeric array.
    Returns True if valid, False otherwise.
    """
    if vector is None:
        return False
    if not isinstance(vector, np.ndarray):
        return False
    if vector.size == 0:
        return False
    if not np.issubdtype(vector.dtype, np.number):
        return False
    if np.any(np.isnan(vector)) or np.any(np.isinf(vector)):
        return False
    if expected_dim is not None and vector.shape[0] != expected_dim:
        return False
    return True


def remove_stop_words(text):
    words = text.split()
    filtered_words = [word for word in words if word.lower() not in stop_words]
    return ' '.join(filtered_words)


@app.route('/process_ticket', methods=['POST'])
def process_ticket():
    try:
        data = request.json
        if not data:
            return jsonify({'error': 'No data provided', 'similar_ticket_ids': [], 'vector': None}), 400

        new_ticket_text = data.get('text')
        embedded_tickets = data.get('ticketEmbeddingDTOS', [])

        if not new_ticket_text:
            return jsonify({'error': 'No ticket text provided', 'similar_ticket_ids': [], 'vector': None}), 400

        # Get the embedding for the new ticket
        try:
            cleaned_text = remove_stop_words(BeautifulSoup(new_ticket_text, 'lxml').get_text())
            new_ticket_embedding = model.encode(cleaned_text)
        except Exception as e:
            return jsonify({'error': f'Failed to encode ticket: {str(e)}', 'similar_ticket_ids': [], 'vector': None}), 500

        # Validate the new ticket embedding
        if not validate_vector(new_ticket_embedding):
            return jsonify({'error': 'Invalid embedding generated', 'similar_ticket_ids': [], 'vector': None}), 500

        # If no existing tickets, return empty results with the new vector
        if len(embedded_tickets) == 0:
            return jsonify({
                'similar_ticket_ids': [],
                'vector': json.dumps(new_ticket_embedding.tolist())  # Proper JSON serialization
            })

        # Process existing tickets with safe vector parsing
        similarities_with_ids = []
        for ticket in embedded_tickets:
            ticket_id = ticket.get('ticketId')
            vector_str = ticket.get('vector')

            # Safely parse the vector without using eval()
            parsed_vector = parse_vector(vector_str)

            # Skip invalid vectors
            if not validate_vector(parsed_vector):
                print(f"Warning: Skipping ticket {ticket_id} due to invalid vector")
                continue

            try:
                similarity = util.cos_sim(
                    torch.tensor(new_ticket_embedding),
                    torch.tensor(parsed_vector)
                )
                similarities_with_ids.append((ticket_id, similarity))
            except Exception as e:
                print(f"Warning: Failed to compute similarity for ticket {ticket_id}: {str(e)}")
                continue

        print(f"Computed similarities for {len(similarities_with_ids)} tickets")

        # Filter out the tickets with similarity greater than threshold
        similar_tickets = [(ticketId, sim) for ticketId, sim in similarities_with_ids if sim > 0.76]

        # Select top 10 similar tickets
        top_similar_tickets = sorted(similar_tickets, key=lambda x: x[1], reverse=True)[:10]

        # Extract the ticket IDs from the top similar tickets
        top_ticket_ids = [ticket[0] for ticket in top_similar_tickets]

        # Return the IDs and the new ticket embedding with proper JSON serialization
        return jsonify({
            'similar_ticket_ids': top_ticket_ids,
            'vector': json.dumps(new_ticket_embedding.tolist()) # Proper JSON serialization
        })

    except Exception as e:
        print(f"Error in process_ticket: {str(e)}")
        return jsonify({'error': f'Internal server error: {str(e)}', 'similar_ticket_ids': [], 'vector': None}), 500



def process_question_with_context(message, db_context, user_email):
    """
    Process user question using the provided database context.
    """
    message_lower = message.lower()

    # Handle greeting
    if any(word in message_lower for word in ['bonjour', 'salut', 'hello', 'hi', 'bonsoir']):
        return "Bonjour! Je suis l'assistant d'IssueTracker. Voici ce que je peux faire pour vous:\n- Consulter les tickets par statut, priorité ou date\n- Voir les projets et leurs départements\n- Afficher les statistiques du tableau de bord\n- Vérifier vos tickets personnels et notifications"

    # Handle thanks
    if any(word in message_lower for word in ['merci', 'thanks', 'thank you']):
        return "Pas de problème. Continuez à travailler efficacement."

    # Questions about tickets
    if 'ticket' in message_lower or 'demande' in message_lower or 'problème' in message_lower:
        if 'tickets' in db_context:
            tickets_info = db_context['tickets']
            response = f"Voici les informations sur les tickets:\n\n"
            response += f"Nombre total de tickets: {tickets_info.get('totalCount', 0)}\n"

            if 'byStatus' in tickets_info:
                response += "\nPar statut:\n"
                for status, count in tickets_info['byStatus'].items():
                    response += f"  - {status}: {count}\n"

            if 'byPriority' in tickets_info:
                response += "\nPar priorité:\n"
                for priority, count in tickets_info['byPriority'].items():
                    response += f"  - {priority}: {count}\n"

            if 'recentTickets' in tickets_info and tickets_info['recentTickets']:
                response += "\nTickets récents:\n"
                for t in tickets_info['recentTickets'][:5]:
                    response += f"  - #{t.get('id')}: {t.get('title')} ({t.get('status')})\n"

            return response.strip()

    # Questions about projects
    if 'projet' in message_lower or 'project' in message_lower:
        if 'projects' in db_context:
            projects = db_context['projects']
            response = f"Voici les projets disponibles ({len(projects)} au total):\n\n"
            for p in projects[:10]:
                response += f"  - {p.get('name')}"
                if 'department' in p:
                    response += f" (Département: {p.get('department')})"
                if 'ticketsCount' in p:
                    response += f" - {p.get('ticketsCount')} tickets"
                response += "\n"
            return response.strip()

    # Questions about departments
    if 'département' in message_lower or 'department' in message_lower or 'service' in message_lower:
        if 'departments' in db_context:
            departments = db_context['departments']
            response = f"Voici les départements ({len(departments)} au total):\n\n"
            for d in departments:
                response += f"  - {d.get('name')}"
                if 'usersCount' in d:
                    response += f" - {d.get('usersCount')} utilisateurs"
                if 'projectsCount' in d:
                    response += f" - {d.get('projectsCount')} projets"
                response += "\n"
            return response.strip()

    # Questions about statistics/dashboard
    if any(word in message_lower for word in ['stat', 'dashboard', 'nombre', 'combien', 'total', 'statistique']):
        if 'dashboardStats' in db_context:
            stats = db_context['dashboardStats']
            response = "Voici les statistiques du tableau de bord:\n\n"
            response += f"  - Total des tickets: {stats.get('totalTickets', 0)}\n"
            response += f"  - Tickets ouverts: {stats.get('openTickets', 0)}\n"
            response += f"  - Tickets en cours: {stats.get('inProgressTickets', 0)}\n"
            response += f"  - Tickets résolus: {stats.get('resolvedTickets', 0)}\n"
            response += f"  - Total des projets: {stats.get('totalProjects', 0)}\n"
            response += f"  - Total des utilisateurs: {stats.get('totalUsers', 0)}\n"
            return response.strip()

    # Questions about user's own tickets/notifications
    if 'mes' in message_lower or 'mon' in message_lower or 'ma' in message_lower:
        if 'currentUser' in db_context:
            user_info = db_context['currentUser']
            response = f"Bonjour {user_info.get('firstName', '')} {user_info.get('lastName', '')},\n\n"

            if 'userTicketCount' in db_context:
                response += f"Vous avez {db_context['userTicketCount']} ticket(s) créé(s).\n"

            if 'unreadNotificationsCount' in db_context:
                count = db_context['unreadNotificationsCount']
                if count > 0:
                    response += f"Vous avez {count} notification(s) non lue(s).\n"

            if 'userRecentTickets' in db_context and db_context['userRecentTickets']:
                response += "\nVos tickets récents:\n"
                for t in db_context['userRecentTickets'][:5]:
                    response += f"  - #{t.get('id')}: {t.get('title')} ({t.get('status')})\n"

            return response.strip()

    # Default response
    return "Je peux vous aider avec:\n" \
           "- Les tickets (nombre, statut, priorité)\n" \
           "- Les projets et départements\n" \
           "- Les statistiques et le tableau de bord\n" \
           "- Vos tickets et notifications personnels\n\n" \
           "Choisissez une option parmi celles ci-dessus pour obtenir des informations précises."

def detect_intent(message):
    """Simple intent detection."""
    message_lower = message.lower()
    if any(word in message_lower for word in ['bonjour', 'salut', 'hello']):
        return 'greeting'
    if 'ticket' in message_lower:
        return 'ticket_query'
    if 'projet' in message_lower or 'project' in message_lower:
        return 'project_query'
    if 'stat' in message_lower or 'dashboard' in message_lower:
        return 'dashboard_query'
    return 'general_query'

def detect_category(message):
    """Detect the category of the request."""
    message_lower = message.lower()
    if 'ticket' in message_lower or 'problème' in message_lower:
        return 'informatique'
    if 'projet' in message_lower:
        return 'administratif'
    return 'autres'

def detect_priority(message):
    """Detect priority from message."""
    message_lower = message.lower()
    if any(word in message_lower for word in ['urgent', 'critique', 'immédiat']):
        return 'High'
    if any(word in message_lower for word in ['faible', 'bas', 'petit']):
        return 'Low'
    return 'Medium'

@app.route('/health', methods=['GET'])
def health():
    """Health check endpoint."""
    return jsonify({'status': 'healthy'}), 200

if __name__ == "__main__":
    app.run(host='0.0.0.0', port=5000, debug=True)

# .\dupenv\bin\Activate.ps1
# python .\dup.py    
# waitress-serve --listen=*:5000 dup:app
