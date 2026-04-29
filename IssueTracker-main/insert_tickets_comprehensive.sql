-- ============================================================================
-- COMPREHENSIVE TICKET INSERTION SCRIPT FOR ISSUE TRACKER - AIR ALGERIE
-- ============================================================================
-- This script inserts realistic, well-structured ticket data for all departments
-- ============================================================================

-- ============================================================================
-- DELETE EXISTING TICKETS (Optional - uncomment if you want to start fresh)
-- ============================================================================
-- DELETE FROM comment WHERE ticket_id IN (SELECT id FROM ticket);
-- DELETE FROM ticket WHERE id > 30;

-- ============================================================================
-- INSERT TICKETS FOR DSI (IT) DEPARTMENT
-- ============================================================================
-- High Priority IT Issues
INSERT INTO ticket (title, description, status, priority, category, vector, created_at, modified_at, created_by_id, assigned_to_id, project_id) VALUES
('Critical Server Outage', 'Main application server is completely down. All users unable to access the system. Immediate attention required.', 'InProgress', 'Critical', 'Infrastructure', 'Server Room A - Rack 3', CURRENT_TIMESTAMP - INTERVAL '2 hours', CURRENT_TIMESTAMP, 5, 3, 1),
('Database Performance Degradation', 'Database queries are taking 10x longer than normal. Users reporting slow application response times.', 'Open', 'High', 'Database', 'Production Database Cluster', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', 6, 4, 1),
('Security Vulnerability Detected', 'Nessus scan detected critical security vulnerability in web application. Requires immediate patching.', 'ToDo', 'Critical', 'Security', 'Web Application Firewall', CURRENT_TIMESTAMP - INTERVAL '4 hours', CURRENT_TIMESTAMP - INTERVAL '4 hours', 7, 3, 1),
('Email System Malfunction', 'Corporate email system is rejecting incoming emails. Multiple departments affected.', 'InProgress', 'High', 'Email', 'Exchange Server', CURRENT_TIMESTAMP - INTERVAL '6 hours', CURRENT_TIMESTAMP - INTERVAL '3 hours', 5, 4, 1),
('VPN Access Issues', 'Remote employees unable to connect to VPN from multiple locations.', 'Open', 'Medium', 'Network', 'VPN Gateway', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', 6, 3, 1),
('Software License Expiration', 'Adobe Creative Suite licenses expiring next week for 15 users.', 'ToDo', 'Medium', 'Software', 'Licensing Server', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '3 days', 7, 4, 1),
('Workstation Hardware Failure', 'Finance department workstation completely dead. Critical financial data at risk.', 'Open', 'High', 'Hardware', 'Finance Office - Desk 12', CURRENT_TIMESTAMP - INTERVAL '3 hours', CURRENT_TIMESTAMP - INTERVAL '3 hours', 5, 3, 1),
('Network Switch Failure', 'Network switch on 3rd floor failed, affecting 25 users.', 'InProgress', 'High', 'Network', 'Floor 3 - Switch B', CURRENT_TIMESTAMP - INTERVAL '1 hour', CURRENT_TIMESTAMP, 6, 4, 1),
('Backup System Failure', 'Nightly backup process failed for the past 3 days. Data integrity at risk.', 'Open', 'Critical', 'Backup', 'Backup Server', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '2 days', 7, 3, 1),
('Antivirus Update Issues', 'Antivirus definitions not updating on 50+ machines across the company.', 'ToDo', 'Medium', 'Security', 'Endpoint Protection', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '2 days', 5, 4, 1);

-- ============================================================================
-- INSERT TICKETS FOR DRH (HR) DEPARTMENT
-- ============================================================================
-- HR Management and Employee Issues
INSERT INTO ticket (title, description, status, priority, category, vector, created_at, modified_at, created_by_id, assigned_to_id, project_id) VALUES
('Employee Onboarding System Down', 'New employee onboarding portal is completely inaccessible. 5 new hires cannot complete paperwork.', 'InProgress', 'High', 'Onboarding', 'HR Portal', CURRENT_TIMESTAMP - INTERVAL '4 hours', CURRENT_TIMESTAMP, 11, 9, 2),
('Payroll Processing Error', 'Payroll system showing incorrect overtime calculations for March. 120 employees affected.', 'Open', 'Critical', 'Payroll', 'Payroll System', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', 12, 9, 2),
('Training Module Malfunction', 'Safety training module not tracking completion status correctly.', 'ToDo', 'Medium', 'Training', 'LMS System', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '2 days', 13, 10, 2),
('Employee Records Access Issue', 'Cannot access employee records database. Performance reviews delayed.', 'Open', 'High', 'Records', 'HR Database', CURRENT_TIMESTAMP - INTERVAL '6 hours', CURRENT_TIMESTAMP - INTERVAL '6 hours', 11, 9, 2),
('Benefits Enrollment Problem', 'Open enrollment portal not accepting submissions for dental benefits.', 'InProgress', 'Medium', 'Benefits', 'Benefits Portal', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '12 hours', 12, 10, 2),
('Time Tracking System Error', 'Time clock system not recording hours for night shift employees.', 'Open', 'High', 'Timekeeping', 'Time Clock System', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', 13, 9, 2),
('Employee Portal Password Reset', 'Multiple employees locked out of employee portal after password policy update.', 'ToDo', 'Medium', 'Access', 'Employee Portal', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', 11, 10, 2),
('Performance Review System', 'Performance review system not allowing managers to submit reviews.', 'Open', 'Medium', 'Reviews', 'Performance System', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '3 days', 12, 9, 2),
('Recruitment System Down', 'Job posting system not accepting new applications. Critical for ongoing hiring.', 'InProgress', 'High', 'Recruitment', 'ATS System', CURRENT_TIMESTAMP - INTERVAL '8 hours', CURRENT_TIMESTAMP - INTERVAL '4 hours', 13, 10, 2),
('Employee Data Migration', 'Need to migrate employee data from old system to new HRIS platform.', 'ToDo', 'Low', 'Migration', 'HRIS System', CURRENT_TIMESTAMP - INTERVAL '1 week', CURRENT_TIMESTAMP - INTERVAL '1 week', 11, 9, 2);

-- ============================================================================
-- INSERT TICKETS FOR OPS (OPERATIONS) DEPARTMENT
-- ============================================================================
-- Flight Operations and Scheduling Issues
INSERT INTO ticket (title, description, status, priority, category, vector, created_at, modified_at, created_by_id, assigned_to_id, project_id) VALUES
('Flight Schedule System Crash', 'Main flight scheduling system crashed during peak booking hours. All bookings affected.', 'InProgress', 'Critical', 'Scheduling', 'Operations System', CURRENT_TIMESTAMP - INTERVAL '3 hours', CURRENT_TIMESTAMP, 17, 15, 3),
('Crew Assignment Conflict', 'System showing duplicate crew assignments for flight AH-1024 tomorrow morning.', 'Open', 'High', 'Crew', 'Crew Management', CURRENT_TIMESTAMP - INTERVAL '12 hours', CURRENT_TIMESTAMP - INTERVAL '12 hours', 18, 15, 3),
('Passenger Manifest Error', 'Passenger manifest for flight AH-501 shows incorrect passenger count by 15.', 'InProgress', 'High', 'Passenger', 'Manifest System', CURRENT_TIMESTAMP - INTERVAL '2 hours', CURRENT_TIMESTAMP - INTERVAL '1 hour', 19, 16, 3),
('Cargo Loading System Down', 'Cargo loading system not communicating with warehouse management system.', 'Open', 'Medium', 'Cargo', 'Cargo System', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', 17, 16, 3),
('Flight Tracking System Issue', 'Real-time flight tracking not updating for international flights.', 'ToDo', 'Medium', 'Tracking', 'Flight Tracking', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '2 days', 18, 15, 3),
('Gate Assignment Problem', 'Gate assignment system showing conflicts for Terminal 2 flights this afternoon.', 'Open', 'High', 'Gates', 'Gate Management', CURRENT_TIMESTAMP - INTERVAL '4 hours', CURRENT_TIMESTAMP - INTERVAL '4 hours', 19, 16, 3),
('Weather Data Integration', 'Weather data not updating in flight planning system. Safety concern.', 'InProgress', 'Critical', 'Weather', 'Weather System', CURRENT_TIMESTAMP - INTERVAL '6 hours', CURRENT_TIMESTAMP - INTERVAL '3 hours', 17, 15, 3),
('Check-in System Slowdown', 'Online check-in system responding very slowly during peak hours.', 'Open', 'Medium', 'Check-in', 'Check-in System', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', 18, 16, 3),
('Baggage Handling System', 'Baggage handling system showing errors for connecting flights.', 'ToDo', 'Medium', 'Baggage', 'Baggage System', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', 19, 15, 3),
('Flight Delay Notifications', 'Passenger notification system not sending delay updates for domestic flights.', 'Open', 'High', 'Notifications', 'Notification System', CURRENT_TIMESTAMP - INTERVAL '8 hours', CURRENT_TIMESTAMP - INTERVAL '8 hours', 17, 16, 3);

-- ============================================================================
-- INSERT TICKETS FOR MAINTENANCE DEPARTMENT
-- ============================================================================
-- Aircraft Maintenance and Technical Issues
INSERT INTO ticket (title, description, status, priority, category, vector, created_at, modified_at, created_by_id, assigned_to_id, project_id) VALUES
('Engine Vibration Alert', 'Aircraft AT-72 showing engine vibration alerts during last flight. Requires immediate inspection.', 'InProgress', 'Critical', 'Engine', 'Aircraft AT-72', CURRENT_TIMESTAMP - INTERVAL '2 hours', CURRENT_TIMESTAMP, 23, 21, 4),
('Hydraulic System Leak', 'Hydraulic fluid leak detected on aircraft BT-45 during pre-flight inspection.', 'Open', 'High', 'Hydraulics', 'Aircraft BT-45', CURRENT_TIMESTAMP - INTERVAL '4 hours', CURRENT_TIMESTAMP - INTERVAL '4 hours', 24, 21, 4),
('Avionics Software Update', 'Scheduled software update required for navigation systems on A320 fleet.', 'ToDo', 'Medium', 'Avionics', 'A320 Fleet', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '3 days', 25, 22, 4),
('Landing Gear Inspection', 'Routine landing gear inspection required for aircraft CT-123.', 'Open', 'Medium', 'Inspection', 'Aircraft CT-123', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', 23, 21, 4),
('Cabin Pressurization Issue', 'Cabin pressurization system showing abnormal readings on aircraft DT-456.', 'InProgress', 'High', 'Cabin', 'Aircraft DT-456', CURRENT_TIMESTAMP - INTERVAL '6 hours', CURRENT_TIMESTAMP - INTERVAL '3 hours', 24, 22, 4),
('Fuel System Contamination', 'Fuel contamination detected in aircraft ET-789 during routine check.', 'Open', 'Critical', 'Fuel', 'Aircraft ET-789', CURRENT_TIMESTAMP - INTERVAL '8 hours', CURRENT_TIMESTAMP - INTERVAL '8 hours', 25, 21, 4),
('Tire Pressure Monitoring', 'TPMS system showing errors for multiple aircraft on the tarmac.', 'ToDo', 'Medium', 'Tires', 'Multiple Aircraft', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '2 days', 23, 22, 4),
('Electrical System Fault', 'Electrical system fault reported on aircraft FT-234 during flight.', 'Open', 'High', 'Electrical', 'Aircraft FT-234', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', 24, 21, 4),
('Spare Parts Inventory', 'Critical shortage of brake pads for Boeing 737 fleet.', 'ToDo', 'High', 'Inventory', 'Spare Parts Warehouse', CURRENT_TIMESTAMP - INTERVAL '1 week', CURRENT_TIMESTAMP - INTERVAL '1 week', 25, 22, 4),
('Maintenance Log System', 'Maintenance log system not accepting entries for completed inspections.', 'Open', 'Medium', 'System', 'Maintenance Database', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '2 days', 23, 21, 4);

-- ============================================================================
-- INSERT TICKETS FOR GFC (FINANCE) DEPARTMENT
-- ============================================================================
-- Financial and Accounting Issues
INSERT INTO ticket (title, description, status, priority, category, vector, created_at, modified_at, created_by_id, assigned_to_id, project_id) VALUES
('Quarterly Financial Reports', 'Q1 financial reports showing discrepancies in revenue recognition.', 'InProgress', 'High', 'Reports', 'Financial System', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '1 day', 29, 27, 5),
('Vendor Payment System', 'Vendor payment system not processing electronic transfers correctly.', 'Open', 'Critical', 'Payments', 'Payment System', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', 30, 27, 5),
('Budget Allocation Error', 'Budget allocation system showing incorrect figures for department budgets.', 'ToDo', 'Medium', 'Budget', 'Budgeting System', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '3 days', 31, 28, 5),
('Expense Report Audit', 'Expense report system not flagging duplicate submissions properly.', 'Open', 'High', 'Expenses', 'Expense System', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', 29, 28, 5),
('Tax Calculation Issue', 'Tax calculation module producing incorrect amounts for international transactions.', 'InProgress', 'High', 'Tax', 'Tax System', CURRENT_TIMESTAMP - INTERVAL '12 hours', CURRENT_TIMESTAMP - INTERVAL '6 hours', 30, 27, 5),
('Invoice Processing Delay', 'Invoice processing system delayed by 48 hours due to database issues.', 'Open', 'Medium', 'Invoicing', 'Invoice System', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '2 days', 31, 28, 5),
('Payroll Integration Problem', 'Payroll system not integrating properly with time tracking system.', 'ToDo', 'Medium', 'Payroll', 'Payroll System', CURRENT_TIMESTAMP - INTERVAL '4 days', CURRENT_TIMESTAMP - INTERVAL '4 days', 29, 27, 5),
('Financial Audit Preparation', 'Need system access for external auditors for annual financial audit.', 'Open', 'Medium', 'Audit', 'Audit System', CURRENT_TIMESTAMP - INTERVAL '1 week', CURRENT_TIMESTAMP - INTERVAL '1 week', 30, 28, 5),
('Currency Exchange Rates', 'Currency exchange rate updates not syncing with banking systems.', 'InProgress', 'Medium', 'Currency', 'Exchange System', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '12 hours', 31, 27, 5),
('Financial Dashboard Error', 'Executive financial dashboard showing incorrect KPIs and metrics.', 'Open', 'High', 'Dashboard', 'Dashboard System', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '3 days', 29, 28, 5);

-- ============================================================================
-- INSERT TICKETS FOR REF (REFORM) DEPARTMENT
-- ============================================================================
-- Additional department tickets for comprehensive coverage
INSERT INTO ticket (title, description, status, priority, category, vector, created_at, modified_at, created_by_id, assigned_to_id, project_id) VALUES
('System Migration Planning', 'Planning migration from legacy system to new platform. Risk assessment required.', 'ToDo', 'Medium', 'Migration', 'Legacy Systems', CURRENT_TIMESTAMP - INTERVAL '1 week', CURRENT_TIMESTAMP - INTERVAL '1 week', 5, 3, 1),
('Data Quality Audit', 'Data quality issues detected in customer database. Need comprehensive audit.', 'Open', 'High', 'Data Quality', 'Customer Database', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '3 days', 6, 4, 1),
('API Integration Testing', 'New API integration with external vendor failing authentication tests.', 'InProgress', 'Medium', 'Integration', 'External API', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '1 day', 7, 3, 1),
('User Training Session', 'Need to schedule user training for new ticketing system features.', 'ToDo', 'Low', 'Training', 'Training System', CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '5 days', 5, 4, 1),
('System Performance Monitoring', 'Performance monitoring showing increased response times during peak hours.', 'Open', 'Medium', 'Performance', 'Monitoring System', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '2 days', 6, 3, 1);

-- ============================================================================
-- INSERT ADDITIONAL COMMENTS FOR NEW TICKETS
-- ============================================================================
-- Comments for IT Department Tickets
INSERT INTO comment (comment, created_at, created_by_id, ticket_id) VALUES
('Server team investigating hardware failure in main rack.', CURRENT_TIMESTAMP - INTERVAL '1 hour', 3, 31),
('Database administrator checking query optimization.', CURRENT_TIMESTAMP - INTERVAL '12 hours', 4, 32),
('Security team applying emergency patches to web application.', CURRENT_TIMESTAMP - INTERVAL '2 hours', 3, 33),
('Exchange server rebooted, checking email flow.', CURRENT_TIMESTAMP - INTERVAL '3 hours', 4, 34),
('VPN logs show authentication server overload.', CURRENT_TIMESTAMP - INTERVAL '1 day', 3, 35),
('License renewal process initiated with vendor.', CURRENT_TIMESTAMP - INTERVAL '2 days', 4, 36),
('Hardware diagnostics completed, motherboard replacement needed.', CURRENT_TIMESTAMP - INTERVAL '2 hours', 3, 37),
('Switch replacement in progress, network restoration expected in 30 minutes.', CURRENT_TIMESTAMP - INTERVAL '30 minutes', 4, 38),
('Backup system logs indicate storage space issue.', CURRENT_TIMESTAMP - INTERVAL '1 day', 3, 39),
