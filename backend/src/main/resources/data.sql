-- Demo data
INSERT INTO `role` (id, name, description) VALUES(1, 'ROLE_ADMIN', 'Administrator Role');
INSERT INTO `role` (id, name, description) VALUES(2, 'ROLE_USER', 'User role');
INSERT INTO `user` (id, username, password, auth_mode, firstname, lastname, email) VALUES (99, 'demo', '$2a$11$D2UTH3CVOU91KgRus8VMW.396gUE/L3JDKxDSK7cxWuEpmqUuoxEu	', 'DATABASE', 'Demo', 'Suricate', 'mvincent.it@gmail.com');
INSERT INTO `user_role` (user_id, role_id) VALUES (99, 1);

-- Configuration
INSERT INTO `configuration`(`config_key`, `config_value`, `config_export`) VALUES('token.delay.minute', '1440', false);
