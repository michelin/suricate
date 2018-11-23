CREATE TABLE widget_param (
  id                bigserial     NOT NULL,
  description       varchar(255),
  name              varchar(255)  NOT NULL,
  required          char          NOT NULL,
  type              varchar(255)  NOT NULL,
  usage_example     varchar(255),
  widget_id         bigint,
  default_value     varchar(255),
  accept_file_regex varchar(255),
  CONSTRAINT PK_WIDGET_PARAM_ID         PRIMARY KEY (id),
  CONSTRAINT FK_WIDGET_PARAM_WIDGET_ID  FOREIGN KEY (widget_id) REFERENCES widget(id)
);

CREATE TABLE widget_param_value (
  id              bigserial     NOT NULL,
  js_key          varchar(255)  NOT NULL,
  value           varchar(255)  NOT NULL,
  widget_param_id bigint,
  CONSTRAINT PK_WIDGET_PARAM_VALUE_ID               PRIMARY KEY (id),
  CONSTRAINT FK_WIDGET_PARAM_VALUE_WIDGET_PARAM_ID  FOREIGN KEY (widget_param_id) REFERENCES Widget_param (id)
);