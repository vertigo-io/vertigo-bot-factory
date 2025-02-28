-- ==========================================================================================
--   Script name         :	alter_chatbot-682-699.sql
--   Database name       :	chatbot
--   SGBD                :	PostgreSQL Version 16.4
--   Creation date       :	25/02/28
--   Application version : 3.0.5
--   Content             :	Performs updates necessary for tickets CHATBOT-682 and CHATBOT-699.
-- ==========================================================================================

-- CHATBOT-682 and CHATBOT-699 Adding new fields for colors of links and rating
ALTER TABLE chatbot_custom_config ADD COLUMN BOT_MESSAGE_LINK_COLOR VARCHAR(100) DEFAULT '-webkit-link';
ALTER TABLE chatbot_custom_config ADD COLUMN BOT_RATING_COLOR VARCHAR(100) DEFAULT '#ffeb3b';

comment on column CHATBOT_CUSTOM_CONFIG.BOT_MESSAGE_LINK_COLOR is
'Bot message link color';

comment on column CHATBOT_CUSTOM_CONFIG.BOT_RATING_COLOR is
'Bot rating color';