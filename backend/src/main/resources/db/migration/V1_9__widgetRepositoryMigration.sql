CREATE TABLE `repository` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `url` varchar(255) DEFAULT NULL,
  `branch` varchar(255) DEFAULT NULL,
  `login` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `enabled` char(1) NOT NULL,
  `local_path` varchar(255) DEFAULT NULL,
  `type` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_REPOSITORY_NAME` (`name`)
);

ALTER TABLE widget ADD `repository_id` bigint(20);
ALTER TABLE widget ADD CONSTRAINT `FK_REPOSITORY_ID_WIDGET` FOREIGN KEY (`repository_id`) REFERENCES `repository` (`id`);