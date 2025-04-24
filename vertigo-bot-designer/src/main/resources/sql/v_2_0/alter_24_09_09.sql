ALTER TABLE CHATBOT_CUSTOM_CONFIG
    ADD COLUMN CHATBOT_DISPLAY bool DEFAULT true,
    ADD COLUMN QANDA_DISPLAY bool DEFAULT true,
    ADD COLUMN DOCUMENTARY_RESOURCE_DISPLAY	bool DEFAULT true;

comment on column CHATBOT_CUSTOM_CONFIG.CHATBOT_DISPLAY is
'Display chatbot';

comment on column CHATBOT_CUSTOM_CONFIG.QANDA_DISPLAY is
'Display Q&A';

comment on column CHATBOT_CUSTOM_CONFIG.DOCUMENTARY_RESOURCE_DISPLAY is
'Display documentary resources';

-- For existing platforms, Q&A and Documentary resources are disabled by default.
UPDATE CHATBOT_CUSTOM_CONFIG
   SET QANDA_DISPLAY = false,
       DOCUMENTARY_RESOURCE_DISPLAY = false;