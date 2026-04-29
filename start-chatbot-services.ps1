# Start Chatbot Services for IssueTracker
# Usage: .\start-chatbot-services.ps1

Continue = 'Continue'
 = Split-Path -Parent System.Management.Automation.InvocationInfo.MyCommand.Path
Set-Location 

Write-Host "======================================" -ForegroundColor Cyan
Write-Host "Starting IssueTracker Chatbot Services" -ForegroundColor Cyan
Write-Host "======================================" -ForegroundColor Cyan

# Check 1: Backend
Write-Host "
[1/5] Checking Backend (port 6969)..." -ForegroundColor Yellow
try {
     = Invoke-WebRequest -Uri "http://localhost:6969/api/v1/chat/health" -TimeoutSec 5 -UseBasicParsing
    if (.StatusCode -eq 200) {
        Write-Host "  ✅ Backend is running" -ForegroundColor Green
    } else {
        Write-Host "  ⚠️  Backend responded with status: " -ForegroundColor Yellow
    }
} catch {
    Write-Host "  ❌ Backend is NOT running" -ForegroundColor Red
    Write-Host "     Start with: mvn spring-boot:run" -ForegroundColor Gray
}

# Check 2: Ollama
Write-Host "
[2/5] Checking Ollama (port 11434)..." -ForegroundColor Yellow
try {
     = Invoke-WebRequest -Uri "http://localhost:11434/api/tags" -TimeoutSec 5 -UseBasicParsing
    if (.StatusCode -eq 200) {
         = .Content | ConvertFrom-Json
         = .models.name
        if ( -match "qwen2.5.*1\\.5b") {
            Write-Host "  ✅ Ollama is running with qwen2.5:1.5b" -ForegroundColor Green
        } else {
            Write-Host "  ⚠️  Ollama is running but qwen2.5:1.5b not found" -ForegroundColor Yellow
            Write-Host "     Run: ollama pull qwen2.5:1.5b" -ForegroundColor Gray
        }
    }
} catch {
    Write-Host "  ❌ Ollama is NOT running" -ForegroundColor Red
    Write-Host "     Start with: ollama serve" -ForegroundColor Gray
}

# Check 3: AI Chatbot Service
Write-Host "
[3/5] Checking AI Chatbot Service (port 5001)..." -ForegroundColor Yellow
 = False
try {
     = Invoke-WebRequest -Uri "http://localhost:5001/health" -TimeoutSec 5 -UseBasicParsing
    if (.StatusCode -eq 200) {
        Write-Host "  ✅ AI Chatbot Service is running" -ForegroundColor Green
         = True
    }
} catch {
    Write-Host "  ❌ AI Chatbot Service is NOT running" -ForegroundColor Red
}

if (-not ) {
    Write-Host "
  Attempting to start AI Chatbot Service..." -ForegroundColor Yellow
     = Join-Path  "ai-service"
    if (Test-Path ) {
        Write-Host "  Starting: python run.py in ai-service/" -ForegroundColor Gray
        Start-Process -FilePath "python" -ArgumentList "run.py" -WorkingDirectory  -NoNewWindow
        Start-Sleep -Seconds 3
        
        try {
             = Invoke-WebRequest -Uri "http://localhost:5001/health" -TimeoutSec 5 -UseBasicParsing
            if (.StatusCode -eq 200) {
                Write-Host "  ✅ AI Chatbot Service started successfully" -ForegroundColor Green
                 = True
            }
        } catch {
            Write-Host "  ❌ Failed to start AI Chatbot Service" -ForegroundColor Red
            Write-Host "     Check: python ai-service/run.py" -ForegroundColor Gray
        }
    } else {
        Write-Host "  ❌ ai-service directory not found" -ForegroundColor Red
    }
}

# Check 4: Rasa Actions Server (optional)
Write-Host "
[4/5] Checking Rasa Actions Server (port 5055)..." -ForegroundColor Yellow
try {
     = Invoke-WebRequest -Uri "http://localhost:5055/webhooks/rest/webhook" -TimeoutSec 5 -UseBasicParsing
    # Rasa actions server doesn't have /health, but responds to webhook with 500 if no data
    Write-Host "  ⚠️  Rasa Actions Server status unclear (this is OK)" -ForegroundColor Yellow
} catch {
    Write-Host "  ⚪ Rasa Actions Server not running (optional)" -ForegroundColor Gray
    Write-Host "     Start with: python -m rasa_sdk --actions actions_server --port 5055" -ForegroundColor Gray
}

# Check 5: Frontend
Write-Host "
[5/5] Checking Frontend (port 5173)..." -ForegroundColor Yellow
try {
     = Invoke-WebRequest -Uri "http://localhost:5173" -TimeoutSec 5 -UseBasicParsing
    if (.StatusCode -eq 200) {
        Write-Host "  ✅ Frontend is running" -ForegroundColor Green
    }
} catch {
    Write-Host "  ⚪ Frontend not running (optional for API test)" -ForegroundColor Gray
    Write-Host "     Start with: cd issue-tracker-web && npm run dev" -ForegroundColor Gray
}

# Summary
Write-Host "
======================================" -ForegroundColor Cyan
Write-Host "Summary" -ForegroundColor Cyan
Write-Host "======================================" -ForegroundColor Cyan

if () {
    Write-Host "
✅ Chatbot services are ready!" -ForegroundColor Green
    Write-Host "" 
    Write-Host "Test the chatbot:" -ForegroundColor White
    Write-Host "  curl -X POST http://localhost:5001/chat " -ForegroundColor Gray
    Write-Host "       -H 'Content-Type: application/json' " -ForegroundColor Gray
    Write-Host "       -d '{\"message\":\"bonjour\"}'" -ForegroundColor Gray
    Write-Host "" 
    Write-Host "Or open: http://localhost:5173" -ForegroundColor Gray
} else {
    Write-Host "
❌ Chatbot services could not start" -ForegroundColor Red
    Write-Host "" 
    Write-Host "Manual start commands:" -ForegroundColor Yellow
    Write-Host "  1. ollama serve" -ForegroundColor White
    Write-Host "  2. ollama pull qwen2.5:1.5b" -ForegroundColor White
    Write-Host "  3. python ai-service/run.py" -ForegroundColor White
    Write-Host "  4. cd issue-tracker-web && npm run dev" -ForegroundColor White
}

Write-Host "
Known Issues:" -ForegroundColor Yellow
Write-Host "  - If Ollama fails, ensure it is installed: https://ollama.ai" -ForegroundColor Gray
Write-Host "  - Model qwen2.5:1.5b must be pulled first" -ForegroundColor Gray
Write-Host "  - Backend must be running on port 6969" -ForegroundColor Gray
Write-Host ""