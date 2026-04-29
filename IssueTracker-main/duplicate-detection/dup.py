import nltk
import torch
from bs4 import BeautifulSoup
from flask import Flask, request, jsonify
from flask_cors import CORS
from sentence_transformers import SentenceTransformer
from sentence_transformers import util

app = Flask(__name__)
CORS(app)
model = SentenceTransformer('all-MiniLM-L6-v2')

nltk.download('stopwords')

from nltk.corpus import stopwords

stop_words = set(stopwords.words('english'))


def remove_stop_words(text):
    words = text.split()
    filtered_words = [word for word in words if word.lower() not in stop_words]
    return ' '.join(filtered_words)


@app.route('/process_ticket', methods=['POST'])
def process_ticket():
    data = request.json
    new_ticket_text = data['text']
    embedded_tickets = data['ticketEmbeddingDTOS']

    # Get the embedding for the new ticket
    new_ticket_embedding = model.encode(
        remove_stop_words(BeautifulSoup(new_ticket_text, 'lxml').get_text()))
    # print(new_ticket_embedding[0])
    # Calculate cosine similarity with all existing vectors and include ticket IDs
    if (len(embedded_tickets) == 0):
        return jsonify({
            'similar_ticket_ids': [],
            'vector': str(new_ticket_embedding.tolist())  # Convert NumPy array to list for JSON serialization
        })
    # print(embedded_tickets)
    # print(embedded_tickets[0]['vector'])
    # print(type(embedded_tickets[0]['vector']))

    similarities_with_ids = [
        (ticket['ticketId'], util.cos_sim(new_ticket_embedding, torch.tensor(eval(ticket['vector']))))
        # print the tpye of ticket['vector'] to see if it is a string or a list
        for ticket in embedded_tickets
    ]
    print(similarities_with_ids)

    # Filter out the tickets with similarity greater than 0.8
    similar_tickets = [(ticketId, sim) for ticketId, sim in similarities_with_ids if sim > 0.76]

    # Select top 10 similar tickets
    top_similar_tickets = sorted(similar_tickets, key=lambda x: x[1], reverse=True)[:10]

    # Extract the ticket IDs from the top similar tickets
    top_ticket_ids = [ticket[0] for ticket in top_similar_tickets]
    # print(top_ticket_ids)
    # Return the IDs and the new ticket embedding
    # print(len(str(new_ticket_embedding.tolist())))
    return jsonify({
        'similar_ticket_ids': top_ticket_ids,
        'vector': str(new_ticket_embedding.tolist())  # Convert NumPy array to list for JSON serialization
    })


if __name__ == "__main__":
    app.run()

# .\dupenv\bin\Activate.ps1
# python .\dup.py    
# waitress-serve --listen=*:5000 dup:app
