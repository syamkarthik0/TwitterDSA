-- Users initialization
-- First default user
-- Username: test@test.com
-- Password: test123
INSERT INTO users (username, password, role)
SELECT 'test@test.com', '$2a$10$xXG12g53IPX.K4/HN7OGjez9743qlhgbvSQNvt6Sc5dMiayzmrlbW', 'USER'
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE username = 'test@test.com'
);

-- Second default user
-- Username: shashank@shashank.com
-- Password: shashank
INSERT INTO users (username, password, role)
SELECT 'shashank@shashank.com', '$2a$10$QDi2AEl1zhMcHNL7UD2ACOz6u0geaRyO8eZnHDLFl.4lDHhgrKNa2', 'USER'
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE username = 'shashank@shashank.com'
);

-- Create follow relationships
INSERT INTO followers (follower_id, following_id, follow_date)
SELECT f.id, t.id, CURRENT_TIMESTAMP
FROM users f, users t
WHERE f.username = 'test@test.com' 
AND t.username = 'shashank@shashank.com'
AND NOT EXISTS (
    SELECT 1 FROM followers 
    WHERE follower_id = f.id AND following_id = t.id
);

INSERT INTO followers (follower_id, following_id, follow_date)
SELECT f.id, t.id, CURRENT_TIMESTAMP
FROM users f, users t
WHERE f.username = 'shashank@shashank.com' 
AND t.username = 'test@test.com'
AND NOT EXISTS (
    SELECT 1 FROM followers 
    WHERE follower_id = f.id AND following_id = t.id
);

-- Create sample tweets
INSERT INTO tweet (user_id, content, timestamp)
SELECT u.id, 'Hello Twitter! This is my first tweet!', CURRENT_TIMESTAMP
FROM users u 
WHERE u.username = 'test@test.com' 
AND NOT EXISTS (
    SELECT 1 FROM tweet t 
    WHERE t.user_id = u.id 
    AND t.content = 'Hello Twitter! This is my first tweet!'
);

INSERT INTO tweet (user_id, content, timestamp)
SELECT u.id, 'Hey everyone! Excited to be here!', CURRENT_TIMESTAMP
FROM users u 
WHERE u.username = 'shashank@shashank.com' 
AND NOT EXISTS (
    SELECT 1 FROM tweet t 
    WHERE t.user_id = u.id 
    AND t.content = 'Hey everyone! Excited to be here!'
);

INSERT INTO tweet (user_id, content, timestamp)
SELECT u.id, 'Just learned about Spring Boot! It''s amazing! #coding #java', CURRENT_TIMESTAMP
FROM users u 
WHERE u.username = 'test@test.com' 
AND NOT EXISTS (
    SELECT 1 FROM tweet t 
    WHERE t.user_id = u.id 
    AND t.content = 'Just learned about Spring Boot! It''s amazing! #coding #java'
);

INSERT INTO tweet (user_id, content, timestamp)
SELECT u.id, 'Hello everyone! Excited to join this platform!', CURRENT_TIMESTAMP
FROM users u 
WHERE u.username = 'shashank@shashank.com' 
AND NOT EXISTS (
    SELECT 1 FROM tweet t 
    WHERE t.user_id = u.id 
    AND t.content = 'Hello everyone! Excited to join this platform!'
);

INSERT INTO tweet (user_id, content, timestamp)
SELECT u.id, 'Working on a cool new project with React and Spring Boot!', CURRENT_TIMESTAMP
FROM users u 
WHERE u.username = 'shashank@shashank.com' 
AND NOT EXISTS (
    SELECT 1 FROM tweet t 
    WHERE t.user_id = u.id 
    AND t.content = 'Working on a cool new project with React and Spring Boot!'
);

-- Initialize user feeds for existing tweets
INSERT INTO user_feeds (user_id, tweet_id, created_at)
SELECT f.follower_id, t.id, t.timestamp
FROM followers f
JOIN tweet t ON t.user_id = f.following_id
WHERE NOT EXISTS (
    SELECT 1 FROM user_feeds uf 
    WHERE uf.user_id = f.follower_id 
    AND uf.tweet_id = t.id
);

-- Add tweets to authors' own feeds
INSERT INTO user_feeds (user_id, tweet_id, created_at)
SELECT t.user_id, t.id, t.timestamp
FROM tweet t
WHERE NOT EXISTS (
    SELECT 1 FROM user_feeds uf 
    WHERE uf.user_id = t.user_id 
    AND uf.tweet_id = t.id
);
