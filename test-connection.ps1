# Test Connection Script for IssueTracker Chatbot
# Usage: .\test-connection.ps1

Continue = 'Stop'

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "IssueTracker Connection Test" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

 = 0
 = 0

function Test-Endpoint {
    param(
        [string],
        [string],
        [int] = 200
    )
    
    Write-Host "
Testing: " -ForegroundColor Yellow
    Write-Host "  URL: " -ForegroundColor Gray
    
    try {
         = Invoke-WebRequest -Uri  -TimeoutSec 5 -UseBasicParsing
        if (.StatusCode -eq ) {
            Write-Host "  ✅ PASS (Status: )" -ForegroundColor Green
            return True
        } else {
            Write-Host "  ❌ FAIL (Status: , Expected: )" -ForegroundColor Red
            return False
        }
    } catch {
        Write-Host "  ❌ FAIL (Connection Error: )" -ForegroundColor Red
        return False
    }
}

# Test 1: Backend Health
if (Test-Endpoint -Name "Backend Health Check" -Url "http://localhost:6969/actuator/health") {
    ++
} else {
    ++
}

# Test 2: Backend Chat Endpoint
if (Test-Endpoint -Name "Backend /api/v1/chat Endpoint" -Url "http://localhost:6969/api/v1/chat/health") {
    ++
} else {
    ++
}

# Test 3: AI Chatbot Health
if (Test-Endpoint -Name "AI Chatbot Health Check" -Url "http://localhost:5001/health") {
    ++
} else {
    ++
}

# Test 4: Ollama (with model check)
Write-Host "
Testing: Ollama Model Availability" -ForegroundColor Yellow
try {
     = Invoke-WebRequest -Uri "http://localhost:11434/api/tags" -TimeoutSec 5 -UseBasicParsing
    if (.StatusCode -eq 200) {
         = .Content | ConvertFrom-Json
         = .models.name
        if ( -match "qwen2\\.5.*1\\.5b") {
            Write-Host "  ✅ PASS (qwen2.5:1.5b found)" -ForegroundColor Green
            ++
        } else {
            Write-Host "  ❌ FAIL (qwen2.5:1.5b model not found)" -ForegroundColor Red
            Write-Host "     Run: ollama pull qwen2.5:1.5b" -ForegroundColor Gray
            ++
        }
    } else {
        Write-Host "  ❌ FAIL (Status: )" -ForegroundColor Red
        ++
    }
} catch {
    Write-Host "  ❌ FAIL (Connection Error)" -ForegroundColor Red
    Write-Host "     Ollama may not be running" -ForegroundColor Gray
    ++
}

# Test 5: Rasa Actions (optional)
Write-Host "
Testing: Rasa Actions Server" -ForegroundColor Yellow
try {
     = Invoke-WebRequest -Uri "http://localhost:5055/webhooks/rest/webhook" -TimeoutSec 5 -UseBasicParsing -Method POST -Body '{"message":"test"}' -ContentType "application/json"
    Write-Host "  ⚠️  PASS (Status: )" -ForegroundColor Yellow
    ++
} catch {
    Write-Host "  ⚪ SKIP (Rasa Actions is optional)" -ForegroundColor Gray
}

# Test 6: Frontend
Write-Host "
Testing: Frontend Application" -ForegroundColor Yellow
try {
     = Invoke-WebRequest -Uri "http://localhost:5173" -TimeoutSec 5 -UseBasicParsing
    if (.StatusCode -eq 200) {
        Write-Host "  ✅ PASS (Status: )" -ForegroundColor Green
        ++
    } else {
        Write-Host "  ❌ FAIL (Status: )" -ForegroundColor Red
        ++
    }
} catch {
    Write-Host "  ⚪ SKIP (Frontend not required for API test)" -ForegroundColor Gray
}

# Test 7: Actual Chat Test
Write-Host "
Testing: AI Chatbot Actual Response" -ForegroundColor Yellow
if ((Test-Endpoint -Name "AI Chatbot Health" -Url "http://localhost:5001/health") -eq True) {
    try {
        Write-Host "  Sending: {\"message\":\"bonjour\"}" -ForegroundColor Gray
         = @{ message = "bonjour" } | ConvertTo-Json
         = Invoke-WebRequest -Uri "http://localhost:5001/chat" -TimeoutSec 10 -UseBasicParsing -Method POST -Body  -ContentType "application/json"
        if (.StatusCode -eq 200) {
             = .Content | ConvertFrom-Json
            if (.response) {
                Write-Host "  ✅ PASS (Received response)" -ForegroundColor Green
                Write-Host "     Response: ..." -ForegroundColor Gray
                ++
            } else {
                Write-Host "  ❌ FAIL (No response field)" -ForegroundColor Red
                ++
            }
        } else {
            Write-Host "  ❌ FAIL (Status: )" -ForegroundColor Red
            ++
        }
    } catch {
        Write-Host "  ❌ FAIL (Error: )" -ForegroundColor Red
        ++
    }
} else {
    Write-Host "  ⚪ SKIP (AI Chatbot not running)" -ForegroundColor Gray
}

# Summary
Write-Host "
========================================" -ForegroundColor Cyan
Write-Host "Test Summary" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Passed: " -ForegroundColor Green
Write-Host "Failed: " -ForegroundColor Green

if ( -eq 0) {
    Write-Host "
✅ All tests passed!" -ForegroundColor Green
} elseif ( -gt 0) {
    Write-Host "
⚠️  Some tests failed" -ForegroundColor Yellow
    Write-Host "Run ./start-chatbot-services.ps1 to start missing services" -ForegroundColor Gray
} else {
    Write-Host "
❌ All tests failed" -ForegroundColor Red
}