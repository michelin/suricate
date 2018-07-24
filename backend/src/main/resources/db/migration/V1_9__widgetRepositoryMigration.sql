CREATE TABLE `repository` (
  `name` varchar(255) NOT NULL,
  `branch` varchar(255) NOT NULL,
  `url` varchar(255) NOT NULL,
  `enabled` char(1) NOT NULL,
  `type` varchar(255) NOT NULL,
  PRIMARY KEY (`name`)
);