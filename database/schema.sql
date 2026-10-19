-- College Maintenance System: database schema
-- Safe to run more than once (it never deletes existing data).

CREATE DATABASE IF NOT EXISTS college_maintenance
    CHARACTER SET utf8mb4;

USE college_maintenance;

-- ---------- 1. USERS ----------
CREATE TABLE IF NOT EXISTS users (
    user_id       INT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    email         VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          ENUM('STUDENT','FACULTY','HOD','MAINTENANCE_STAFF') NOT NULL,
    department    VARCHAR(50) NULL,
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ---------- 2. MAINTENANCE REQUESTS ----------
CREATE TABLE IF NOT EXISTS maintenance_requests (
    request_id   INT AUTO_INCREMENT PRIMARY KEY,
    submitted_by INT NOT NULL,
    department   VARCHAR(50) NOT NULL,
    room         VARCHAR(20) NOT NULL,
    category     ENUM('ELECTRICAL','PLUMBING','FURNITURE','EQUIPMENT','OTHER') NOT NULL,
    description  TEXT NULL,
    status       ENUM('SUBMITTED','APPROVED','REJECTED','ASSIGNED','IN_PROGRESS','COMPLETED')
                 NOT NULL DEFAULT 'SUBMITTED',
    assigned_to  INT NULL,
    created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                 ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (submitted_by) REFERENCES users(user_id),
    FOREIGN KEY (assigned_to)  REFERENCES users(user_id)
) ENGINE=InnoDB;

-- ---------- 3. REQUEST HISTORY ----------
CREATE TABLE IF NOT EXISTS request_history (
    history_id INT AUTO_INCREMENT PRIMARY KEY,
    request_id INT NOT NULL,
    old_status VARCHAR(20) NULL,
    new_status VARCHAR(20) NOT NULL,
    changed_by INT NOT NULL,
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    remarks    VARCHAR(255) NULL,
    FOREIGN KEY (request_id) REFERENCES maintenance_requests(request_id)
        ON DELETE CASCADE,
    FOREIGN KEY (changed_by) REFERENCES users(user_id)
) ENGINE=InnoDB;

-- ---------- 4. SAMPLE USERS (for testing only) ----------
-- password_hash is a placeholder, NOT a real password or hash.
-- Real login/hashing is a later task for the service layer.
INSERT IGNORE INTO users (name, email, password_hash, role, department) VALUES
('Rahul', 'rahulcollege@gmail.com', 'PLACEHOLDER', 'STUDENT', 'CSE'),
('Sam',   'sam@college.edu',        'PLACEHOLDER', 'FACULTY', 'CSE'),
('Meera', 'meera@college.edu',      'PLACEHOLDER', 'HOD', 'CSE'),
('Arun',  'arun@college.edu',       'PLACEHOLDER', 'MAINTENANCE_STAFF', NULL);