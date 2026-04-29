# PowerShell script to restart the project

Write-Host "Stopping Docker containers..." -ForegroundColor Yellow
docker-compose down

Write-Host "Removing volumes (to reset database with new schema)..." -ForegroundColor Yellow
docker-compose down -v

Write-Host "Starting Docker containers with new schema..." -ForegroundColor Yellow
docker-compose up -d

Write-Host "Waiting for services to be ready..." -ForegroundColor Cyan
Start-Sleep -Seconds 10

Write-Host "Project restarted successfully!" -ForegroundColor Green
Write-Host ""
Write-Host "Services:" -ForegroundColor White
Write-Host "  - Frontend: http://localhost:5173" -ForegroundColor Cyan
Write-Host "  - Backend:  http://localhost:6969" -ForegroundColor Cyan
Write-Host "  - Database: localhost:5432" -ForegroundColor Cyan

