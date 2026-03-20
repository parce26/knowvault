-- =====================================================
-- KnowVault - V3 Seed Data
-- Default categories, admin user, demo user, tags
-- =====================================================

-- Default categories
INSERT INTO categories (name, description) VALUES
('Human Resources', 'HR policies, manuals and procedures'),
('Technical', 'Technical documentation and guides'),
('Legal', 'Contracts, policies and legal documents'),
('Finance', 'Financial reports and procedures'),
('Operations', 'Operational processes and manuals'),
('General', 'General purpose documents');

-- Default admin user
-- Username: admin
-- Email:    admin@gmail.com
-- Password: admin123
INSERT INTO users (username, email, password_hash, role) VALUES
('admin', 'admin@gmail.com',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh/K',
 'admin');

-- Default regular user
-- Username: demo
-- Email:    demo@gmail.com
-- Password: demo123
INSERT INTO users (username, email, password_hash, role) VALUES
('demo', 'demo@gmail.com',
 '$2a$10$8K1p/a0dCMi.gKFhPHgIje5Q4gNJFNFkSyIBHFyBxM2GDQ4K4jVu.',
 'user');

-- Default tags
INSERT INTO tags (tag_name) VALUES
('policy'),
('manual'),
('guide'),
('report'),
('contract'),
('procedure');