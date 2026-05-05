FROM python:3.11-slim

# Install system build dependencies
RUN apt-get update && apt-get install -y --no-install-recommends \
    build-essential gcc python3-dev libffi-dev libssl-dev && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Install PyTorch CPU version first (much smaller)
RUN pip install --no-cache-dir torch --index-url https://download.pytorch.org/whl/cpu

# Install other Python dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir --timeout 300 -r requirements.txt

# Download spacy model
RUN python -m spacy download fr_core_news_sm

# Copy application code
COPY . .

EXPOSE 5001
CMD ["python", "app.py"]