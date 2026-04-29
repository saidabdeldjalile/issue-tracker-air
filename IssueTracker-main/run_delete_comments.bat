@echo off
set PGPASSWORD=postgres
echo Deleting all comments from issue_tracker_db...
"C:\Program Files\PostgreSQL\14\bin\psql.exe" -h localhost -p 5433 -U postgres -d issue_tracker_db -f delete_comments.sql
if %ERRORLEVEL% == 0 (
    echo Success: All comments deleted.
) else (
    echo Error occurred.
)
pause
