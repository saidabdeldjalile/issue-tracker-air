#!/usr/bin/env python3
"""
Rasa Actions Server for Air Algérie Chatbot
"""
from rasa_sdk import Action
from rasa_sdk.executor import CollectingDispatcher
from rasa_sdk import Tracker
from typing import Dict, Text, List, Any
import logging
import requests

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

BACKEND_URL = "http://localhost:6969/api"
FAQ_API = f"{BACKEND_URL}/faqs"
TICKET_API = f"{BACKEND_URL}/tickets"

class ActionCreateTicket(Action):
    def name(self) -> Text:
        return "action_create_ticket"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:

        user_message = tracker.latest_message.get('text', '')
        user_email = tracker.get_slot('user_email') or 'user@airalgerie.dz'

        # Classify the request
        category_info = self._classify_request(user_message)

        # Create ticket via backend API
        ticket_data = {
            "title": user_message[:100],
            "description": user_message,
            "category": category_info.get('category', 'autres'),
            "priority": self._determine_priority(user_message),
            "reporter": user_email
        }

        try:
            response = requests.post(TICKET_API, json=ticket_data, timeout=10)
            if response.status_code in [200, 201]:
                ticket = response.json()
                dispatcher.utter_message(text=f"Ticket créé avec succès! Numéro: #{ticket.get('id')}")
                return []
            else:
                dispatcher.utter_message(text="Désolé, je n'ai pas pu créer le ticket. Veuillez contacter le support directement.")
        except Exception as e:
            logger.error(f"Error creating ticket: {e}")
            dispatcher.utter_message(text="Erreur technique lors de la création du ticket.")

        return []

    def _classify_request(self, message: str) -> Dict[str, str]:
        """Classify the request category and department"""
        message_lower = message.lower()

        # Simple keyword-based classification
        categories = {
            "informatique": ["ordinateur", "pc", "mot de passe", "email", "logiciel", "réseau", "vpn", "bug", "incident"],
            "materiel": ["imprimante", "écran", "souris", "clavier", "téléphone", "équipement"],
            "administratif": ["congé", "vacances", "salaire", "document", "rh", "administratif"],
            "maintenance": ["climatisation", "éclairage", "porte", "plomberie", "ascenseur", "bâtiment"],
            "achat": ["fourniture", "achats", "commande", "budget", "achat"]
        }

        for category, keywords in categories.items():
            if any(keyword in message_lower for keyword in keywords):
                return {"category": category}

        return {"category": "autres"}

    def _determine_priority(self, message: str) -> str:
        urgent_keywords = ["urgent", "critique", "bloquant", "immédiat", "panne", "hors service", "grave"]
        return "High" if any(kw in message.lower() for kw in urgent_keywords) else "Medium"


class ActionTrackTicket(Action):
    def name(self) -> Text:
        return "action_track_ticket"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:

        ticket_id = tracker.get_slot('ticket_id')

        if not ticket_id:
            dispatcher.utter_message(text="Pouvez-vous me donner le numéro du ticket que vous souhaitez suivre?")
            return []

        try:
            response = requests.get(f"{TICKET_API}/{ticket_id}", timeout=5)
            if response.status_code == 200:
                ticket = response.json()
                status = ticket.get('status', 'Unknown')
                dispatcher.utter_message(text=f"Votre ticket #{ticket_id} est en statut: {status}")
            else:
                dispatcher.utter_message(text="Ticket non trouvé. Vérifiez le numéro.")
        except Exception as e:
            logger.error(f"Error tracking ticket: {e}")
            dispatcher.utter_message(text="Erreur lors de la récupération du statut du ticket.")

        return []


class ActionSearchFAQ(Action):
    def name(self) -> Text:
        return "action_search_faq"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:

        user_message = tracker.latest_message.get('text', '')

        try:
            response = requests.get(f"{FAQ_API}/search?query={user_message}&page=0&size=3", timeout=5)
            if response.status_code == 200:
                data = response.json()
                if data.get('content') and len(data['content']) > 0:
                    faq = data['content'][0]
                    dispatcher.utter_message(text=f"Résultat trouvé:\n\n{faq.get('answer', '')}")
                    return []
                else:
                    dispatcher.utter_message(text="Je n'ai pas trouvé de réponse exacte dans la FAQ.")
            else:
                dispatcher.utter_message(text="Erreur lors de la recherche dans la FAQ.")
        except Exception as e:
            logger.error(f"Error searching FAQ: {e}")
            dispatcher.utter_message(text="Erreur technique lors de la recherche.")

        return []


class ActionAnalyzeSentiment(Action):
    def name(self) -> Text:
        return "action_analyze_sentiment"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:

        user_message = tracker.latest_message.get('text', '')

        # Simple sentiment analysis
        negative_words = ["problème", "marche pas", "hs", "cassé", "urgent", "grave"]
        positive_words = ["merci", "super", "parfait", "génial", "bien"]

        negative_score = sum(1 for w in negative_words if w in user_message.lower())
        positive_score = sum(1 for w in positive_words if w in user_message.lower())

        if negative_score > 0:
            sentiment = "negative"
        elif positive_score > 0:
            sentiment = "positive"
        else:
            sentiment = "neutral"

        return []


class ActionClassifyRequest(Action):
    def name(self) -> Text:
        return "action_classify_request"

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:

        user_message = tracker.latest_message.get('text', '')

        # Classify category
        category_info = self._classify_request(user_message)

        # Set slots
        return []

    def _classify_request(self, message: str) -> Dict[str, str]:
        message_lower = message.lower()

        categories = {
            "informatique": {
                "keywords": ["ordinateur", "pc", "mot de passe", "email", "logiciel", "réseau", "vpn", "bug", "incident"],
                "department": "IT"
            },
            "materiel": {
                "keywords": ["imprimante", "écran", "souris", "clavier", "téléphone", "équipement"],
                "department": "Achats"
            },
            "administratif": {
                "keywords": ["congé", "vacances", "salaire", "document", "rh", "administratif"],
                "department": "RH"
            },
            "maintenance": {
                "keywords": ["climatisation", "éclairage", "porte", "plomberie", "ascenseur", "bâtiment"],
                "department": "Maintenance"
            },
            "achat": {
                "keywords": ["fourniture", "achats", "commande", "budget", "achat"],
                "department": "Achats"
            }
        }

        for category, info in categories.items():
            if any(keyword in message_lower for keyword in info["keywords"]):
                return {
                    "category": category,
                    "department": info["department"]
                }

        return {"category": "autres", "department": "Support"}