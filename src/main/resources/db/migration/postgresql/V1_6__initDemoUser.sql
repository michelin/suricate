-- Demo data
INSERT INTO role (id, name, description) values (1, 'ROLE_ADMIN', 'Administrator Role');
INSERT INTO role (id, name, description) values (2, 'ROLE_USER', 'User Role');
INSERT INTO users (id, username, password, auth_mode, firstname, lastname, email)
  values (99, 'demo', '$2a$11$D2UTH3CVOU91KgRus8VMW.396gUE/L3JDKxDSK7cxWuEpmqUuoxEu', 'DATABASE', 'Demo', 'Suricate', 'mvincent.it@gmail.com');
INSERT INTO user_role (user_id, role_id) values (99, 1);
