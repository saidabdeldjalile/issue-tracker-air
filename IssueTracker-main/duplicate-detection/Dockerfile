FROM python:3.9-slim

WORKDIR /app

COPY requirements.txt .

RUN pip3 install torch --index-url https://download.pytorch.org/whl/cpu && pip3 install --no-cache-dir -r requirements.txt

COPY . .

EXPOSE 5000

CMD ["waitress-serve", "--listen=*:5000", "dup:app"]
