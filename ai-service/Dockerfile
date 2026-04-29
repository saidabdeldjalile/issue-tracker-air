FROM python:3.11-slim

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt && \
    python -m spacy download fr_core_news_sm

COPY . .

EXPOSE 5001
CMD ["python", "app.py"]
