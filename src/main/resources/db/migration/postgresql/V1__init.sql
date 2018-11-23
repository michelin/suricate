CREATE TABLE asset (
  id                bigserial     NOT NULL,
  content           BYTEA,
  content_type      VARCHAR(100),
  last_update_date  timestamp,
  size              bigint,
  CONSTRAINT PK_ASSET_ID PRIMARY KEY (id)
);

CREATE TABLE category (
  id              bigserial     NOT NULL,
  name            VARCHAR(255)  NOT NULL,
  technical_name  VARCHAR(255)  NOT NULL,
  image_id        bigint,
  CONSTRAINT PK_CATEGORY_ID             PRIMARY KEY (id),
  CONSTRAINT UK_CATEGORY_TECHNICAL_NAME UNIQUE      (technical_name),
  CONSTRAINT FK_CATEGORY_IMAGE_ID       FOREIGN KEY (image_id)        REFERENCES Asset(id)
);

CREATE TABLE configuration (
  config_key    VARCHAR(255) NOT NULL,
  config_export BIT,
  config_value  VARCHAR(255) NOT NULL,
  CONSTRAINT PK_CONFIGURATION_CONFIG_KEY PRIMARY KEY (config_key)
);

CREATE TABLE library (
  id              bigserial     NOT NULL,
  technical_name  VARCHAR(255)  NOT NULL,
  asset_id        bigint,
  CONSTRAINT PK_LIBRARY_ID              PRIMARY KEY (id),
  CONSTRAINT UK_LIBRARY_TECHNICAL_NAME  UNIQUE      (technical_name),
  CONSTRAINT FK_LIBRARY_ASSET_ID        FOREIGN KEY (asset_id)        REFERENCES Asset(id)
);

CREATE TABLE project (
  id            bigserial     NOT NULL,
  max_column    integer,
  name          VARCHAR(255)  NOT NULL,
  token         VARCHAR(255)  NOT NULL,
  widget_height integer,
  CONSTRAINT PK_PROJECT_ID PRIMARY KEY (id)
);

CREATE TABLE widget (
  id                  bigserial     NOT NULL,
  backend_js          text,
  css_content         text,
  delay               bigint,
  description         VARCHAR(255)  NOT NULL,
  html_content        text,
  info                VARCHAR(255),
  name                VARCHAR(255)  NOT NULL,
  technical_name      VARCHAR(255),
  widget_availability VARCHAR(255),
  category_id         bigint,
  image_id            bigint,
  CONSTRAINT PK_WIDGET_ID             PRIMARY KEY (id),
  CONSTRAINT UK_WIDGET_TECHNICAL_NAME UNIQUE      (technical_name),
  CONSTRAINT FK_WIDGET_CATEGORY_ID    FOREIGN KEY (category_id)     REFERENCES Category(id),
  CONSTRAINT FK_WIDGET_IMAGE_ID       FOREIGN KEY (image_id)        REFERENCES Asset(id)
);

CREATE TABLE project_widget (
  id                  bigserial NOT NULL,
  backend_config      text,
  col                 integer,
  custom_style        text,
  data                text,
  height              integer,
  last_execution_date timestamp,
  last_success_date   timestamp,
  log                 text,
  row                 integer,
  state               VARCHAR(255),
  width               integer,
  project_id          bigint,
  widget_id           bigint,
  CONSTRAINT PK_PROJECT_WIDGET_ID         PRIMARY KEY (id),
  CONSTRAINT FK_PROJECT_WIDGET_PROJECT_ID FOREIGN KEY (project_id)  REFERENCES Project(id),
  CONSTRAINT FK_PROJECT_WIDGET_WIDGET_ID  FOREIGN KEY (widget_id)   REFERENCES Widget(id)
);

CREATE TABLE role (
  id          bigserial     NOT NULL,
  description VARCHAR(100)  NOT NULL,
  name        VARCHAR(50)   NOT NULL,
  CONSTRAINT PK_ROLE_ID   PRIMARY KEY (id),
  CONSTRAINT UK_ROLE_NAME UNIQUE (name)
);

CREATE TABLE users (
  id        bigserial     NOT NULL,
  token     VARCHAR(255)  NOT NULL,
  username  VARCHAR(255)  NOT NULL,
  CONSTRAINT PK_USER_ID       PRIMARY KEY (id),
  CONSTRAINT UK_USER_USERNAME UNIQUE (username)
);

CREATE TABLE user_project (
  project_id  bigint NOT NULL,
  user_id     bigint NOT NULL,
  CONSTRAINT FK_USER_PROJECT_USER_ID    FOREIGN KEY (user_id)     REFERENCES Users(id),
  CONSTRAINT FK_USER_PROJECT_PROJECT_ID FOREIGN KEY (project_id)  REFERENCES Project(id)
);

CREATE TABLE user_role (
  user_id bigint NOT NULL,
  role_id bigint NOT NULL,
  CONSTRAINT FK_USER_ROLE_ROLE_ID FOREIGN KEY (role_id) REFERENCES Role(id),
  CONSTRAINT FK_USER_ROLE_USER_ID FOREIGN KEY (user_id) REFERENCES Users(id)
);


CREATE TABLE widget_library (
  widget_id   bigint  NOT NULL,
  library_id  bigint  NOT NULL,
  CONSTRAINT FK_WIDGET_LIBRARY_LIBRARY_ID FOREIGN KEY (library_id) REFERENCES Library(id),
  CONSTRAINT FK_WIDGET_LIBRARY_WIDGET_ID  FOREIGN KEY (library_id) REFERENCES Library(id)
);