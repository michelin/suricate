INSERT INTO `role`(id, name, description) VALUES(1, 'ROLE_ADMIN', 'Administrator Role');
INSERT INTO `role`(id, name, description) VALUES(2, 'ROLE_USER', 'User role');

-- Configuration
INSERT INTO `configuration`(`config_key`, `config_value`, `config_export`) VALUES('token.delay.minute', '1440', false);
