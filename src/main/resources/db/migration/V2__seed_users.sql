-- Demo accounts for a fresh install. All four share the password: Password123!
-- (BCrypt hash below was generated with the same encoder auth-service uses at runtime.)
INSERT IGNORE INTO users (username, email, password, cin, enabled) VALUES
    ('admin', 'admin@eqdom.ma', '$2a$10$LR/CxAXrjU0PNxAnfrXdUuqFQiauT0nzuekcXQmJwMxJ.D14kGJ56', NULL, TRUE),
    ('agent1', 'agent1@eqdom.ma', '$2a$10$LR/CxAXrjU0PNxAnfrXdUuqFQiauT0nzuekcXQmJwMxJ.D14kGJ56', NULL, TRUE),
    ('responsable1', 'responsable1@eqdom.ma', '$2a$10$LR/CxAXrjU0PNxAnfrXdUuqFQiauT0nzuekcXQmJwMxJ.D14kGJ56', NULL, TRUE),
    ('client1', 'client1@eqdom.ma', '$2a$10$LR/CxAXrjU0PNxAnfrXdUuqFQiauT0nzuekcXQmJwMxJ.D14kGJ56', 'AB123456', TRUE);

INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.name = 'ADMIN' WHERE u.username = 'admin';

INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.name = 'AGENT' WHERE u.username = 'agent1';

INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.name = 'RESPONSABLE' WHERE u.username = 'responsable1';

INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.name = 'CLIENT' WHERE u.username = 'client1';
