-- Tables creation

CREATE TABLE allowed_setting_value (
    id          bigserial               NOT NULL,
    title       character varying(255)  NOT NULL,
    value       character varying(255)  NOT NULL,
    is_default  character(1)            NOT NULL,
    setting_id  bigint                  NOT NULL,
    CONSTRAINT pk_allowed_setting_value_id  PRIMARY KEY (id)
);

CREATE TABLE asset (
    id                 bigserial                    NOT NULL,
    content            bytea,
    content_type       character varying,
    size               bigint,
    created_by         character varying(255)       DEFAULT 'APPLICATION'::character varying NOT NULL,
    created_date       timestamp without time zone  DEFAULT now() NOT NULL,
    last_modified_by   character varying(255)       DEFAULT 'APPLICATION'::character varying,
    last_modified_date timestamp without time zone,
    CONSTRAINT pk_asset_id                          PRIMARY KEY (id)
);

CREATE TABLE category (
    id                  bigserial                   NOT NULL,
    name                character varying           NOT NULL,
    technical_name      character varying           NOT NULL,
    image_id            bigint,
    created_by          character varying(255)      DEFAULT 'APPLICATION'::character varying NOT NULL,
    created_date        timestamp without time zone DEFAULT now() NOT NULL,
    last_modified_by    character varying(255)      DEFAULT 'APPLICATION'::character varying,
    last_modified_date  timestamp without time zone DEFAULT now(),
    CONSTRAINT pk_category_id                       PRIMARY KEY (id),
    CONSTRAINT uk_category_technical_name           UNIQUE (technical_name)

);

CREATE TABLE configuration (
    config_key          character varying           NOT NULL,
    config_export       boolean,
    config_value        character varying(500),
    category_id         bigint,
    data_type           character varying(255),
    created_by          character varying(255)      DEFAULT 'APPLICATION'::character varying NOT NULL,
    created_date        timestamp without time zone DEFAULT now() NOT NULL,
    last_modified_by    character varying(255)      DEFAULT 'APPLICATION'::character varying,
    last_modified_date  timestamp without time zone DEFAULT now(),
    CONSTRAINT pk_configuration_config_key          PRIMARY KEY (config_key)
);

CREATE TABLE library (
    id                  bigserial                   NOT NULL,
    technical_name      character varying           NOT NULL,
    asset_id            bigint,
    created_by          character varying(255)      DEFAULT 'APPLICATION'::character varying NOT NULL,
    created_date        timestamp without time zone DEFAULT now() NOT NULL,
    last_modified_by    character varying(255)      DEFAULT 'APPLICATION'::character varying,
    last_modified_date  timestamp without time zone DEFAULT now(),
    CONSTRAINT pk_library_id                        PRIMARY KEY (id),
    CONSTRAINT uk_library_technical_name            UNIQUE (technical_name)

);

CREATE TABLE project (
    id                  bigserial                   NOT NULL,
    max_column          integer,
    name                character varying           NOT NULL,
    token               character varying           NOT NULL,
    widget_height       integer,
    css_style           text,
    screenshot_id       bigint,
    created_by          character varying(255)      DEFAULT 'APPLICATION'::character varying NOT NULL,
    created_date        timestamp without time zone DEFAULT now() NOT NULL,
    last_modified_by    character varying(255)      DEFAULT 'APPLICATION'::character varying,
    last_modified_date  timestamp without time zone DEFAULT now(),
    CONSTRAINT pk_project_id                        PRIMARY KEY (id)
);

CREATE TABLE project_widget (
    id                  bigserial                       NOT NULL,
    backend_config      text,
    col                 integer,
    custom_style        text,
    data                text,
    height              integer,
    last_execution_date timestamp without time zone,
    log                 text,
    row                 integer,
    width               integer,
    project_id          bigint,
    widget_id           bigint,
    last_success_date   timestamp without time zone,
    state               character varying,
    created_by          character varying(255)          DEFAULT 'APPLICATION'::character varying NOT NULL,
    created_date        timestamp without time zone     DEFAULT now() NOT NULL,
    last_modified_by    character varying(255)          DEFAULT 'APPLICATION'::character varying,
    last_modified_date  timestamp without time zone     DEFAULT now(),
    CONSTRAINT pk_project_widget_id                     PRIMARY KEY (id)
);

CREATE TABLE repository (
    id              bigserial               NOT NULL,
    name            character varying(255)  NOT NULL,
    url             character varying(255),
    branch          character varying(255),
    login           character varying(255),
    password        character varying(255),
    enabled         character(1)            NOT NULL,
    local_path      character varying(255),
    type            character varying(255)  NOT NULL,
    CONSTRAINT pk_repository_id             PRIMARY KEY (id),
    CONSTRAINT uk_repository_name           UNIQUE (name)
);


CREATE TABLE role (
    id          bigserial           NOT NULL,
    description character varying   NOT NULL,
    name        character varying   NOT NULL,
    CONSTRAINT pk_role_id           PRIMARY KEY (id),
    CONSTRAINT uk_role_name         UNIQUE (name)
);

CREATE TABLE setting (
    id              bigserial               NOT NULL,
    constrained     character(1)            NOT NULL,
    data_type       character varying(255)  NOT NULL,
    type            character varying(255)  NOT NULL,
    description     character varying(255)  NOT NULL,
    CONSTRAINT pk_setting_id                PRIMARY KEY (id)
);

CREATE TABLE user_project (
    project_id  bigint NOT NULL,
    user_id     bigint NOT NULL
);

CREATE TABLE user_role (
    user_id     bigint NOT NULL,
    role_id     bigint NOT NULL
);

CREATE TABLE user_setting (
    id                          bigserial                   NOT NULL,
    unconstrained_value         character varying(255),
    setting_id                  bigint                      NOT NULL,
    allowed_setting_value_id    bigint,
    user_id                     bigint                      NOT NULL,
    CONSTRAINT pk_user_setting_id                           PRIMARY KEY (id)
);

CREATE TABLE users (
    id          bigserial               NOT NULL,
    username    character varying       NOT NULL,
    firstname   character varying(250),
    lastname    character varying(250),
    email       character varying(250),
    password    character varying(250),
    auth_mode   character varying(20)   DEFAULT 'LDAP'::character varying NOT NULL,
    CONSTRAINT pk_users_id              PRIMARY KEY (id),
    CONSTRAINT uk_user_username         UNIQUE (username)
);

CREATE TABLE widget (
    id                  bigserial                   NOT NULL,
    backend_js          text,
    css_content         text,
    delay               bigint,
    description         character varying           NOT NULL,
    html_content        text,
    name                character varying           NOT NULL,
    technical_name      character varying,
    category_id         bigint,
    info                character varying,
    image_id            bigint,
    widget_availability character varying,
    timeout             bigint,
    repository_id       bigint,
    created_by          character varying(255)      DEFAULT 'APPLICATION'::character varying NOT NULL,
    created_date        timestamp without time zone DEFAULT now() NOT NULL,
    last_modified_by    character varying(255)      DEFAULT 'APPLICATION'::character varying,
    last_modified_date  timestamp without time zone DEFAULT now(),
    CONSTRAINT pk_widget_id                         PRIMARY KEY (id),
    CONSTRAINT uk_widget_technical_name             UNIQUE (technical_name)
);

CREATE TABLE widget_library (
    widget_id   bigint NOT NULL,
    library_id  bigint NOT NULL
);

CREATE TABLE widget_param (
    id                  bigserial                   NOT NULL,
    description         character varying(255),
    name                character varying(255)      NOT NULL,
    required            character(1)                NOT NULL,
    type                character varying(255)      NOT NULL,
    usage_example       character varying(255),
    widget_id           bigint,
    default_value       character varying(255),
    accept_file_regex   character varying(255),
    created_by          character varying(255)      DEFAULT 'APPLICATION'::character varying NOT NULL,
    created_date        timestamp without time zone DEFAULT now() NOT NULL,
    last_modified_by    character varying(255)      DEFAULT 'APPLICATION'::character varying,
    last_modified_date  timestamp without time zone DEFAULT now(),
    CONSTRAINT pk_widget_param_id                   PRIMARY KEY (id)
);


CREATE TABLE widget_param_value (
    id                  bigserial                   NOT NULL,
    js_key              character varying(255)      NOT NULL,
    value               character varying(255)      NOT NULL,
    widget_param_id     bigint,
    created_by          character varying(255)      DEFAULT 'APPLICATION'::character varying NOT NULL,
    created_date        timestamp without time zone DEFAULT now() NOT NULL,
    last_modified_by    character varying(255)      DEFAULT 'APPLICATION'::character varying,
    last_modified_date  timestamp without time zone DEFAULT now(),
    CONSTRAINT pk_widget_param_value_id             PRIMARY KEY (id)
);

-- Constraints creation (FK)
ALTER TABLE widget_param_value      ADD CONSTRAINT fk_widget_param_value_widget_param_id    FOREIGN KEY (widget_param_id)           REFERENCES widget_param (id) ;
ALTER TABLE widget_param            ADD CONSTRAINT fk_widget_param_widget_id                FOREIGN KEY (widget_id)                 REFERENCES widget (id) ;
ALTER TABLE widget_library          ADD CONSTRAINT fk_widget_library_widget_id              FOREIGN KEY (library_id)                REFERENCES library (id) ;
ALTER TABLE widget_library          ADD CONSTRAINT fk_widget_library_library_id             FOREIGN KEY (library_id)                REFERENCES library (id) ;
ALTER TABLE widget                  ADD CONSTRAINT fk_widget_repository_id                  FOREIGN KEY (repository_id)             REFERENCES repository (id) ;
ALTER TABLE widget                  ADD CONSTRAINT fk_widget_image_id                       FOREIGN KEY (image_id)                  REFERENCES asset (id) ;
ALTER TABLE widget                  ADD CONSTRAINT fk_widget_category_id                    FOREIGN KEY (category_id)               REFERENCES category (id) ;
ALTER TABLE user_setting            ADD CONSTRAINT fk_user_setting_user_id                  FOREIGN KEY (user_id)                   REFERENCES users (id) ;
ALTER TABLE user_setting            ADD CONSTRAINT fk_user_setting_setting_id               FOREIGN KEY (setting_id)                REFERENCES setting (id) ;
ALTER TABLE user_setting            ADD CONSTRAINT fk_user_setting_allowed_setting_value_id FOREIGN KEY (allowed_setting_value_id)  REFERENCES allowed_setting_value (id) ;
ALTER TABLE user_role               ADD CONSTRAINT fk_user_role_user_id                     FOREIGN KEY (user_id)                   REFERENCES users (id) ;
ALTER TABLE user_role               ADD CONSTRAINT fk_user_role_role_id                     FOREIGN KEY (role_id)                   REFERENCES role (id) ;
ALTER TABLE user_project            ADD CONSTRAINT fk_user_project_user_id                  FOREIGN KEY (user_id)                   REFERENCES users (id) ;
ALTER TABLE user_project            ADD CONSTRAINT fk_user_project_project_id               FOREIGN KEY (project_id)                REFERENCES project (id) ;
ALTER TABLE project_widget          ADD CONSTRAINT fk_project_widget_widget_id              FOREIGN KEY (widget_id)                 REFERENCES widget (id) ;
ALTER TABLE project_widget          ADD CONSTRAINT fk_project_widget_project_id             FOREIGN KEY (project_id)                REFERENCES project (id) ;
ALTER TABLE project                 ADD CONSTRAINT fk_project_screenshot_id                 FOREIGN KEY (screenshot_id)             REFERENCES asset (id) ;
ALTER TABLE library                 ADD CONSTRAINT fk_library_asset_id                      FOREIGN KEY (asset_id)                  REFERENCES asset (id) ;
ALTER TABLE configuration           ADD CONSTRAINT fk_configuration_category_id             FOREIGN KEY (category_id)               REFERENCES category (id) ;
ALTER TABLE category                ADD CONSTRAINT fk_category_image_id                     FOREIGN KEY (image_id)                  REFERENCES asset (id) ;
ALTER TABLE allowed_setting_value   ADD CONSTRAINT fk_allowed_setting_value_setting_id      FOREIGN KEY (setting_id)                REFERENCES setting (id) ;

-- Insert default settings
INSERT INTO setting(constrained, data_type, type, description) VALUES (E'Y' , E'COMBO' , E'TEMPLATE' , E'Template');
INSERT INTO setting(constrained, data_type, type, description) VALUES (E'Y' , E'COMBO' , E'LANGUAGE' , E'Language');
INSERT INTO allowed_setting_value(title, value, is_default, setting_id) VALUES (E'Default' , E'default-theme' , E'Y' , 1);
INSERT INTO allowed_setting_value(title, value, is_default, setting_id) VALUES (E'Dark' , E'dark-theme' , E'N' , 1);
INSERT INTO allowed_setting_value(title, value, is_default, setting_id) VALUES (E'English' , E'en' , E'Y' , 2);
INSERT INTO allowed_setting_value(title, value, is_default, setting_id) VALUES (E'Français' , E'fr' , E'N' , 2);

-- Insert default roles
INSERT INTO role(description, name) VALUES (E'Administrator Role' , E'ROLE_ADMIN');
INSERT INTO role(description, name) VALUES (E'User role' , E'ROLE_USER');
