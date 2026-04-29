# Delete All Comments TODO

- [x] Create delete_comments.sql ✅
- [x] Create run_delete_comments.bat ✅
- [ ] Stop backend (kill_port.bat or Ctrl+C if running)
Run `run_delete_comments.bat` (fixed with full psql path)
Verify: `"C:\Program Files\PostgreSQL\14\bin\psql.exe" -h localhost -p 5433 -U postgres -d issue_tracker_db -c "SELECT COUNT(*) FROM comment;"`
- [ ] Restart app (start_project.bat or restart_backend.bat)
- [ ] Test app functionality ✅

**Note**: Enter password when prompted by psql (default: postgres).
