ALTER TABLE project ADD screenshot_id bigint;
ALTER TABLE project ADD CONSTRAINT FK_PROJECT_SCREENSHOT_ID FOREIGN KEY (screenshot_id) REFERENCES asset (id);
