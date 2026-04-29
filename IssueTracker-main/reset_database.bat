@echo off
echo ================================================
echo Resetting Issue Tracker Database
echo ================================================

REM Set PostgreSQL connection parameters
SET PGHOST=localhost
SET PGPORT=5433
SET PGUSER=postgres
SET PGPASSWORD=postgres
SET PGDATABASE=postgres

REM Check if PostgreSQL is running
echo.
echo Checking PostgreSQL connection...
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -c "SELECT version();" >nul 2>&1
if errorlevel 1 (
    echo ERROR: Cannot connect to PostgreSQL. Make sure PostgreSQL is running.
    echo Try starting it with: start_postgres.bat
    pause
    exit /b 1
)

echo Connected to PostgreSQL successfully.
echo.
echo Executing database reset script...
echo.

REM Execute the SQL script
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -f "%~dp0reset_database_new.sql"

if errorlevel 1 (
    echo.
    echo ERROR: Failed to reset database.
    pause
    exit /b 1
)

echo.
echo ================================================
echo Database reset completed successfully!
echo ================================================
echo.
echo Default admin credentials:
echo   Email: admin@issue.com
echo   Password: admin123
echo.

pause

