-- =========================================================================================================
--   Script name     :	20250214_patch_DSIHEB-7199_bot-1120_delete_saved_trainings.sql
--   Database name   :	chatbot
--   SGBD            :	PostgreSQL Version 16.4
--   Creation date   :	14/02/2025
--   Content         :	On CDS bot (id 1120), saved trainings cannot be deleted, and no new training can be
--                      saved. This script deletes current saved trainings of bot with id 1120, in order to
--                      unblock the situation.
-- =========================================================================================================

-- Delete saved trainings
delete from saved_training
 where bot_id = 1120;

-- Delete unreferenced attachment_file_info
delete from attachment_file_info att_fi
      where not exists (select null from saved_training sav_tra where sav_tra.att_file_info_id = att_fi.att_fi_id)
        and not exists (select null from attachment att where att.att_fi_id = att_fi.att_fi_id);

COMMIT;