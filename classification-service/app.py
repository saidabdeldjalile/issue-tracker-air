from flask import Flask, request, jsonify
from flask_cors import CORS

app = Flask(__name__)
CORS(app)

CATEGORY_KEYWORDS = {
    "informatique": ["wifi", "ordinateur", "mot de passe", "email", "logiciel", "réseau", "VPN", "serveur", "pc", "bug", "incident", "application", "informatique", "IT", "technique"],
    "materiel": ["imprimante", "écran", "souris", "clavier", "chaise", "bureau", "téléphone", "matériel", "équipement", "hardware"],
    "administratif": ["congé", "vacances", "salaire", "bulletin", "contrat", "document", "RH", "ressources humaines", "paie", "administratif"],
    "maintenance": ["climatisation", "éclairage", "portes", "serrure", "plomberie", "ascenseur", "réparation", "bâtiment", "maintenance"],
    "achat": ["fourniture", "achats", "commande", "budget", "achat"],
    "formation": ["formation", "cours", "apprentissage", "diplôme", "certification", "stage"],
}

DEPARTMENT_MAPPING = {
    "informatique": "IT",
    "materiel": "Achats",
    "administratif": "RH",
    "maintenance": "Maintenance",
    "achat": "Achats",
    "formation": "RH",
    "autres": "Support"
}


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
        best = max(scores.keys(), key=lambda k: scores[k])
        return best
    return "autres"


def extract_keywords(text):
    if not text:
        return []
    text_lower = text.lower()
    all_keywords = [kw for kws in CATEGORY_KEYWORDS.values() for kw in kws]
    return list(set([kw for kw in all_keywords if kw in text_lower]))[:5]


def determine_priority(text):
    urgent_keywords = ["urgent", "critique", "bloquant", "immédiat", "panne", "hors service", "grave", "security"]
    return "High" if any(kw in text.lower() for kw in urgent_keywords) else "Medium"


@app.route('/classify', methods=['POST'])
def classify():
    try:
        data = request.json
        if not data:
            return jsonify({'error': 'No data provided'}), 400
        
        text = data.get('text', '')
        title = data.get('title', '')
        full_text = f"{title} {text}"
        
        category = classify_category(full_text)
        keywords = extract_keywords(full_text)
        priority = determine_priority(full_text)
        
        return jsonify({
            'category': category,
            'suggestedDepartment': DEPARTMENT_MAPPING.get(category, "Support"),
            'suggestedPriority': priority,
            'keywords': keywords,
            'confidence': 0.85
        })
    
    except Exception as e:
        return jsonify({'error': str(e)}), 500


@app.route('/health', methods=['GET'])
def health():
    return jsonify({'status': 'ok'})


if __name__ == "__main__":
    app.run(host='0.0.0.0', port=5001)