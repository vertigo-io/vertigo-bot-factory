CREATE OR REPLACE FUNCTION add_evaluation_technical_topic() RETURNS void AS $$
DECLARE
  bot chatbot;
  topId NUMERIC;
  sinId NUMERIC;
  technicalCategory topic_category;
  kindTopic kind_topic;
BEGIN
    FOR bot IN (select * from chatbot)
        LOOP
        SELECT * from topic_category where bot_id = bot.bot_id and is_technical = true INTO technicalCategory;
        SELECT * from kind_topic where kto_cd = 'RATING' INTO kindTopic;

        SELECT nextval('SEQ_TOPIC') INTO topId;
        SELECT nextval('SEQ_SCRIPT_INTENTION') INTO sinId;

        INSERT INTO TOPIC (top_id, title, description, is_enabled, bot_id, top_cat_id, tto_cd, kto_cd, code)
        values (topId, kindTopic.label_fr, kindTopic.description_fr, true, bot.bot_id, technicalCategory.top_cat_id, 'SCRIPTINTENTION', kindTopic.kto_cd, kindTopic.kto_cd);

        INSERT INTO script_intention (sin_id, script, top_id) VALUES (sinId,
            'begin sequence
                rating /user/local/rating "Vous pouvez nous laisser une note de satisfaction si vous le souhaitez"
                say "Merci !"
            end sequence', topId);
    END LOOP;
END;
$$
LANGUAGE plpgsql;

SELECT add_evaluation_technical_topic();