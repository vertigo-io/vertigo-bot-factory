-- ============================================================
--   SGBD      		  :  H2                     
-- ============================================================

-- ============================================================
--   Drop                                       
-- ============================================================
drop table IF EXISTS ACTION cascade;
drop sequence IF EXISTS SEQ_ACTION;
drop table IF EXISTS CHATBOT cascade;
drop sequence IF EXISTS SEQ_CHATBOT;
drop table IF EXISTS INTENT cascade;
drop sequence IF EXISTS SEQ_INTENT;
drop table IF EXISTS INTENT_TRAINING_SENTENCE cascade;
drop sequence IF EXISTS SEQ_INTENT_TRAINING_SENTENCE;
drop table IF EXISTS TRAINING cascade;
drop sequence IF EXISTS SEQ_TRAINING;
drop table IF EXISTS UTTER_TEXT cascade;
drop sequence IF EXISTS SEQ_UTTER_TEXT;




-- ============================================================
--   Sequences                                      
-- ============================================================
create sequence SEQ_ACTION
	start with 1000 cache 20; 

create sequence SEQ_CHATBOT
	start with 1000 cache 20; 

create sequence SEQ_INTENT
	start with 1000 cache 20; 

create sequence SEQ_INTENT_TRAINING_SENTENCE
	start with 1000 cache 20; 

create sequence SEQ_TRAINING
	start with 1000 cache 20; 

create sequence SEQ_UTTER_TEXT
	start with 1000 cache 20; 


-- ============================================================
--   Table : ACTION                                        
-- ============================================================
create table ACTION
(
    ACT_ID      	 NUMERIC     	not null,
    TITLE       	 VARCHAR(100)	not null,
    BOT_ID      	 NUMERIC     	not null,
    constraint PK_ACTION primary key (ACT_ID)
);

comment on column ACTION.ACT_ID is
'ID';

comment on column ACTION.TITLE is
'Text';

comment on column ACTION.BOT_ID is
'Chatbot';

-- ============================================================
--   Table : CHATBOT                                        
-- ============================================================
create table CHATBOT
(
    BOT_ID      	 NUMERIC     	not null,
    NAME        	 VARCHAR(100)	not null,
    constraint PK_CHATBOT primary key (BOT_ID)
);

comment on column CHATBOT.BOT_ID is
'ID';

comment on column CHATBOT.NAME is
'Name';

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
'Titre';

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
'Intent';

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
'Valide';

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
    ACT_ID      	 NUMERIC     	,
    constraint PK_UTTER_TEXT primary key (UTX_ID)
);

comment on column UTTER_TEXT.UTX_ID is
'ID';

comment on column UTTER_TEXT.TEXT is
'Text';

comment on column UTTER_TEXT.INT_ID is
'Intent';

comment on column UTTER_TEXT.ACT_ID is
'Action';


alter table ACTION
	add constraint FK_ACTION_CHATBOT_CHATBOT foreign key (BOT_ID)
	references CHATBOT (BOT_ID);

create index ACTION_CHATBOT_CHATBOT_FK on ACTION (BOT_ID asc);

alter table UTTER_TEXT
	add constraint FK_ACTION_UTTER_TEXT_ACTION foreign key (ACT_ID)
	references ACTION (ACT_ID);

create index ACTION_UTTER_TEXT_ACTION_FK on UTTER_TEXT (ACT_ID asc);

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


