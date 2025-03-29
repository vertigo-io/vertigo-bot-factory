-- =========================================================================================================
--   Script name         :	alter_25_03_14_add_config_jira_check_before_create.sql
--   Database name       :	chatbot
--   SGBD                :	PostgreSQL Version 16.4
--   Creation date       :	14/03/25
--   Application version :  2.4.2
--   Content             :	Adds a parameter do activate/deactivate Jira fields check before ticket creation
-- ==========================================================================================================

-- Add Jira config parameter
alter table CHATBOT_CUSTOM_CONFIG add COLUMN JIRA_CHECK_BEFORE_CREATE bool not null DEFAULT true;
comment on column CHATBOT_CUSTOM_CONFIG.JIRA_CHECK_BEFORE_CREATE is 'Check Jira fields before ticket creation';