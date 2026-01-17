-- Check all users and their enabled status
SELECT id, email, role, enabled FROM users ORDER BY id;

-- Check if test users exist and are enabled
SELECT id, email, role, enabled FROM users 
WHERE email IN ('test@example.com', 'user@productreview.com', 'migrated@productreview.com');

-- Update any disabled regular users to enabled
UPDATE users 
SET enabled = true 
WHERE role = 'User' AND (enabled IS NULL OR enabled = false);

-- Verify the updates
SELECT id, email, role, enabled FROM users 
WHERE role = 'User' 
ORDER BY id;
