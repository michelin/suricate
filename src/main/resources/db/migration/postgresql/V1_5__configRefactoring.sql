ALTER TABLE users ALTER COLUMN token TYPE VARCHAR(500);
ALTER TABLE users DROP COLUMN token;
ALTER TABLE users ADD COLUMN firstname VARCHAR(250);
ALTER TABLE users ADD COLUMN lastname VARCHAR(250);
ALTER TABLE users ADD COLUMN email VARCHAR(250);
ALTER TABLE users ADD COLUMN password VARCHAR(250);
ALTER TABLE users ADD COLUMN auth_mode VARCHAR(20) NOT NULL;

ALTER TABLE asset ADD COLUMN created_by VARCHAR(255) NOT NULL;
ALTER TABLE asset ADD COLUMN created_date TIMESTAMP NOT NULL;
ALTER TABLE asset ADD COLUMN last_modified_by VARCHAR(255);
ALTER TABLE asset RENAME COLUMN last_update_date TO last_modified_date;

ALTER TABLE category ADD COLUMN created_by VARCHAR(255) NOT NULL;
ALTER TABLE category ADD COLUMN created_date TIMESTAMP NOT NULL;
ALTER TABLE category ADD COLUMN last_modified_by VARCHAR(255);
ALTER TABLE category ADD COLUMN last_modified_date TIMESTAMP;

ALTER TABLE configuration ADD COLUMN created_by VARCHAR(255) NOT NULL;
ALTER TABLE configuration ADD COLUMN created_date TIMESTAMP NOT NULL;
ALTER TABLE configuration ADD COLUMN last_modified_by VARCHAR(255);
ALTER TABLE configuration ADD COLUMN last_modified_date TIMESTAMP;

ALTER TABLE library ADD COLUMN created_by VARCHAR(255) NOT NULL;
ALTER TABLE library ADD COLUMN created_date TIMESTAMP NOT NULL;
ALTER TABLE library ADD COLUMN last_modified_by VARCHAR(255);
ALTER TABLE library ADD COLUMN last_modified_date TIMESTAMP;

ALTER TABLE project ADD COLUMN created_by VARCHAR(255) NOT NULL;
ALTER TABLE project ADD COLUMN created_date TIMESTAMP NOT NULL;
ALTER TABLE project ADD COLUMN last_modified_by VARCHAR(255);
ALTER TABLE project ADD COLUMN last_modified_date TIMESTAMP;

ALTER TABLE project_widget ADD COLUMN created_by VARCHAR(255) NOT NULL;
ALTER TABLE project_widget ADD COLUMN created_date TIMESTAMP NOT NULL;
ALTER TABLE project_widget ADD COLUMN last_modified_by VARCHAR(255);
ALTER TABLE project_widget ADD COLUMN last_modified_date TIMESTAMP;

ALTER TABLE widget ADD COLUMN created_by VARCHAR(255) NOT NULL;
ALTER TABLE widget ADD COLUMN created_date TIMESTAMP NOT NULL;
ALTER TABLE widget ADD COLUMN last_modified_by VARCHAR(255);
ALTER TABLE widget ADD COLUMN last_modified_date TIMESTAMP;

ALTER TABLE widget_param ADD COLUMN created_by VARCHAR(255) NOT NULL;
ALTER TABLE widget_param ADD COLUMN created_date TIMESTAMP NOT NULL;
ALTER TABLE widget_param ADD COLUMN last_modified_by VARCHAR(255);
ALTER TABLE widget_param ADD COLUMN last_modified_date TIMESTAMP;

ALTER TABLE widget_param_value ADD COLUMN created_by VARCHAR(255) NOT NULL;
ALTER TABLE widget_param_value ADD COLUMN created_date TIMESTAMP NOT NULL;
ALTER TABLE widget_param_value ADD COLUMN last_modified_by VARCHAR(255);
ALTER TABLE widget_param_value ADD COLUMN last_modified_date TIMESTAMP;
