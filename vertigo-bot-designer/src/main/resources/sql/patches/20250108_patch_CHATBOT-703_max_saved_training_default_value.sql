-- =========================================================================================================
--   Script name     :	20250108_patch_CHATBOT-703_max_saved_training_default_value.sql
--   Database name   :	chatbot
--   SGBD            :	PostgreSQL Version 16.4
--   Creation date   :	08/01/2025
--   Content         :	Sets a default value (10) to chatbot_custom_config.max_saved_training column when not
--                      already defined. This case shouldn't happen anymore after CHATBOT-703 resolution.
-- =========================================================================================================

update chatbot_custom_config
   set max_saved_training = 10
 where max_saved_training is null;

COMMIT;