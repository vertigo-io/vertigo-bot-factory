ALTER TABLE CHATBOT_CUSTOM_CONFIG ADD TOTAL_MAX_ATTACHMENT_SIZE	 NUMERIC DEFAULT -1;

comment on column CHATBOT_CUSTOM_CONFIG.TOTAL_MAX_ATTACHMENT_SIZE is
'Total maximum attachment size';