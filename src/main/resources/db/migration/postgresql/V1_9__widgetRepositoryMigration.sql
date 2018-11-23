CREATE TABLE repository (
  id          bigserial     NOT NULL,
  name        varchar(255)  NOT NULL,
  url         varchar(255),
  branch      varchar(255),
  login       varchar(255),
  password    varchar(255),
  enabled     char          NOT NULL,
  local_path  varchar(255),
  type        varchar(255)  NOT NULL,
  CONSTRAINT PK_REPOSITORY_ID   PRIMARY KEY (id),
  CONSTRAINT UK_REPOSITORY_NAME UNIQUE      (name)
);
ALTER TABLE widget ADD repository_id bigint;
ALTER TABLE widget ADD CONSTRAINT FK_WIDGET_REPOSITORY_ID FOREIGN KEY (repository_id) REFERENCES repository (id);