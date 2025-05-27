-- ==========================================================================================
--   Script name         :	alter_chatbot-763-754.sql
--   Database name       :	chatbot
--   SGBD                :	PostgreSQL Version 16.4
--   Creation date       :	27/05/2025
--   Application version :  3.0.6
--   Content             :	Performs updates necessary for tickets CHATBOT-763 and CHATBOT-754.
-- ==========================================================================================

-- CHATBOT-763 : Remove tyop_cd column on context_environment_value
alter table CONTEXT_ENVIRONMENT_VALUE
    drop constraint FK_A_CONTEXT_ENVIRONMENT_VALUE_TYPE_OPERATOR_TYPE_OPERATOR;

drop index A_CONTEXT_ENVIRONMENT_VALUE_TYPE_OPERATOR_TYPE_OPERATOR_FK;

ALTER TABLE CONTEXT_ENVIRONMENT_VALUE DROP COLUMN TYOP_CD;
