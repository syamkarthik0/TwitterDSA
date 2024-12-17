-- Check if user doesn't exist before inserting
INSERT INTO users (username, password, role)
SELECT 'shashank@shashank.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'USER'
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE username = 'shashank@shashank.com'
);
