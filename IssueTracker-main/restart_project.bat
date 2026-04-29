@echo off
echo ============================================
echo Restarting Issue Tracker Project
echo ============================================

echo.
echo [1/4] Killing existing processes on port 6969...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :6969 ^| findstr LISTENING') do (
    echo Killing process %%a on port 6969
    taskkill /F /PID %%a >nul 2>&1
)

echo.
echo [2/4] Killing existing processes on port 5173...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :5173 ^| findstr LISTENING') do (
    echo Killing process %%a on port 5173
    taskkill /F /PID %%a >nul 2>&1
)

echo.
echo [3/4] Starting Backend (Spring Boot on port 6969)...
start "Spring Boot Backend" cmd /k "cd /d "%~dp0IssueTracker-main" && mvnw spring-boot:run"

timeout /t 15 /nobreak >nul

echo.
echo [4/4] Starting Frontend (React on port 5173)...
start "React Frontend" cmd /k "cd /d "%~dp0issue-tracker-web" && npm run dev"

echo.
echo ============================================
echo Project Restart Complete!
echo Backend: http://localhost:6969
echo Frontend: http://localhost:5173
echo ============================================
pause

