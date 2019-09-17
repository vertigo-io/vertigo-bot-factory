-- ============================================================
--   SGBD      		  :  H2                     
-- ============================================================

-- ============================================================
--   Drop                                       
-- ============================================================
drop table IF EXISTS CHATBOT cascade;
drop sequence IF EXISTS SEQ_CHATBOT;
drop table IF EXISTS INTENT cascade;
drop sequence IF EXISTS SEQ_INTENT;
drop table IF EXISTS INTENT_TRAINING_SENTENCE cascade;
drop sequence IF EXISTS SEQ_INTENT_TRAINING_SENTENCE;
drop table IF EXISTS MEDIA_FILE_INFO cascade;
drop sequence IF EXISTS SEQ_MEDIA_FILE_INFO;
drop table IF EXISTS TRAINING cascade;
drop sequence IF EXISTS SEQ_TRAINING;
drop table IF EXISTS UTTER_TEXT cascade;
drop sequence IF EXISTS SEQ_UTTER_TEXT;




-- ============================================================
--   Sequences                                      
-- ============================================================
create sequence SEQ_CHATBOT
	start with 1000 cache 20; 

create sequence SEQ_INTENT
	start with 1000 cache 20; 

create sequence SEQ_INTENT_TRAINING_SENTENCE
	start with 1000 cache 20; 

create sequence SEQ_MEDIA_FILE_INFO
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
    UTX_ID_WELCOME	 NUMERIC     	not null,
    UTX_ID_DEFAULT	 NUMERIC     	not null,
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

comment on column CHATBOT.UTX_ID_WELCOME is
'Welcome text';

comment on column CHATBOT.UTX_ID_DEFAULT is
'Default text';

-- ============================================================
--   Table : INTENT                                        
-- ============================================================
create table INTENT
(
    INT_ID      	 NUMERIC     	not null,
    TITLE       	 VARCHAR(100)	not null,
    DESCRIPTION 	 VARCHAR(100)	,
    IS_SMALL_TALK	 bool        	not null,
    IS_ENABLED  	 bool        	not null,
    BOT_ID      	 NUMERIC     	not null,
    constraint PK_INTENT primary key (INT_ID)
);

comment on column INTENT.INT_ID is
'ID';

comment on column INTENT.TITLE is
'Title';

comment on column INTENT.DESCRIPTION is
'Description';

comment on column INTENT.IS_SMALL_TALK is
'SmallTalk';

comment on column INTENT.IS_ENABLED is
'Enabled';

comment on column INTENT.BOT_ID is
'Chatbot';

-- ============================================================
--   Table : INTENT_TRAINING_SENTENCE                                        
-- ============================================================
create table INTENT_TRAINING_SENTENCE
(
    ITS_ID      	 NUMERIC     	not null,
    TEXT        	 VARCHAR(100)	not null,
    INT_ID      	 NUMERIC     	not null,
    constraint PK_INTENT_TRAINING_SENTENCE primary key (ITS_ID)
);

comment on column INTENT_TRAINING_SENTENCE.ITS_ID is
'ID';

comment on column INTENT_TRAINING_SENTENCE.TEXT is
'Text';

comment on column INTENT_TRAINING_SENTENCE.INT_ID is
'SmallTalkIntent';

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
--   Table : TRAINING                                        
-- ============================================================
create table TRAINING
(
    TRA_ID      	 NUMERIC     	not null,
    START_TIME  	 TIMESTAMP   	not null,
    END_TIME    	 TIMESTAMP   	,
    VERSION_NUMBER	 NUMERIC     	not null,
    TAG         	 VARCHAR(100)	,
    VALID       	 bool        	not null,
    BOT_ID      	 NUMERIC     	not null,
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

comment on column TRAINING.TAG is
'Tag';

comment on column TRAINING.VALID is
'Valid';

comment on column TRAINING.BOT_ID is
'Chatbot';

-- ============================================================
--   Table : UTTER_TEXT                                        
-- ============================================================
create table UTTER_TEXT
(
    UTX_ID      	 NUMERIC     	not null,
    TEXT        	 VARCHAR(100)	not null,
    INT_ID      	 NUMERIC     	,
    constraint PK_UTTER_TEXT primary key (UTX_ID)
);

comment on column UTTER_TEXT.UTX_ID is
'ID';

comment on column UTTER_TEXT.TEXT is
'Text';

comment on column UTTER_TEXT.INT_ID is
'Intent';


alter table CHATBOT
	add constraint FK_CHATBOT_MEDIA_FILE_INFO_MEDIA_FILE_INFO foreign key (FIL_ID_AVATAR)
	references MEDIA_FILE_INFO (FIL_ID);

create index CHATBOT_MEDIA_FILE_INFO_MEDIA_FILE_INFO_FK on CHATBOT (FIL_ID_AVATAR asc);

alter table CHATBOT
	add constraint FK_CHATBOT_UTTER_TEXT_DEFAULT_UTTER_TEXT foreign key (UTX_ID_DEFAULT)
	references UTTER_TEXT (UTX_ID);

create index CHATBOT_UTTER_TEXT_DEFAULT_UTTER_TEXT_FK on CHATBOT (UTX_ID_DEFAULT asc);

alter table CHATBOT
	add constraint FK_CHATBOT_UTTER_TEXT_WELCOME_UTTER_TEXT foreign key (UTX_ID_WELCOME)
	references UTTER_TEXT (UTX_ID);

create index CHATBOT_UTTER_TEXT_WELCOME_UTTER_TEXT_FK on CHATBOT (UTX_ID_WELCOME asc);

alter table INTENT
	add constraint FK_INTENT_CHATBOT_CHATBOT foreign key (BOT_ID)
	references CHATBOT (BOT_ID);

create index INTENT_CHATBOT_CHATBOT_FK on INTENT (BOT_ID asc);

alter table INTENT_TRAINING_SENTENCE
	add constraint FK_INTENT_INTENT_TRAINING_SENTENCE_INTENT foreign key (INT_ID)
	references INTENT (INT_ID);

create index INTENT_INTENT_TRAINING_SENTENCE_INTENT_FK on INTENT_TRAINING_SENTENCE (INT_ID asc);

alter table UTTER_TEXT
	add constraint FK_INTENT_UTTER_TEXT_INTENT foreign key (INT_ID)
	references INTENT (INT_ID);

create index INTENT_UTTER_TEXT_INTENT_FK on UTTER_TEXT (INT_ID asc);

alter table TRAINING
	add constraint FK_TRAINING_CHATBOT_CHATBOT foreign key (BOT_ID)
	references CHATBOT (BOT_ID);

create index TRAINING_CHATBOT_CHATBOT_FK on TRAINING (BOT_ID asc);


