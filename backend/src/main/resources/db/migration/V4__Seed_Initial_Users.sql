-- Insert initial admin user
INSERT INTO users (email, password, full_name) VALUES 
('admin@example.com', '$2a$10$YourBCryptHashedPasswordHere', 'Admin User');

-- Insert admin role for admin user
INSERT INTO user_roles (user_id, role) VALUES 
(1, 'ADMIN');

-- Insert initial regular user
INSERT INTO users (email, password, full_name) VALUES 
('user@example.com', '$2a$10$YourBCryptHashedPasswordHere', 'Regular User');

-- Insert user role for regular user
INSERT INTO user_roles (user_id, role) VALUES 
(2, 'USER');
