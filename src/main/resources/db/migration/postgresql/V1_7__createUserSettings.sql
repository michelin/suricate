/* ***************************************** */
/*          SETTING PART                     */
/* ***************************************** */
CREATE TABLE setting (
  id          bigserial    NOT NULL,
  constrained char         NOT NULL,
  data_type   varchar(255) NOT NULL,
  type        varchar(255) NOT NULL,
  description varchar(255) NOT NULL,
  CONSTRAINT PK_SETTING_ID PRIMARY KEY (id)
);
INSERT INTO setting (id, constrained, data_type, type, description) values (1, 'Y', 'SELECT', 'TEMPLATE', 'Template');
INSERT INTO setting (id, constrained, data_type, type, description) values (2, 'Y', 'SELECT', 'LANGUAGE', 'Language');


/* *************************************************** */
/*             ALLOWED SETTING VALUE PART              */
/* *************************************************** */
CREATE TABLE allowed_setting_value (
  id         bigserial    NOT NULL,
  title      varchar(255) NOT NULL,
  value      varchar(255) NOT NULL,
  is_default char         NOT NULL,
  setting_id bigint       NOT NULL,
  CONSTRAINT PK_ALLOWED_SETTING_VALUE_ID          PRIMARY KEY (id),
  CONSTRAINT FK_ALLOWED_SETTING_VALUE_SETTING_ID  FOREIGN KEY (setting_id) REFERENCES setting (id)
);
INSERT INTO allowed_setting_value (id, title, value, is_default, setting_id) values (1, 'Default', 'default-theme', 'Y', 1);
INSERT INTO allowed_setting_value (id, title, value, is_default, setting_id) values (2, 'Dark', 'dark-theme', 'N', 1);
INSERT INTO allowed_setting_value (id, title, value, is_default, setting_id) values (3, 'English', 'en', 'Y', 2);
INSERT INTO allowed_setting_value (id, title, value, is_default, setting_id) values (4, 'Français', 'fr', 'N', 2);


/* *************************************************** */
/*             USER SETTING VALUE PART                 */
/* *************************************************** */
CREATE TABLE user_setting (
  id                       bigserial      NOT NULL,
  unconstrained_value      varchar(255),
  setting_id               bigint         NOT NULL,
  allowed_setting_value_id bigint,
  user_id                  bigint         NOT NULL,
  CONSTRAINT PK_USER_SETTING_ID                       PRIMARY KEY (id),
  CONSTRAINT FK_USER_SETTING_ALLOWED_SETTING_VALUE_ID FOREIGN KEY (allowed_setting_value_id)  REFERENCES allowed_setting_value (id),
  CONSTRAINT FK_USER_SETTING_USER_ID                  FOREIGN KEY (user_id)                   REFERENCES users (id),
  CONSTRAINT FK_USER_SETTING_SETTING_ID               FOREIGN KEY (setting_id)                REFERENCES setting (id)
);

/* Insert the default setting for every users */
INSERT INTO user_setting (setting_id, allowed_setting_value_id, user_id)
  SELECT s.id, asv.id, u.id
  FROM setting s, allowed_setting_value asv, users u
  WHERE s.id = asv.setting_id and asv.is_default = 'Y'
  ORDER BY u.id;