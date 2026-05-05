Write-Host "Starting Air Algerie Issue Tracker Platform..."

# Start Backend
Write-Host "Starting Backend..."
Start-Process -FilePath "cmd.exe" -ArgumentList "/k title Backend & .\mvnw.cmd spring-boot:run"

# Start AI Service
Write-Host "Starting AI Service..."
Start-Process -FilePath "cmd.exe" -ArgumentList "/k title AI Service & cd ai-service & python run.py"

# Start Rasa Actions
Write-Host "Starting Rasa Actions..."
Start-Process -FilePath "cmd.exe" -ArgumentList "/k title Rasa Actions & cd ai-service & python -m rasa_sdk --actions actions_server --port 5055"

# Start Frontend
Write-Host "Starting Frontend..."
Start-Process -FilePath "cmd.exe" -ArgumentList "/k title Frontend & cd issue-tracker-web & npm run dev"

Write-Host "All services are starting in separate windows!"
