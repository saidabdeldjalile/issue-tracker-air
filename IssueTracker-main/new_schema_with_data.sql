-- ============================================================================
-- COMPLETE DATABASE SCHEMA FOR ISSUE TRACKER - AIR ALGERIE
-- ============================================================================
-- Database: issue_tracker_db
-- Port: 5433
-- ============================================================================

-- ============================================================================
-- 1. DEPARTMENT TABLE
-- ============================================================================
CREATE TABLE IF NOT EXISTS department (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    code VARCHAR(20) UNIQUE,
    description TEXT
);

-- ============================================================================
-- 2. USER TABLE
-- ============================================================================
CREATE TABLE IF NOT EXISTS _user (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    registration_number VARCHAR(50) UNIQUE,
    department_code VARCHAR(20),
    department_id BIGINT REFERENCES department(id)
);

-- ============================================================================
-- 3. PROJECT TABLE
-- ============================================================================
CREATE TABLE IF NOT EXISTS project (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    department_id BIGINT REFERENCES department(id)
);

-- ============================================================================
-- 3.5. UNANSWERED QUESTION TABLE
-- ============================================================================
CREATE TABLE IF NOT EXISTS unanswered_question (
    id BIGSERIAL PRIMARY KEY,
    question TEXT NOT NULL,
    context TEXT,
    user_email VARCHAR(255) NOT NULL,
    suggested_category VARCHAR(255),
    suggested_department VARCHAR(255),
    related_ticket_id BIGINT,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP,
    resolved_by_faq_id BIGINT
);

-- ============================================================================
-- 3.6. FAQ TABLE
-- ============================================================================
CREATE TABLE IF NOT EXISTS faq (
    id BIGSERIAL PRIMARY KEY,
    question VARCHAR(255) NOT NULL,
    answer TEXT NOT NULL,
    category VARCHAR(255),
    department_id BIGINT REFERENCES department(id),
    active BOOLEAN NOT NULL DEFAULT true,
    view_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- FAQ Keywords table (for @ElementCollection)
CREATE TABLE IF NOT EXISTS faq_keywords (
    faq_id BIGINT NOT NULL REFERENCES faq(id) ON DELETE CASCADE,
    keyword VARCHAR(255) NOT NULL,
    PRIMARY KEY (faq_id, keyword)
);

-- ============================================================================
-- 3.7. PASSWORD RESET TOKEN TABLE
-- ============================================================================
CREATE TABLE IF NOT EXISTS password_reset_token (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id INTEGER NOT NULL REFERENCES _user(id),
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN NOT NULL DEFAULT false,
    used_at TIMESTAMP
);

-- ============================================================================
-- 3.8. CHATBOT FEEDBACK TABLE
-- ============================================================================
CREATE TABLE IF NOT EXISTS chatbot_feedback (
    id BIGSERIAL PRIMARY KEY,
    session_id VARCHAR(255),
    user_email VARCHAR(255),
    message TEXT,
    response TEXT,
    rating INTEGER,
    feedback TEXT,
    helpful BOOLEAN,
    created_at TIMESTAMP
);

-- ============================================================================
-- 4. TICKET TABLE
-- ============================================================================
CREATE TABLE IF NOT EXISTS ticket (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(255) NOT NULL,
    priority VARCHAR(255) NOT NULL,
    category VARCHAR(255),
    vector TEXT,
    department_code VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by_id INTEGER REFERENCES _user(id),
    assigned_to_id INTEGER REFERENCES _user(id),
    project_id BIGINT REFERENCES project(id)
);

-- ============================================================================
-- 5. COMMENT TABLE
-- ============================================================================
CREATE TABLE IF NOT EXISTS comment (
    id BIGSERIAL PRIMARY KEY,
    comment TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by_id INTEGER REFERENCES _user(id),
    ticket_id BIGINT REFERENCES ticket(id)
);

-- ============================================================================
-- 6. NOTIFICATION TABLE
-- ============================================================================
CREATE TABLE IF NOT EXISTS notification (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT,
    user_id INTEGER REFERENCES _user(id),
    department_id BIGINT REFERENCES department(id),
    related_entity_id BIGINT,
    is_read BOOLEAN DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- INDEXES
-- ============================================================================
CREATE INDEX idx_ticket_created_by ON ticket(created_by_id);
CREATE INDEX idx_ticket_assigned_to ON ticket(assigned_to_id);
CREATE INDEX idx_ticket_project ON ticket(project_id);
CREATE INDEX idx_ticket_status ON ticket(status);
CREATE INDEX idx_comment_ticket ON comment(ticket_id);
CREATE INDEX idx_user_department ON _user(department_id);
CREATE INDEX idx_notification_user ON notification(user_id);
CREATE INDEX idx_notification_department ON notification(department_id);

-- ============================================================================
-- SEQUENCES
-- ============================================================================
CREATE SEQUENCE IF NOT EXISTS department_id_seq;
CREATE SEQUENCE IF NOT EXISTS _user_id_seq;
CREATE SEQUENCE IF NOT EXISTS project_id_seq;
CREATE SEQUENCE IF NOT EXISTS ticket_id_seq;
CREATE SEQUENCE IF NOT EXISTS comment_id_seq;
CREATE SEQUENCE IF NOT EXISTS notification_id_seq;

-- ============================================================================
-- SAMPLE DATA INSERTION - AIR ALGERIE DEPARTMENTS
-- ============================================================================
-- Password for all users: said (bcrypt hash: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsW/dW6IO2uF3dxOmy)
-- Status: Open, ToDo, InProgress, Done, Closed, Deleted
-- Priority: High, Medium, Low, Critical
-- ============================================================================

-- ============================================================================
-- DEPARTMENTS (Air Algerie)
-- ============================================================================
INSERT INTO department (id, name, code, description) VALUES 
(1, 'DSI', 'DSI', 'Direction des Systemes d''Information - IT Department'),
(2, 'DRH', 'DRH', 'Direction des Ressources Humaines - HR Department'),
(3, 'OPS', 'OPS', 'Operations - Flight Operations Department'),
(4, 'MAINTENANCE', 'MAINT', 'Aircraft Maintenance Department'),
(5, 'GFC', 'GFC', 'Gestion Financiere et Comptable - Finance Department')
ON CONFLICT (id) DO NOTHING;

-- ============================================================================
-- USERS
-- ============================================================================
-- Super Admin
INSERT INTO _user (id, first_name, last_name, email, password, role, registration_number, department_id) VALUES
(1, 'Super', 'Admin', 'admin@airalgerie.dz', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsW/dW6IO2uF3dxOmy', 'ADMIN', 'SUP001', NULL)
ON CONFLICT (id) DO NOTHING;

-- DSI (IT) Department Users
INSERT INTO _user (id, first_name, last_name, email, password, role, registration_number, department_id) VALUES
(2, 'Admin', 'DSI', 'admin.dsi@airalgerie.dz', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsW/dW6IO2uF3dxOmy', 'ADMIN', 'DSI001', 1),
(3, 'Support', 'IT1', 'support.it1@airalgerie.dz', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsW/dW6IO2uF3dxOmy', 'SUPPORT', 'DSI002', 1),
(4, 'Support', 'IT2', 'support.it2@airalgerie.dz', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsW/dW6IO2uF3dxOmy', 'SUPPORT', 'DSI003', 1),
(5, 'Ahmed', 'Benzadi', 'ahmed.benzadi@airalgerie.dz', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsW/dW6IO2uF3dxOmy', 'USER', 'DSI004', 1),
(6, 'Fatima', 'Zahra', 'fatima.zahra@airalgerie.dz', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsW/dW6IO2uF3dxOmy', 'USER', 'DSI005', 1),
(7, 'Mohamed', 'Ali', 'mohamed.ali@airalgerie.dz', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsW/dW6IO2uF3dxOmy', 'USER', 'DSI006', 1)
ON CONFLICT (id) DO NOTHING;

-- DRH (HR) Department Users
INSERT INTO _user (id, first_name, last_name, email, password, role, registration_number, department_id) VALUES
(8, 'Admin', 'DRH', 'admin.drh@airalgerie.dz', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsW/dW6IO2uF3dxOmy', 'ADMIN', 'DRH001', 2),
(9, 'Support', 'HR1', 'support.hr1@airalgerie.dz', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsW/dW6IO2uF3dxOmy', 'SUPPORT', 'DRH002', 2),
(10, 'Support', 'HR2', 'support.hr2@airalgerie.dz', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsW/dW6IO2uF3dxOmy', 'SUPPORT', 'DRH003', 2),
(11, 'Youssef', 'Khaled', 'youssef.khaled@airalgerie.dz', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsW/dW6IO2uF3dxOmy', 'USER', 'DRH004', 2),
(12, 'Leila', 'Mansouri', 'leila.mansouri@airalgerie.dz', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsW/dW6IO2uF3dxOmy', 'USER', 'DRH005', 2),
(13, 'Karim', 'Bouchama', 'karim.bouchama@airalgerie.dz', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsW/dW6IO2uF3dxOmy', 'USER', 'DRH006', 2)
ON CONFLICT (id) DO NOTHING;

-- OPS (Operations) Department Users
INSERT INTO _user (id, first_name, last_name, email, password, role, registration_number, department_id) VALUES
(14, 'Admin', 'OPS', 'admin.ops@airalgerie.dz', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsW/dW6IO2uF3dxOmy', 'ADMIN', 'OPS001', 3),
(15, 'Support', 'OPS1', 'support.ops1@airalgerie.dz', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsW/dW6IO2uF3dxOmy', 'SUPPORT', 'OPS002', 3),
(16, 'Support', 'OPS2', 'support.ops2@airalgerie.dz', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsW/dW6IO2uF3dxOmy', 'SUPPORT', 'OPS003', 3),
(17, 'Nadir', 'Boumediene', 'nadir.boumediene@airalgerie.dz', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsW/dW6IO2uF3dxOmy', 'USER', 'OPS004', 3),
(18, 'Samir', 'Lazizi', 'samir.lazizi@airalgerie.dz', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsW/dW6IO2uF3dxOmy', 'USER', 'OPS005', 3),
(19, 'Rachid', 'Hamadouche', 'rachid.hamadouche@airalgerie.dz', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsW/dW6IO2uF3dxOmy', 'USER', 'OPS006', 3)
ON CONFLICT (id) DO NOTHING;

-- MAINTENANCE Department Users
INSERT INTO _user (id, first_name, last_name, email, password, role, registration_number, department_id) VALUES
(20, 'Admin', 'MAINTENANCE', 'admin.maint@airalgerie.dz', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsW/dW6IO2uF3dxOmy', 'ADMIN', 'MAINT001', 4),
(21, 'Support', 'MAINT1', 'support.maint1@airalgerie.dz', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsW/dW6IO2uF3dxOmy', 'SUPPORT', 'MAINT002', 4),
(22, 'Support', 'MAINT2', 'support.maint2@airalgerie.dz', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsW/dW6IO2uF3dxOmy', 'SUPPORT', 'MAINT003', 4),
(23, 'Hichem', 'Ghanem', 'hichem.ghanem@airalgerie.dz', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsW/dW6IO2uF3dxOmy', 'USER', 'MAINT004', 4),
(24, 'Djamel', 'Mokrani', 'djamel.mokrani@airalgerie.dz', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsW/dW6IO2uF3dxOmy', 'USER', 'MAINT005', 4),
(25, 'Sofiane', 'Belhadj', 'sofiane.belhadj@airalgerie.dz', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsW/dW6IO2uF3dxOmy', 'USER', 'MAINT006', 4)
ON CONFLICT (id) DO NOTHING;

-- GFC (Finance) Department Users
INSERT INTO _user (id, first_name, last_name, email, password, role, registration_number, department_id) VALUES
(26, 'Admin', 'GFC', 'admin.gfc@airalgerie.dz', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsW/dW6IO2uF3dxOmy', 'ADMIN', 'GFC001', 5),
(27, 'Support', 'FIN1', 'support.fin1@airalgerie.dz', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsW/dW6IO2uF3dxOmy', 'SUPPORT', 'GFC002', 5),
(28, 'Support', 'FIN2', 'support.fin2@airalgerie.dz', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsW/dW6IO2uF3dxOmy', 'SUPPORT', 'GFC003', 5),
(29, 'Nadia', 'Said', 'nadia.said@airalgerie.dz', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsW/dW6IO2uF3dxOmy', 'USER', 'GFC004', 5),
(30, 'Rachid', 'Amrani', 'rachid.amrani@airalgerie.dz', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsW/dW6IO2uF3dxOmy', 'USER', 'GFC005', 5),
(31, 'Tarik', 'Mansour', 'tarik.mansour@airalgerie.dz', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.rsW/dW6IO2uF3dxOmy', 'USER', 'GFC006', 5)
ON CONFLICT (id) DO NOTHING;

-- ============================================================================
-- PROJECTS
-- ============================================================================
INSERT INTO project (id, name, department_id) VALUES
(1, 'IT Infrastructure', 1),
(2, 'HR Management System', 2),
(3, 'Flight Operations System', 3),
(4, 'Aircraft Maintenance Tracking', 4),
(5, 'Financial Accounting System', 5)
ON CONFLICT (id) DO NOTHING;

-- ============================================================================
-- TICKETS
-- ============================================================================
-- DSI (IT) Tickets
INSERT INTO ticket (id, title, description, status, priority, category, created_at, modified_at, created_by_id, assigned_to_id, project_id) VALUES
(1, 'Email access issue', 'Cannot access corporate email since this morning. Need immediate assistance.', 'Open', 'High', 'Email', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 5, 3, 1),
(2, 'Printer not working', 'The printer on floor 3 is not printing any documents.', 'InProgress', 'Medium', 'Hardware', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', 6, 3, 1),
(3, 'VPN connection failure', 'Unable to connect to company VPN from home office.', 'Done', 'High', 'Network', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '1 day', 7, 4, 1),
(4, 'Software installation request', 'Need Adobe Creative Suite installed on workstation.', 'ToDo', 'Low', 'Software', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '2 days', 5, 4, 1),
(5, 'Laptop performance issue', 'My laptop is running very slow and frequently freezes.', 'Open', 'Critical', 'Hardware', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 6, NULL, 1),
(6, 'Network outage in Building A', 'No network connectivity in Building A since 8 AM.', 'InProgress', 'Critical', 'Network', CURRENT_TIMESTAMP - INTERVAL '2 hours', CURRENT_TIMESTAMP, 7, 3, 1)
ON CONFLICT (id) DO NOTHING;

-- DRH (HR) Tickets
INSERT INTO ticket (id, title, description, status, priority, category, created_at, modified_at, created_by_id, assigned_to_id, project_id) VALUES
(7, 'Leave request pending', 'Submitted leave request 2 weeks ago but still not approved.', 'Open', 'Medium', 'Leave', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', 11, 9, 2),
(8, 'Payroll discrepancy', 'My salary this month is different from expected. Need clarification.', 'InProgress', 'High', 'Payroll', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '1 day', 12, 9, 2),
(9, 'Employee card not working', 'Cannot access building with my employee card since yesterday.', 'Done', 'High', 'Access', CURRENT_TIMESTAMP - INTERVAL '4 days', CURRENT_TIMESTAMP - INTERVAL '2 days', 13, 10, 2),
(10, 'Training certificate request', 'Need certificate for completed safety training last month.', 'ToDo', 'Low', 'Training', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '3 days', 11, 10, 2),
(11, 'Contact info update', 'Need to update emergency contact information in the system.', 'Open', 'Low', 'Administrative', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 12, NULL, 2),
(12, 'ID card request', 'New employee ID card needed for access to terminals.', 'ToDo', 'Medium', 'Administrative', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', 13, 9, 2)
ON CONFLICT (id) DO NOTHING;

-- OPS (Operations) Tickets
INSERT INTO ticket (id, title, description, status, priority, category, created_at, modified_at, created_by_id, assigned_to_id, project_id) VALUES
(13, 'Flight scheduling error', 'Several flights showing incorrect departure times in the system.', 'Open', 'High', 'Scheduling', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', 17, 15, 3),
(14, 'Crew scheduling conflict', 'Two crew members assigned to the same flight AH-204.', 'InProgress', 'Critical', 'Crew', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP, 18, 15, 3),
(15, 'Fuel booking system down', 'Cannot complete fuel booking for tomorrow flights.', 'Done', 'High', 'Fuel', CURRENT_TIMESTAMP - INTERVAL '4 days', CURRENT_TIMESTAMP - INTERVAL '2 days', 19, 16, 3),
(16, 'Cargo manifest error', 'Cargo loading at Terminal 2 shows wrong weights.', 'ToDo', 'Medium', 'Cargo', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '2 days', 17, 16, 3),
(17, 'Weather system outage', 'Weather update system not working. Affects flight planning.', 'Open', 'Critical', 'System', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 18, NULL, 3),
(18, 'Passenger list discrepancy', 'Passenger manifest does not match check-in records for flight AH-501.', 'InProgress', 'High', 'Passenger', CURRENT_TIMESTAMP - INTERVAL '3 hours', CURRENT_TIMESTAMP, 19, 15, 3)
ON CONFLICT (id) DO NOTHING;

-- MAINTENANCE Tickets
INSERT INTO ticket (id, title, description, status, priority, category, created_at, modified_at, created_by_id, assigned_to_id, project_id) VALUES
(19, 'Engine inspection request', 'Aircraft AT-72 requires scheduled engine inspection.', 'ToDo', 'High', 'Inspection', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', 23, 21, 4),
(20, 'Tool inventory discrepancy', 'Missing tools from maintenance kit. Need replacement.', 'Open', 'Medium', 'Inventory', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '2 days', 24, 21, 4),
(21, 'Landing gear warning', 'Landing gear warning light on aircraft BT-45.', 'InProgress', 'Critical', 'Maintenance', CURRENT_TIMESTAMP - INTERVAL '4 hours', CURRENT_TIMESTAMP, 25, 22, 4),
(22, 'Avionics system update', 'Need software update for avionics systems on fleet.', 'ToDo', 'Low', 'Software', CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '5 days', 23, 22, 4),
(23, 'Spare parts order', 'Running low on brake pads for A320 fleet.', 'Open', 'High', 'Inventory', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 24, NULL, 4),
(24, 'Maintenance log error', 'Cannot update maintenance log in the system.', 'Open', 'Medium', 'System', CURRENT_TIMESTAMP - INTERVAL '6 hours', CURRENT_TIMESTAMP, 25, 21, 4)
ON CONFLICT (id) DO NOTHING;

-- GFC (Finance) Tickets
INSERT INTO ticket (id, title, description, status, priority, category, created_at, modified_at, created_by_id, assigned_to_id, project_id) VALUES
(25, 'Expense report rejected', 'My expense report for last month was rejected without explanation.', 'Open', 'Medium', 'Expenses', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', 29, 27, 5),
(26, 'Invoice payment delay', 'Supplier invoice payment is overdue by 30 days.', 'InProgress', 'High', 'Payment', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '1 day', 30, 27, 5),
(27, 'Budget approval needed', 'Need approval for Q4 budget allocation for new equipment.', 'Done', 'Medium', 'Budget', CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '3 days', 31, 28, 5),
(28, 'Purchase request', 'Need to purchase new accounting software license.', 'ToDo', 'Low', 'Purchase', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '3 days', 29, 28, 5),
(29, 'Financial report error', 'The monthly financial report shows incorrect figures for revenue.', 'Open', 'Critical', 'Reports', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 30, NULL, 5),
(30, 'Travel advance request', 'Need travel advance for upcoming business trip to Paris.', 'Open', 'Medium', 'Expenses', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day', 31, 27, 5)
ON CONFLICT (id) DO NOTHING;

-- ============================================================================
-- COMMENTS
-- ============================================================================
INSERT INTO comment (id, comment, created_at, created_by_id, ticket_id) VALUES
(1, 'I tried restarting my computer but still cannot access email.', CURRENT_TIMESTAMP - INTERVAL '20 hours', 5, 1),
(2, 'IT support has been notified. Looking into the server configuration.', CURRENT_TIMESTAMP - INTERVAL '18 hours', 3, 1),
(3, 'Printer maintenance team is on the way to fix the issue.', CURRENT_TIMESTAMP - INTERVAL '20 hours', 3, 2),
(4, 'Thank you for the quick response!', CURRENT_TIMESTAMP - INTERVAL '19 hours', 6, 2),
(5, 'VPN settings have been reset. Please try connecting again.', CURRENT_TIMESTAMP - INTERVAL '2 days', 4, 3),
(6, 'Works now! Thank you so much!', CURRENT_TIMESTAMP - INTERVAL '2 days', 7, 3),
(7, 'Software installation approved. Will be done by tomorrow morning.', CURRENT_TIMESTAMP - INTERVAL '1 day', 4, 4),
(8, 'Laptop needs hardware diagnostics. Will schedule repair.', CURRENT_TIMESTAMP - INTERVAL '1 hour', 3, 5),
(9, 'Network switch replaced. Connectivity restored.', CURRENT_TIMESTAMP - INTERVAL '30 minutes', 3, 6),
(10, 'I checked your leave request. It needs director approval.', CURRENT_TIMESTAMP - INTERVAL '20 hours', 9, 7),
(11, 'Thank you for checking. Can you expedite this please?', CURRENT_TIMESTAMP - INTERVAL '19 hours', 11, 7),
(12, 'Found the discrepancy in overtime calculation. Correcting now.', CURRENT_TIMESTAMP - INTERVAL '1 day', 9, 8),
(13, 'Access card has been reactivated. Please test it.', CURRENT_TIMESTAMP - INTERVAL '3 days', 10, 9),
(14, 'Certificate has been generated and sent to your email.', CURRENT_TIMESTAMP - INTERVAL '2 days', 10, 10),
(15, 'Updated your contact information in the system.', CURRENT_TIMESTAMP - INTERVAL '1 hour', 9, 11),
(16, 'New ID card will be ready tomorrow. Please collect from security.', CURRENT_TIMESTAMP - INTERVAL '2 hours', 9, 12),
(17, 'Flight schedule has been corrected in the system.', CURRENT_TIMESTAMP - INTERVAL '20 hours', 15, 13),
(18, 'Crew scheduling has been adjusted. Both crew members reassigned.', CURRENT_TIMESTAMP - INTERVAL '12 hours', 15, 14),
(19, 'Fuel booking system is fixed now. Please try again.', CURRENT_TIMESTAMP - INTERVAL '3 days', 16, 15),
(20, 'Cargo manifest corrected with proper weights.', CURRENT_TIMESTAMP - INTERVAL '1 day', 16, 16),
(21, 'Weather system service restored. Should be working now.', CURRENT_TIMESTAMP - INTERVAL '4 hours', 15, 17),
(22, 'Passenger manifest updated. Please verify at check-in.', CURRENT_TIMESTAMP - INTERVAL '2 hours', 15, 18),
(23, 'Engine inspection scheduled for tomorrow at 6 AM.', CURRENT_TIMESTAMP - INTERVAL '20 hours', 21, 19),
(24, 'New tools ordered. Will arrive in 2 days.', CURRENT_TIMESTAMP - INTERVAL '1 day', 21, 20),
(25, 'Landing gear issue resolved after inspection.', CURRENT_TIMESTAMP - INTERVAL '2 hours', 22, 21),
(26, 'Avionics update scheduled for next maintenance window.', CURRENT_TIMESTAMP - INTERVAL '4 days', 22, 22),
(27, 'Brake pads order placed. Expected delivery next week.', CURRENT_TIMESTAMP - INTERVAL '6 hours', 21, 23),
(28, 'Maintenance log issue fixed. Please try again.', CURRENT_TIMESTAMP - INTERVAL '3 hours', 21, 24),
(29, 'Expense report was missing receipts. Please resubmit.', CURRENT_TIMESTAMP - INTERVAL '20 hours', 27, 25),
(30, 'Payment has been processed and sent to supplier.', CURRENT_TIMESTAMP - INTERVAL '1 day', 27, 26),
(31, 'Budget approved. You can proceed with the purchase.', CURRENT_TIMESTAMP - INTERVAL '4 days', 28, 27),
(32, 'Looking into the financial report error.', CURRENT_TIMESTAMP - INTERVAL '2 hours', 27, 29),
(33, 'Travel advance approved. Will be processed within 48 hours.', CURRENT_TIMESTAMP - INTERVAL '6 hours', 27, 30)
ON CONFLICT (id) DO NOTHING;

-- ============================================================================
-- NOTIFICATIONS
-- ============================================================================
INSERT INTO notification (id, type, title, message, user_id, department_id, related_entity_id, is_read, created_at) VALUES
(1, 'TICKET_CREATED', 'Nouveau ticket cree', 'Le ticket a ete cree', 5, 1, 1, true, CURRENT_TIMESTAMP - INTERVAL '23 hours'),
(2, 'TICKET_CREATED', 'Nouveau ticket cree', 'Le ticket a ete cree', 6, 1, 1, true, CURRENT_TIMESTAMP - INTERVAL '23 hours'),
(3, 'TICKET_ASSIGNED', 'Ticket vous a ete assigne', 'Le ticket vous a ete assigne', 3, 1, 1, true, CURRENT_TIMESTAMP - INTERVAL '22 hours'),
(4, 'TICKET_STATUS_CHANGED', 'Statut du ticket modifie', 'Le ticket a ete deplace', 6, 1, 2, true, CURRENT_TIMESTAMP - INTERVAL '1 day'),
(5, 'TICKET_STATUS_CHANGED', 'Statut du ticket modifie', 'Le ticket a ete deplace', 7, 1, 3, true, CURRENT_TIMESTAMP - INTERVAL '1 day'),
(6, 'TICKET_CREATED', 'Nouveau ticket cree', 'Le ticket a ete cree', 11, 2, 7, true, CURRENT_TIMESTAMP - INTERVAL '1 day'),
(7, 'TICKET_CREATED', 'Nouveau ticket cree', 'Le ticket a ete cree', 12, 2, 7, true, CURRENT_TIMESTAMP - INTERVAL '1 day'),
(8, 'TICKET_ASSIGNED', 'Ticket vous a ete assigne', 'Le ticket vous a ete assigne', 9, 2, 7, true, CURRENT_TIMESTAMP - INTERVAL '1 day'),
(9, 'COMMENT_ADDED', 'Nouveau commentaire', 'Le support a ajoute un commentaire', 5, 1, 1, true, CURRENT_TIMESTAMP - INTERVAL '18 hours'),
(10, 'COMMENT_ADDED', 'Nouveau commentaire', 'Nouveau commentaire sur le ticket', 6, 1, 2, true, CURRENT_TIMESTAMP - INTERVAL '19 hours'),
(11, 'TICKET_CREATED', 'Nouveau ticket cree', 'Le ticket a ete cree', 17, 3, 13, false, CURRENT_TIMESTAMP - INTERVAL '1 day'),
(12, 'TICKET_CREATED', 'Nouveau ticket cree', 'Le ticket a ete cree', 18, 3, 13, false, CURRENT_TIMESTAMP - INTERVAL '1 day'),
(13, 'TICKET_ASSIGNED', 'Ticket vous a ete assigne', 'Le ticket vous a ete assigne', 15, 3, 13, false, CURRENT_TIMESTAMP - INTERVAL '1 day'),
(14, 'COMMENT_ADDED', 'Nouveau commentaire', 'Un utilisateur a ajoute un commentaire', 15, 3, 13, false, CURRENT_TIMESTAMP - INTERVAL '20 hours'),
(15, 'TICKET_CREATED', 'Nouveau ticket cree', 'Le ticket a ete cree', 23, 4, 19, false, CURRENT_TIMESTAMP - INTERVAL '1 day'),
(16, 'TICKET_ASSIGNED', 'Ticket vous a ete assigne', 'Le ticket vous a ete assigne', 21, 4, 19, false, CURRENT_TIMESTAMP - INTERVAL '1 day'),
(17, 'COMMENT_ADDED', 'Nouveau commentaire', 'Le support a ajoute un commentaire', 21, 4, 19, false, CURRENT_TIMESTAMP - INTERVAL '18 hours'),
(18, 'TICKET_STATUS_CHANGED', 'Statut du ticket modifie', 'Le ticket a ete deplace', 25, 4, 21, false, CURRENT_TIMESTAMP),
(19, 'TICKET_CREATED', 'Nouveau ticket cree', 'Le ticket a ete cree', 29, 5, 25, false, CURRENT_TIMESTAMP - INTERVAL '1 day'),
(20, 'TICKET_ASSIGNED', 'Ticket vous a ete assigne', 'Le ticket vous a ete assigne', 27, 5, 25, false, CURRENT_TIMESTAMP - INTERVAL '1 day'),
(21, 'COMMENT_ADDED', 'Nouveau commentaire', 'Nouveau commentaire sur le ticket', 27, 5, 25, false, CURRENT_TIMESTAMP - INTERVAL '19 hours'),
(22, 'TICKET_CREATED', 'Nouveau ticket cree', 'Le ticket a ete cree', 30, 5, 29, false, CURRENT_TIMESTAMP),
(23, 'TICKET_CREATED', 'Nouveau ticket cree', 'Le ticket a ete cree', 31, 5, 29, false, CURRENT_TIMESTAMP),
(24, 'COMMENT_ADDED', 'Nouveau commentaire', 'Un utilisateur a ajoute un commentaire', 27, 5, 29, false, CURRENT_TIMESTAMP - INTERVAL '2 hours')
ON CONFLICT (id) DO NOTHING;

-- ============================================================================
-- FIX SEQUENCES
-- ============================================================================
SELECT setval('department_id_seq', (SELECT MAX(id) FROM department));
SELECT setval('_user_id_seq', (SELECT MAX(id) FROM _user));
SELECT setval('project_id_seq', (SELECT MAX(id) FROM project));
SELECT setval('ticket_id_seq', (SELECT MAX(id) FROM ticket));
SELECT setval('comment_id_seq', (SELECT MAX(id) FROM comment));
SELECT setval('notification_id_seq', (SELECT MAX(id) FROM notification));

-- ============================================================================
-- VERIFICATION
-- ============================================================================
SELECT 'Departments:' as info, COUNT(*) as count FROM department
UNION ALL
SELECT 'Users:', COUNT(*) FROM _user
UNION ALL
SELECT 'Projects:', COUNT(*) FROM project
UNION ALL
SELECT 'Tickets:', COUNT(*) FROM ticket
UNION ALL
SELECT 'Comments:', COUNT(*) FROM comment
UNION ALL
SELECT 'Notifications:', COUNT(*) FROM notification;

