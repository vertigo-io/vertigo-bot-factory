ALTER TABLE chatbot_custom_config ADD COLUMN BOT_MESSAGE_BACKGROUND_COLOR VARCHAR(100) DEFAULT '#ccccc6';
ALTER TABLE chatbot_custom_config ADD COLUMN USER_MESSAGE_FONT_COLOR VARCHAR(100) DEFAULT '#000000';
ALTER TABLE chatbot_custom_config RENAME COLUMN FONT_COLOR TO BOT_MESSAGE_FONT_COLOR;


