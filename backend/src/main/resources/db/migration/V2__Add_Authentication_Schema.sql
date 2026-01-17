-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

-- Create user_roles table
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role)
);

-- Update reviews table to reference users instead of device_id
ALTER TABLE reviews 
ADD COLUMN user_id BIGINT,
ADD CONSTRAINT fk_reviews_user 
FOREIGN KEY (user_id) REFERENCES users(id);

-- Migrate existing reviews to user-based system (optional - for existing data)
-- This creates a default user for existing reviews
INSERT INTO users (email, password, first_name, last_name) 
VALUES ('migrated@productreview.com', '$2a$10$dummy.hash.for.migration', 'Migrated', 'User')
ON CONFLICT (email) DO NOTHING;

-- Update existing reviews to point to migrated user
UPDATE reviews 
SET user_id = (SELECT id FROM users WHERE email = 'migrated@productreview.com')
WHERE user_id IS NULL;

-- Remove old columns (after migration is complete)
-- ALTER TABLE reviews DROP COLUMN reviewer_name;
-- ALTER TABLE reviews DROP COLUMN device_id;

-- Update review_helpful_votes table
ALTER TABLE review_helpful_votes 
ADD COLUMN user_id BIGINT,
ADD CONSTRAINT fk_review_votes_user 
FOREIGN KEY (user_id) REFERENCES users(id);

-- Migrate existing helpful votes
UPDATE review_helpful_votes 
SET user_id = (SELECT id FROM users WHERE email = 'migrated@productreview.com')
WHERE user_id IS NULL;

-- Remove old device_id column (after migration)
-- ALTER TABLE review_helpful_votes DROP COLUMN device_id;

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_reviews_user_id ON reviews(user_id);
CREATE INDEX IF NOT EXISTS idx_review_votes_user_id ON review_helpful_votes(user_id);

-- Insert default users with pre-computed hashed passwords
INSERT INTO users (email, password, first_name, last_name) VALUES
('admin@productreview.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'Admin', 'User'),
('user@productreview.com', '$2a$10$9lKcv1JQK4XHKCnFV5Vv/.p4hjxqZqVzXq9w8Q7Z8N9E7Z6Q5E5W', 'Regular', 'User')
ON CONFLICT (email) DO NOTHING;

-- Assign roles
INSERT INTO user_roles (user_id, role) 
SELECT id, 'ADMIN' FROM users WHERE email = 'admin@productreview.com'
ON CONFLICT DO NOTHING;

INSERT INTO user_roles (user_id, role) 
SELECT id, 'USER' FROM users WHERE email IN ('admin@productreview.com', 'user@productreview.com')
ON CONFLICT DO NOTHING;
