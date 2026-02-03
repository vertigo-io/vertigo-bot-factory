create sequence SEQ_JIRA_CUSTOM_FIELD_SETTING
    start with 1000 cache 1;

CREATE TABLE jira_custom_field_setting (
                                           jir_cus_field_set_id BIGINT       PRIMARY KEY DEFAULT nextval('seq_jira_custom_field_setting'),
                                           label                VARCHAR(100) NOT NULL,
                                           field_key            VARCHAR(100) NOT NULL,
                                           enabled              BOOLEAN      NOT NULL DEFAULT FALSE,
                                           mandatory            BOOLEAN      NOT NULL DEFAULT FALSE,
                                           bot_id               BIGINT       NOT NULL,
                                           CONSTRAINT fk_jira_custom_field_bot
                                               FOREIGN KEY (bot_id) REFERENCES chatbot (bot_id)
);

CREATE INDEX idx_jira_custom_field_bot
    ON jira_custom_field_setting (bot_id);
