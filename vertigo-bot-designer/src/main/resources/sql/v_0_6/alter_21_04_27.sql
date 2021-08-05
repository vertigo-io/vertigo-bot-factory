-- ============================================================
--   Table : KIND_TOPIC                                        
-- ============================================================
create table KIND_TOPIC
(
    KTO_CD      	 VARCHAR(100)	not null,
    LABEL       	 VARCHAR(100)	not null,
    TITLE_ENGLISH    	VARCHAR(100)	not null,
    TITLE_FRENCH		VARCHAR(100)	not null,
    description_english VARCHAR(100),
    description_french 	VARCHAR(100),
    default_english  	TEXT			not null,
    default_french 		TEXT			not null,
    constraint PK_KIND_TOPIC primary key (KTO_CD)
);

comment on column KIND_TOPIC.KTO_CD is
'ID';

comment on column KIND_TOPIC.LABEL is
'Label';

comment on column KIND_TOPIC.TITLE_ENGLISH is
'Title (English)';

comment on column KIND_TOPIC.TITLE_FRENCH is
'Title (French)';

comment on column KIND_TOPIC.DESCRIPTION_ENGLISH is
'Description (English)';

comment on column KIND_TOPIC.DESCRIPTION_FRENCH is
'Description (French)';

comment on column KIND_TOPIC.DEFAULT_ENGLISH is
'Default text (English)';

comment on column KIND_TOPIC.DEFAULT_FRENCH is
'Default text (French)';

-- ============================================================
--   Insert MasterData values : KIND_TOPIC                                        
-- ============================================================
insert into KIND_TOPIC(KTO_CD, LABEL, TITLE_ENGLISH, TITLE_FRENCH, DESCRIPTION_ENGLISH, DESCRIPTION_FRENCH, DEFAULT_ENGLISH, DEFAULT_FRENCH) values ('START', 'Start', 'Start', 'Début', 'Default start response', 'Réponse de début par défaut', 'Hello !', 'Bonjour !');
insert into KIND_TOPIC(KTO_CD, LABEL, TITLE_ENGLISH, TITLE_FRENCH, DESCRIPTION_ENGLISH, DESCRIPTION_FRENCH, DEFAULT_ENGLISH, DEFAULT_FRENCH) values ('END', 'End', 'End', 'Fin', 'Default end response', 'Réponse de fin par défaut', 'Bye !', 'Au revoir !');
insert into KIND_TOPIC(KTO_CD, LABEL, TITLE_ENGLISH, TITLE_FRENCH, DESCRIPTION_ENGLISH, DESCRIPTION_FRENCH, DEFAULT_ENGLISH, DEFAULT_FRENCH) values ('FAILURE', 'Failure', 'Failure', 'Echec', 'Default failure response', 'Réponse de d''échec par défaut', 'Sorry, I don''t understand.', 'Désolé, je n''ai pas compris.');
insert into KIND_TOPIC(KTO_CD, LABEL, TITLE_ENGLISH, TITLE_FRENCH, DESCRIPTION_ENGLISH, DESCRIPTION_FRENCH, DEFAULT_ENGLISH, DEFAULT_FRENCH) values ('NORMAL', 'Normal', 'Normal', 'Normal', 'null', 'null', 'Text', 'Texte');


ALTER TABLE TOPIC
ADD COLUMN KTO_CD VARCHAR(100) ;

-- Add constraint 
alter table TOPIC
	add constraint FK_A_TOPIC_KIND_TOPIC foreign key (KTO_CD)
	references KIND_TOPIC (KTO_CD);
	
create index A_TOPIC_TO_KIND_TOPIC_FK on TOPIC (TTO_CD asc);

UPDATE TOPIC SET KTO_CD = 'NORMAL';

ALTER TABLE TOPIC
ALTER COLUMN KTO_CD SET NOT NULL;

ALTER TABLE TOPIC_CATEGORY
ADD COLUMN IS_TECHNICAL BOOLEAN;


CREATE OR REPLACE FUNCTION reprise_basic_message()  RETURNS void AS $$
DECLARE
  bot chatbot;
  topCatId NUMERIC;
  topId NUMERIC;
  smtId NUMERIC;
  uttWelcome utter_text;
  uttDefault utter_text;
BEGIN
	FOR bot IN (select * from chatbot) 
	LOOP
		SELECT * from utter_text where utt_id = bot.utt_id_welcome INTO uttWelcome;
		SELECT * from utter_text where utt_id = bot.utt_id_default INTO uttDefault;
		
		SELECT nextval('SEQ_TOPIC_CATEGORY') INTO topCatId;
		INSERT INTO topic_category (top_cat_id, label, level, is_enabled, bot_id, is_technical)
		VALUES (topCatId, 'Basics', 1, true, bot.bot_id, true);
		
		SELECT nextval('SEQ_TOPIC') INTO topId;
		SELECT nextval('SEQ_SMALL_TALK') INTO smtId;
		
		INSERT INTO TOPIC (top_id, title, description, is_enabled, bot_id, top_cat_id, tto_cd, kto_cd)
		values (topId, 'Failure', 'Default failure response', true, bot.bot_id, topCatId, 'SMALLTALK', 'FAILURE');
		
		INSERT INTO small_talk (smt_id, rty_id, top_id) VALUES (smtId, 'RICH_TEXT', topId);
		UPDATE utter_text set smt_id = smtId where utt_id = uttDefault.utt_id;
		
		
		SELECT nextval('SEQ_TOPIC') INTO topId;
		SELECT nextval('SEQ_SMALL_TALK') INTO smtId;
		
		INSERT INTO TOPIC (top_id, title, description, is_enabled, bot_id, top_cat_id, tto_cd, kto_cd)
		values (topId, 'Start', 'Default start response', true, bot.bot_id, topCatId, 'SMALLTALK', 'START');
		
		INSERT INTO small_talk (smt_id, rty_id, top_id) VALUES (smtId, 'RICH_TEXT', topId);
		UPDATE utter_text set smt_id = smtId where utt_id = uttWelcome.utt_id;
		
		SELECT nextval('SEQ_TOPIC') INTO topId;
		SELECT nextval('SEQ_SMALL_TALK') INTO smtId;
		
		INSERT INTO TOPIC (top_id, title, description, is_enabled, bot_id, top_cat_id, tto_cd, kto_cd)
		values (topId, 'End', 'Default end response', true, bot.bot_id, topCatId, 'SMALLTALK', 'END');
		
		INSERT INTO small_talk (smt_id, rty_id, top_id) VALUES (smtId, 'RICH_TEXT', topId);
		INSERT INTO utter_text (utt_id, text, smt_id) VALUES (nextval('SEQ_UTTER_TEXT'), 'Bye !', smtId);
		
		
	END LOOP;
END;
$$
LANGUAGE plpgsql;

SELECT reprise_basic_message();

ALTER TABLE CHATBOT
DROP COLUMN IF EXISTS UTT_ID_WELCOME,
DROP COLUMN IF EXISTS UTT_ID_DEFAULT;

ALTER TABLE RESPONSE_BUTTON
DROP COLUMN IF EXISTS BOT_ID_WELCOME,
DROP COLUMN IF EXISTS BOT_ID_DEFAULT;

alter table response_type
add column label_fr varchar(100);

comment on column KIND_TOPIC.KTO_CD is
'ID';

comment on column KIND_TOPIC.LABEL is
'Title';


update response_type
set label_fr = 'Texte'
where rty_id = 'RICH_TEXT';

update response_type
set label_fr = 'Texte aléatoire'
where rty_id = 'RANDOM_TEXT';

alter table response_type
alter column label_fr set not null;

alter table type_topic
add column label_fr varchar(100);

update type_topic
set label_fr = 'Small talk'
where tto_cd = 'SMALLTALK';

update type_topic
set label_fr = 'Intention scriptée'
where tto_cd = 'SCRIPTINTENTION';

alter table type_topic
alter column label_fr set not null;

