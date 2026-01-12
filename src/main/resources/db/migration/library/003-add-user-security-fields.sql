-- Liquibase formatted SQL
-- changeset library:003-add-user-security-fields

-- Add password and role columns to users table
ALTER TABLE users
ADD COLUMN password VARCHAR(255) NOT NULL DEFAULT '',
ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'USER';

-- Update existing users with default password (bcrypt hash for "password")
-- ВАЖНО: В реальном приложении нужно использовать BCryptPasswordEncoder для генерации хешей
UPDATE users SET password = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', role = 'USER' WHERE email = 'john@example.com';
UPDATE users SET password = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', role = 'LIBRARIAN' WHERE email = 'jane@example.com';

-- Add test users with different roles
INSERT INTO users (email, first_name, last_name, phone, registered_at, password, role) VALUES
('user@example.com', 'User', 'Test', '+1111111111', NOW(), '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'USER'),
('librarian@example.com', 'Librarian', 'Test', '+2222222222', NOW(), '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'LIBRARIAN'),
('admin@example.com', 'Admin', 'Test', '+3333333333', NOW(), '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN');

