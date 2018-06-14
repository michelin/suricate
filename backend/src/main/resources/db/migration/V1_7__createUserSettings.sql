/* ***************************************** */
/*          SETTING PART                     */
/* ***************************************** */
CREATE TABLE `setting` (
  `id`          bigint(20)   NOT NULL AUTO_INCREMENT,
  `constrained` char(1)      NOT NULL,
  `data_type`   varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
);

INSERT INTO setting (id, constrained, data_type, description) values (1, 'Y', 'SELECT', 'Template');


/* *************************************************** */
/*             ALLOWED SETTING VALUE PART              */
/* *************************************************** */
CREATE TABLE `allowed_setting_value` (
  `id`         bigint(20)   NOT NULL AUTO_INCREMENT,
  `title`      varchar(255) NOT NULL,
  `value`      varchar(255) NOT NULL,
  `is_default` char(1)      NOT NULL,
  `setting_id` bigint(20)   NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FK_SETTING_ID_ALLOWED_SETTING_VALUE` FOREIGN KEY (`setting_id`) REFERENCES `setting` (`id`)
);

INSERT INTO allowed_setting_value (id, title, value, is_default, setting_id) values (1, 'Default', 'DEFAULT', 'Y', 1);
INSERT INTO allowed_setting_value (id, title, value, is_default, setting_id) values (2, 'Dark', 'DARK', 'N', 1);


/* *************************************************** */
/*             USER SETTING VALUE PART                 */
/* *************************************************** */
CREATE TABLE `user_setting` (
  `id`                       bigint(20) NOT NULL AUTO_INCREMENT,
  `unconstrained_value`      varchar(255)        DEFAULT NULL,
  `setting_id`               bigint(20) NOT NULL,
  `allowed_setting_value_id` bigint(20)          DEFAULT NULL,
  `user_id`                  bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FK_ALLOWED_SETTING_VALUE_ID_USER_SETTING` FOREIGN KEY (`allowed_setting_value_id`) REFERENCES `allowed_setting_value` (`id`),
  CONSTRAINT `FK_USER_ID_USER_SETTING` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_SETTING_ID_USER_SETTING` FOREIGN KEY (`setting_id`) REFERENCES `setting` (`id`)
);

/* Insert the default setting for every users */
INSERT IGNORE INTO user_setting (setting_id, allowed_setting_value_id, user_id)
  SELECT
    s.id,
    asv.id,
    u.id
  FROM setting s, allowed_setting_value asv, user u
  WHERE s.id = 1
        and asv.id = 1
  ORDER BY u.id;