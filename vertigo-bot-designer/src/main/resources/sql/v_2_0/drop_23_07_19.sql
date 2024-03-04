DELETE FROM type_export_analytics
WHERE tea_cd = 'RATING';

ALTER TABLE type_export_analytics
DROP COLUMN BOT_RELATED;
