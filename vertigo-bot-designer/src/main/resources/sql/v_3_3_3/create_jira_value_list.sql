create sequence SEQ_JIRA_VALUE_LIST
    start with 1000 cache 1;

create sequence SEQ_JIRA_VALUE_LIST_ITEM
    start with 1000 cache 1;

CREATE TABLE jira_value_list (
    jir_val_list_id BIGINT       PRIMARY KEY DEFAULT nextval('seq_jira_value_list'),
    code            VARCHAR(100) NOT NULL,
    label           VARCHAR(100) NOT NULL,
    bot_id          BIGINT       NOT NULL,
    CONSTRAINT fk_jira_value_list_bot
        FOREIGN KEY (bot_id) REFERENCES chatbot (bot_id),
    CONSTRAINT uk_jira_value_list_bot_code
        UNIQUE (bot_id, code)
);

CREATE INDEX idx_jira_value_list_bot
    ON jira_value_list (bot_id);

CREATE TABLE jira_value_list_item (
    jir_val_item_id BIGINT       PRIMARY KEY DEFAULT nextval('seq_jira_value_list_item'),
    code            VARCHAR(100) NOT NULL,
    label           VARCHAR(100) NOT NULL,
    sequence        NUMERIC      NOT NULL,
    jir_val_list_id BIGINT       NOT NULL,
    CONSTRAINT fk_jira_value_list_item_list
        FOREIGN KEY (jir_val_list_id) REFERENCES jira_value_list (jir_val_list_id),
    CONSTRAINT uk_jira_value_list_item_code
        UNIQUE (jir_val_list_id, code)
);

CREATE INDEX idx_jira_value_list_item_list
    ON jira_value_list_item (jir_val_list_id);

ALTER TABLE jira_custom_field_setting
    ADD COLUMN jir_val_list_id BIGINT;

ALTER TABLE jira_custom_field_setting
    ADD CONSTRAINT fk_jira_custom_field_value_list
        FOREIGN KEY (jir_val_list_id) REFERENCES jira_value_list (jir_val_list_id);

CREATE INDEX idx_jira_custom_field_value_list
    ON jira_custom_field_setting (jir_val_list_id);
