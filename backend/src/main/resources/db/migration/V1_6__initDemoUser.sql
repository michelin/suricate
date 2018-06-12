-- Demo data
INSERT IGNORE INTO `role` (id, name, description) VALUES(1, 'ROLE_ADMIN', 'Administrator Role');
INSERT IGNORE INTO `role` (id, name, description) VALUES(2, 'ROLE_USER', 'User role');
INSERT IGNORE INTO `user` (id, username, password, auth_mode, firstname, lastname, email) VALUES (99, 'demo', '$2a$11$D2UTH3CVOU91KgRus8VMW.396gUE/L3JDKxDSK7cxWuEpmqUuoxEu	', 'DATABASE', 'Demo', 'Suricate', 'mvincent.it@gmail.com');
INSERT IGNORE INTO `user_role` (user_id, role_id) VALUES (99, 1);
