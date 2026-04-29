# PowerShell Script to Reset Database and Run Backend

Write-Host "============================================" -ForegroundColor Cyan
Write-Host "Resetting and Initializing Database" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan

# PostgreSQL path
$psqlPath = "C:\Program Files\PostgreSQL\14\bin\psql.exe"

# Step 1: Reset database
Write-Host "`nStep 1: Resetting database..." -ForegroundColor Yellow
& $psqlPath -h localhost -p 5433 -U postgres -f reset_db.sql

# Step 2: Create tables and insert data
Write-Host "`nStep 2: Creating tables and inserting data..." -ForegroundColor Yellow
& $psqlPath -h localhost -p 5433 -U postgres -d issue_tracker_db -f final_schema_data.sql

Write-Host "`nStep 3: Database reset complete!" -ForegroundColor Green

Write-Host "`n============================================" -ForegroundColor Cyan
Write-Host "Starting Backend (Spring Boot)" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan

# Step 4: Run Spring Boot
cd "C:\Users\LENOVO\Desktop\IssueTracker-main"
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=6969"

