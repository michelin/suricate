ALTER TABLE `configuration` ADD `category_id` bigint(20);
AltER TABLE `configuration` ADD CONSTRAINT `FK_CATEGORY_ID_CONFIGURATION` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`);
ALTER TABLE `configuration` ADD `data_type` varchar(255);
ALTER TABLE `configuration` MODIFY COLUMN `config_value` varchar(255) NULL;
