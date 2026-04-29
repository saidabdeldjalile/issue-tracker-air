# Run both backend and frontend
Write-Host "Starting Backend..." -ForegroundColor Green
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd 'c:\Users\LENOVO\Desktop\IssueTracker-main\IssueTracker-main'; .\mvnw spring-boot:run"

Start-Sleep -Seconds 10

Write-Host "Starting Frontend..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd 'c:\Users\LENOVO\Desktop\IssueTracker-main\issue-tracker-web'; npm run dev"

Write-Host "Application starting..." -ForegroundColor Yellow
Write-Host "Backend: http://localhost:6969" -ForegroundColor White
Write-Host "Frontend: http://localhost:5173" -ForegroundColor White

