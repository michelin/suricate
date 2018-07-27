CREATE TABLE `repository` (
  `name` varchar(255) NOT NULL,
  `url` varchar(255) DEFAULT NULL,
  `branch` varchar(255) DEFAULT NULL,
  `login` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `enabled` char(1) NOT NULL,
  `local_path` varchar(255) DEFAULT NULL,
  `type` varchar(255) NOT NULL,
  PRIMARY KEY (`name`)
);

ALTER TABLE widget ADD `repository_name` varchar(255);
ALTER TABLE widget ADD CONSTRAINT `FK_REPOSITORY_NAME_WIDGET` FOREIGN KEY (`repository_name`) REFERENCES `repository` (`name`);