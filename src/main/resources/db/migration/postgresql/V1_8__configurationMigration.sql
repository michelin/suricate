ALTER TABLE configuration ADD category_id bigint;
AltER TABLE configuration ADD CONSTRAINT FK_CONFIGURATION_CATEGORY_ID FOREIGN KEY (category_id) REFERENCES category (id);
ALTER TABLE configuration ADD data_type varchar(255);
ALTER TABLE configuration ALTER COLUMN config_value DROP NOT NULL;
ALTER TABLE configuration ALTER COLUMN config_value TYPE varchar(255);
