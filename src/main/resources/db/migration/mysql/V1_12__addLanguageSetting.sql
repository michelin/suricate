INSERT INTO setting (id, constrained, data_type, type, description) values (2, 'Y', 'SELECT', 'LANGUAGE', 'Language');

INSERT INTO allowed_setting_value (id, title, value, is_default, setting_id) values (3, 'English', 'en', 'Y', 2);
INSERT INTO allowed_setting_value (id, title, value, is_default, setting_id) values (4, 'Français', 'fr', 'N', 2);

/* Insert the default setting for every users */
INSERT INTO user_setting (setting_id, allowed_setting_value_id, user_id)
  SELECT s.id, asv.id, u.id
  FROM setting s, allowed_setting_value asv, users u
  WHERE s.id = asv.setting_id
        and s.id = 2
        and asv.is_default = 'Y'
  ORDER BY u.id;