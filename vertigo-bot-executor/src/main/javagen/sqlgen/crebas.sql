-- ============================================================
--   SGBD      		  :  H2                     
-- ============================================================

-- ============================================================
--   Drop                                       
-- ============================================================
drop table IF EXISTS ACTION cascade;
drop sequence IF EXISTS SEQ_ACTION;
drop table IF EXISTS INTENT cascade;
drop sequence IF EXISTS SEQ_INTENT;
drop table IF EXISTS INTENT_TRAINING_SENTENCE cascade;
drop sequence IF EXISTS SEQ_INTENT_TRAINING_SENTENCE;
drop table IF EXISTS UTTER_TEXT cascade;
drop sequence IF EXISTS SEQ_UTTER_TEXT;




-- ============================================================
--   Sequences                                      
-- ============================================================
create sequence SEQ_ACTION
	start with 1000 cache 20; 

create sequence SEQ_INTENT
	start with 1000 cache 20; 

create sequence SEQ_INTENT_TRAINING_SENTENCE
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
    constraint PK_ACTION primary key (ACT_ID)
);

comment on column ACTION.ACT_ID is
'ID';

comment on column ACTION.TITLE is
'Text';

-- ============================================================
--   Table : INTENT                                        
-- ============================================================
create table INTENT
(
    INT_ID      	 NUMERIC     	not null,
    TITLE       	 VARCHAR(100)	not null,
    IS_SMALL_TALK	 bool        	not null,
    constraint PK_INTENT primary key (INT_ID)
);

comment on column INTENT.INT_ID is
'ID';

comment on column INTENT.TITLE is
'Titre';

comment on column INTENT.IS_SMALL_TALK is
'SmallTalk';

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


alter table UTTER_TEXT
	add constraint FK_ACTION_UTTER_TEXT_ACTION foreign key (ACT_ID)
	references ACTION (ACT_ID);

create index ACTION_UTTER_TEXT_ACTION_FK on UTTER_TEXT (ACT_ID asc);

alter table INTENT_TRAINING_SENTENCE
	add constraint FK_INTENT_INTENT_TRAINING_SENTENCE_INTENT foreign key (INT_ID)
	references INTENT (INT_ID);

create index INTENT_INTENT_TRAINING_SENTENCE_INTENT_FK on INTENT_TRAINING_SENTENCE (INT_ID asc);

alter table UTTER_TEXT
	add constraint FK_INTENT_UTTER_TEXT_INTENT foreign key (INT_ID)
	references INTENT (INT_ID);

create index INTENT_UTTER_TEXT_INTENT_FK on UTTER_TEXT (INT_ID asc);


