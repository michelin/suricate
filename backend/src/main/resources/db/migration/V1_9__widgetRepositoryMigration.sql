CREATE TABLE `repository` (
  `name` varchar(255) NOT NULL,
  `url` varchar(255) NOT NULL,
  `branch` varchar(255) NOT NULL,
  `login` varchar(255),
  `password` varchar(255),
  `enabled` char(1) NOT NULL,
  PRIMARY KEY (`name`)
);

ALTER TABLE widget ADD `repository_name` varchar(255);
ALTER TABLE widget ADD CONSTRAINT `FK_REPOSITORY_NAME_WIDGET` FOREIGN KEY (`repository_name`) REFERENCES `repository` (`name`);