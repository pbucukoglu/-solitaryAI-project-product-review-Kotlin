-- Fix user roles for existing users
INSERT INTO user_roles (user_id, role) VALUES 
(3, 'USER'),
(6, 'USER'),
(7, 'ADMIN'),
(8, 'USER')
ON CONFLICT (user_id, role) DO NOTHING;
