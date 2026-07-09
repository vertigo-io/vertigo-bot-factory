-- ==========================================================================================
--   Script name         :	alter_chatbot-tabs-secondary-colors.sql
--   Database name       :	chatbot
--   SGBD                :	PostgreSQL Version 16.4
--   Creation date       :	26/04/24
--   Application version : 3.3.0
--   Content             :	Adds secondary background and font colors for the platform footer tabs
--                          (CHATBOT / FAQ) to allow distinguishing selected vs unselected tabs.
-- ==========================================================================================

-- Adding new fields for the secondary colors used by unselected footer tabs
ALTER TABLE chatbot_custom_config ADD COLUMN SECONDARY_BACKGROUND_COLOR VARCHAR(100) DEFAULT '#ffffff';
ALTER TABLE chatbot_custom_config ADD COLUMN SECONDARY_FONT_COLOR VARCHAR(100) DEFAULT '#000000';

comment on column CHATBOT_CUSTOM_CONFIG.SECONDARY_BACKGROUND_COLOR is
'Bot secondary background color';

comment on column CHATBOT_CUSTOM_CONFIG.SECONDARY_FONT_COLOR is
'Bot secondary font color';
