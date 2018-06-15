-- Demo data
INSERT INTO `role` (id, name, description)
  SELECT 1, 'ROLE_ADMIN', 'Administrator Role'
  FROM dual
  WHERE not exists(select * from role where id = 1);

INSERT INTO `role` (id, name, description)
  SELECT 2, 'ROLE_USER', 'User Role'
  FROM dual
  WHERE not exists(SELECT * FROM role WHERE id = 2);

INSERT INTO `user` (id, username, password, auth_mode, firstname, lastname, email)
  SELECT 99, 'demo', '$2a$11$D2UTH3CVOU91KgRus8VMW.396gUE/L3JDKxDSK7cxWuEpmqUuoxEu', 'DATABASE', 'Demo', 'Suricate', 'mvincent.it@gmail.com'
  FROM dual
  WHERE not exists(SELECT * FROM user WHERE id = 99);

INSERT INTO `user_role` (user_id, role_id)
  SELECT 99, 1
  FROM dual
  WHERE not exists(SELECT * FROM user_role WHERE user_id = 99 and role_id = 1);
