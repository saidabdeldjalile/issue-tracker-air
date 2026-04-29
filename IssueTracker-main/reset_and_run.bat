@echo off
echo ============================================
echo Resetting and Initializing Database
echo ============================================

echo Step 1: Resetting database...
"C:\Program Files\PostgreSQL\14\bin\psql.exe" -h localhost -p 5433 -U postgres -f reset_db.sql

echo Step 2: Creating tables and inserting data...
"C:\Program Files\PostgreSQL\14\bin\psql.exe" -h localhost -p 5433 -U postgres -d issue_tracker_db -f final_schema_data.sql

echo Step 3: Database reset complete!
echo.
echo ============================================
echo Starting Backend (Spring Boot)
echo ============================================

cd /d "%~dp0"
call mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=6969

pause

