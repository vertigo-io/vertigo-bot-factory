ALTER TABLE CHATBOT_CUSTOM_CONFIG ADD COLUMN FONT_FAMILY VARCHAR(100) DEFAULT 'Arial, Helvetica, sans-serif';

comment on column CHATBOT_CUSTOM_CONFIG.FONT_FAMILY is
'Bot font family';
