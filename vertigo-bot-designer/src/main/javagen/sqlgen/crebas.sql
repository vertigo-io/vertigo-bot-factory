-- ============================================================
--   SGBD      		  :  Postgres                     
-- ============================================================

-- ============================================================
--   Drop                                       
-- ============================================================
drop table IF EXISTS CHATBOT cascade;
drop sequence IF EXISTS SEQ_CHATBOT;
drop table IF EXISTS MEDIA_FILE_INFO cascade;
drop sequence IF EXISTS SEQ_MEDIA_FILE_INFO;
drop table IF EXISTS NLU_TRAINING_SENTENCE cascade;
drop sequence IF EXISTS SEQ_NLU_TRAINING_SENTENCE;
drop table IF EXISTS SMALL_TALK cascade;
drop sequence IF EXISTS SEQ_SMALL_TALK;
drop table IF EXISTS TRAINING cascade;
drop sequence IF EXISTS SEQ_TRAINING;
drop table IF EXISTS UTTER_TEXT cascade;
drop sequence IF EXISTS SEQ_UTTER_TEXT;




-- ============================================================
--   Sequences                                      
-- ============================================================
create sequence SEQ_CHATBOT
	start with 1000 cache 20; 

create sequence SEQ_MEDIA_FILE_INFO
	start with 1000 cache 20; 

create sequence SEQ_NLU_TRAINING_SENTENCE
	start with 1000 cache 20; 

create sequence SEQ_SMALL_TALK
	start with 1000 cache 20; 

create sequence SEQ_TRAINING
	start with 1000 cache 20; 

create sequence SEQ_UTTER_TEXT
	start with 1000 cache 20; 


-- ============================================================
--   Table : CHATBOT                                        
-- ============================================================
create table CHATBOT
(
    BOT_ID      	 NUMERIC     	not null,
    NAME        	 VARCHAR(100)	not null,
    CREATION_DATE	 DATE        	not null,
    STATUS      	 VARCHAR(100)	not null,
    FIL_ID_AVATAR	 NUMERIC     	,
    UTT_ID_WELCOME	 NUMERIC     	not null,
    UTT_ID_DEFAULT	 NUMERIC     	not null,
    constraint PK_CHATBOT primary key (BOT_ID)
);

comment on column CHATBOT.BOT_ID is
'ID';

comment on column CHATBOT.NAME is
'Name';

comment on column CHATBOT.CREATION_DATE is
'Creation date';

comment on column CHATBOT.STATUS is
'Status';

comment on column CHATBOT.FIL_ID_AVATAR is
'Avatar';

comment on column CHATBOT.UTT_ID_WELCOME is
'Welcome text';

comment on column CHATBOT.UTT_ID_DEFAULT is
'Default text';

-- ============================================================
--   Table : MEDIA_FILE_INFO                                        
-- ============================================================
create table MEDIA_FILE_INFO
(
    FIL_ID      	 NUMERIC     	not null,
    FILE_NAME   	 VARCHAR(100)	not null,
    MIME_TYPE   	 VARCHAR(100)	not null,
    LENGTH      	 NUMERIC     	not null,
    LAST_MODIFIED	 TIMESTAMP   	not null,
    FILE_PATH   	 VARCHAR(500)	,
    FILE_DATA   	 bytea       	,
    constraint PK_MEDIA_FILE_INFO primary key (FIL_ID)
);

comment on column MEDIA_FILE_INFO.FIL_ID is
'Id';

comment on column MEDIA_FILE_INFO.FILE_NAME is
'Name';

comment on column MEDIA_FILE_INFO.MIME_TYPE is
'MimeType';

comment on column MEDIA_FILE_INFO.LENGTH is
'Size';

comment on column MEDIA_FILE_INFO.LAST_MODIFIED is
'Modification Date';

comment on column MEDIA_FILE_INFO.FILE_PATH is
'path';

comment on column MEDIA_FILE_INFO.FILE_DATA is
'data';

-- ============================================================
--   Table : NLU_TRAINING_SENTENCE                                        
-- ============================================================
create table NLU_TRAINING_SENTENCE
(
    NTS_ID      	 NUMERIC     	not null,
    TEXT        	 VARCHAR(100)	not null,
    SMT_ID      	 NUMERIC     	not null,
    constraint PK_NLU_TRAINING_SENTENCE primary key (NTS_ID)
);

comment on column NLU_TRAINING_SENTENCE.NTS_ID is
'ID';

comment on column NLU_TRAINING_SENTENCE.TEXT is
'Text';

comment on column NLU_TRAINING_SENTENCE.SMT_ID is
'SmallTalk';

-- ============================================================
--   Table : SMALL_TALK                                        
-- ============================================================
create table SMALL_TALK
(
    SMT_ID      	 NUMERIC     	not null,
    TITLE       	 VARCHAR(100)	not null,
    DESCRIPTION 	 VARCHAR(100)	,
    IS_ENABLED  	 bool        	not null,
    BOT_ID      	 NUMERIC     	not null,
    constraint PK_SMALL_TALK primary key (SMT_ID)
);

comment on column SMALL_TALK.SMT_ID is
'ID';

comment on column SMALL_TALK.TITLE is
'Title';

comment on column SMALL_TALK.DESCRIPTION is
'Description';

comment on column SMALL_TALK.IS_ENABLED is
'Enabled';

comment on column SMALL_TALK.BOT_ID is
'Chatbot';

-- ============================================================
--   Table : TRAINING                                        
-- ============================================================
create table TRAINING
(
    TRA_ID      	 NUMERIC     	not null,
    START_TIME  	 TIMESTAMP   	not null,
    END_TIME    	 TIMESTAMP   	,
    VERSION_NUMBER	 NUMERIC     	not null,
    STATUS      	 VARCHAR(100)	not null,
    LOG         	 TEXT        	,
    BOT_ID      	 NUMERIC     	not null,
    FIL_ID_MODEL	 NUMERIC     	,
    constraint PK_TRAINING primary key (TRA_ID)
);

comment on column TRAINING.TRA_ID is
'ID';

comment on column TRAINING.START_TIME is
'Start time';

comment on column TRAINING.END_TIME is
'End time';

comment on column TRAINING.VERSION_NUMBER is
'Version';

comment on column TRAINING.STATUS is
'Status';

comment on column TRAINING.LOG is
'Log';

comment on column TRAINING.BOT_ID is
'Chatbot';

comment on column TRAINING.FIL_ID_MODEL is
'Model';

-- ============================================================
--   Table : UTTER_TEXT                                        
-- ============================================================
create table UTTER_TEXT
(
    UTT_ID      	 NUMERIC     	not null,
    TEXT        	 VARCHAR(100)	not null,
    SMT_ID      	 NUMERIC     	,
    constraint PK_UTTER_TEXT primary key (UTT_ID)
);

comment on column UTTER_TEXT.UTT_ID is
'ID';

comment on column UTTER_TEXT.TEXT is
'Text';

comment on column UTTER_TEXT.SMT_ID is
'SmallTalk';


alter table CHATBOT
	add constraint FK_CHATBOT_MEDIA_FILE_INFO_MEDIA_FILE_INFO foreign key (FIL_ID_AVATAR)
	references MEDIA_FILE_INFO (FIL_ID);

create index CHATBOT_MEDIA_FILE_INFO_MEDIA_FILE_INFO_FK on CHATBOT (FIL_ID_AVATAR asc);

alter table CHATBOT
	add constraint FK_CHATBOT_UTTER_TEXT_DEFAULT_UTTER_TEXT foreign key (UTT_ID_DEFAULT)
	references UTTER_TEXT (UTT_ID);

create index CHATBOT_UTTER_TEXT_DEFAULT_UTTER_TEXT_FK on CHATBOT (UTT_ID_DEFAULT asc);

alter table CHATBOT
	add constraint FK_CHATBOT_UTTER_TEXT_WELCOME_UTTER_TEXT foreign key (UTT_ID_WELCOME)
	references UTTER_TEXT (UTT_ID);

create index CHATBOT_UTTER_TEXT_WELCOME_UTTER_TEXT_FK on CHATBOT (UTT_ID_WELCOME asc);

alter table SMALL_TALK
	add constraint FK_SMALL_TALK_CHATBOT_CHATBOT foreign key (BOT_ID)
	references CHATBOT (BOT_ID);

create index SMALL_TALK_CHATBOT_CHATBOT_FK on SMALL_TALK (BOT_ID asc);

alter table NLU_TRAINING_SENTENCE
	add constraint FK_SMALL_TALK_NLU_TRAINING_SENTENCE_SMALL_TALK foreign key (SMT_ID)
	references SMALL_TALK (SMT_ID);

create index SMALL_TALK_NLU_TRAINING_SENTENCE_SMALL_TALK_FK on NLU_TRAINING_SENTENCE (SMT_ID asc);

alter table UTTER_TEXT
	add constraint FK_SMALL_TALK_UTTER_TEXT_SMALL_TALK foreign key (SMT_ID)
	references SMALL_TALK (SMT_ID);

create index SMALL_TALK_UTTER_TEXT_SMALL_TALK_FK on UTTER_TEXT (SMT_ID asc);

alter table TRAINING
	add constraint FK_TRAINING_CHATBOT_CHATBOT foreign key (BOT_ID)
	references CHATBOT (BOT_ID);

create index TRAINING_CHATBOT_CHATBOT_FK on TRAINING (BOT_ID asc);

alter table TRAINING
	add constraint FK_TRAINING_MEDIA_FILE_INFO_MEDIA_FILE_INFO foreign key (FIL_ID_MODEL)
	references MEDIA_FILE_INFO (FIL_ID);

create index TRAINING_MEDIA_FILE_INFO_MEDIA_FILE_INFO_FK on TRAINING (FIL_ID_MODEL asc);


