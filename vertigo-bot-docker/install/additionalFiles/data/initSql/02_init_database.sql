\c chatbot chatbot

-- ============================================================
--   SGBD      		  :  Postgres                     
-- ============================================================

-- ============================================================
--   Drop                                       
-- ============================================================
drop table IF EXISTS CHA_PER_RIGHTS cascade;
drop table IF EXISTS CHATBOT cascade;
drop sequence IF EXISTS SEQ_CHATBOT;
drop table IF EXISTS CHATBOT_NODE cascade;
drop sequence IF EXISTS SEQ_CHATBOT_NODE;
drop table IF EXISTS GROUPS cascade;
drop sequence IF EXISTS SEQ_GROUPS;
drop table IF EXISTS MEDIA_FILE_INFO cascade;
drop sequence IF EXISTS SEQ_MEDIA_FILE_INFO;
drop table IF EXISTS NLU_TRAINING_SENTENCE cascade;
drop sequence IF EXISTS SEQ_NLU_TRAINING_SENTENCE;
drop table IF EXISTS PERSON cascade;
drop sequence IF EXISTS SEQ_PERSON;
drop table IF EXISTS PERSON_ROLE cascade;
drop table IF EXISTS RESPONSE_BUTTON cascade;
drop sequence IF EXISTS SEQ_RESPONSE_BUTTON;
drop table IF EXISTS RESPONSE_TYPE cascade;
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

create sequence SEQ_CHATBOT_NODE
	start with 1000 cache 20; 

create sequence SEQ_GROUPS
	start with 1000 cache 20; 

create sequence SEQ_MEDIA_FILE_INFO
	start with 1000 cache 20; 

create sequence SEQ_NLU_TRAINING_SENTENCE
	start with 1000 cache 20; 

create sequence SEQ_PERSON
	start with 1000 cache 20; 


create sequence SEQ_RESPONSE_BUTTON
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
    DESCRIPTION 	 TEXT        	not null,
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

comment on column CHATBOT.DESCRIPTION is
'Description';

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
--   Table : CHATBOT_NODE                                        
-- ============================================================
create table CHATBOT_NODE
(
    NOD_ID      	 NUMERIC     	not null,
    NAME        	 VARCHAR(100)	not null,
    URL         	 TEXT        	not null,
    IS_DEV      	 bool        	not null,
    COLOR       	 VARCHAR(20) 	not null,
    API_KEY     	 VARCHAR(100)	not null,
    BOT_ID      	 NUMERIC     	not null,
    TRA_ID      	 NUMERIC     	,
    constraint PK_CHATBOT_NODE primary key (NOD_ID)
);

comment on column CHATBOT_NODE.NOD_ID is
'ID';

comment on column CHATBOT_NODE.NAME is
'Name';

comment on column CHATBOT_NODE.URL is
'URL';

comment on column CHATBOT_NODE.IS_DEV is
'Dev node';

comment on column CHATBOT_NODE.COLOR is
'Color';

comment on column CHATBOT_NODE.API_KEY is
'ApiKey';

comment on column CHATBOT_NODE.BOT_ID is
'Chatbot';

comment on column CHATBOT_NODE.TRA_ID is
'Loaded model';

-- ============================================================
--   Table : GROUPS                                        
-- ============================================================
create table GROUPS
(
    GRP_ID      	 NUMERIC     	not null,
    NAME        	 VARCHAR(100)	,
    constraint PK_GROUPS primary key (GRP_ID)
);

comment on column GROUPS.GRP_ID is
'Id';

comment on column GROUPS.NAME is
'Name';

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
--   Table : PERSON                                        
-- ============================================================
create table PERSON
(
    PER_ID      	 NUMERIC     	not null,
    LOGIN       	 VARCHAR(100)	not null,
    NAME        	 VARCHAR(100)	not null,
    PASSWORD    	 VARCHAR(100)	not null,
    GRP_ID      	 NUMERIC     	,
    ROL_CD      	 VARCHAR(100)	not null,
    constraint PK_PERSON primary key (PER_ID)
);

comment on column PERSON.PER_ID is
'Id';

comment on column PERSON.LOGIN is
'Login';

comment on column PERSON.NAME is
'Name';

comment on column PERSON.PASSWORD is
'Password';

comment on column PERSON.GRP_ID is
'Group';

comment on column PERSON.ROL_CD is
'Role';

-- ============================================================
--   Table : PERSON_ROLE                                        
-- ============================================================
create table PERSON_ROLE
(
    ROL_CD      	 VARCHAR(100)	not null,
    LABEL       	 VARCHAR(100)	not null,
    SORT_ORDER  	 NUMERIC     	not null,
    constraint PK_PERSON_ROLE primary key (ROL_CD)
);

comment on column PERSON_ROLE.ROL_CD is
'Code';

comment on column PERSON_ROLE.LABEL is
'Label';

comment on column PERSON_ROLE.SORT_ORDER is
'Order';

-- ============================================================
--   Table : RESPONSE_BUTTON                                        
-- ============================================================
create table RESPONSE_BUTTON
(
    BTN_ID      	 NUMERIC     	not null,
    TEXT        	 TEXT        	not null,
    SMT_ID      	 NUMERIC     	,
    SMT_ID_RESPONSE	 NUMERIC     	not null,
    BOT_ID_WELCOME	 NUMERIC     	,
    BOT_ID_DEFAULT	 NUMERIC     	,
    constraint PK_RESPONSE_BUTTON primary key (BTN_ID)
);

comment on column RESPONSE_BUTTON.BTN_ID is
'ID';

comment on column RESPONSE_BUTTON.TEXT is
'Text';

comment on column RESPONSE_BUTTON.SMT_ID is
'SmallTalk';

comment on column RESPONSE_BUTTON.SMT_ID_RESPONSE is
'SmallTalkResponse';

comment on column RESPONSE_BUTTON.BOT_ID_WELCOME is
'welcome buttons';

comment on column RESPONSE_BUTTON.BOT_ID_DEFAULT is
'Default buttons';

-- ============================================================
--   Table : RESPONSE_TYPE                                        
-- ============================================================
create table RESPONSE_TYPE
(
    RTY_ID      	 VARCHAR(100)	not null,
    LABEL       	 VARCHAR(100)	not null,
    SORT_ORDER  	 NUMERIC     	not null,
    constraint PK_RESPONSE_TYPE primary key (RTY_ID)
);

comment on column RESPONSE_TYPE.RTY_ID is
'ID';

comment on column RESPONSE_TYPE.LABEL is
'Title';

comment on column RESPONSE_TYPE.SORT_ORDER is
'Order';

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
    RTY_ID      	 VARCHAR(100)	not null,
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

comment on column SMALL_TALK.RTY_ID is
'Response type';

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
    INFOS       	 TEXT        	,
    WARNINGS    	 TEXT        	,
    NLU_THRESHOLD	 NUMERIC(3,2)	not null,
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

comment on column TRAINING.INFOS is
'Informations';

comment on column TRAINING.WARNINGS is
'Warnings';

comment on column TRAINING.NLU_THRESHOLD is
'NLU Threshold';

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
    TEXT        	 TEXT        	not null,
    SMT_ID      	 NUMERIC     	,
    constraint PK_UTTER_TEXT primary key (UTT_ID)
);

comment on column UTTER_TEXT.UTT_ID is
'ID';

comment on column UTTER_TEXT.TEXT is
'Text';

comment on column UTTER_TEXT.SMT_ID is
'SmallTalk';


alter table RESPONSE_BUTTON
	add constraint FK_CHATBOT_DEFAULT_BUTTONS_CHATBOT foreign key (BOT_ID_DEFAULT)
	references CHATBOT (BOT_ID);

create index CHATBOT_DEFAULT_BUTTONS_CHATBOT_FK on RESPONSE_BUTTON (BOT_ID_DEFAULT asc);

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

alter table RESPONSE_BUTTON
	add constraint FK_CHATBOT_WELCOME_BUTTONS_CHATBOT foreign key (BOT_ID_WELCOME)
	references CHATBOT (BOT_ID);

create index CHATBOT_WELCOME_BUTTONS_CHATBOT_FK on RESPONSE_BUTTON (BOT_ID_WELCOME asc);

alter table CHATBOT_NODE
	add constraint FK_NODE_CHATBOT_CHATBOT foreign key (BOT_ID)
	references CHATBOT (BOT_ID);

create index NODE_CHATBOT_CHATBOT_FK on CHATBOT_NODE (BOT_ID asc);

alter table CHATBOT_NODE
	add constraint FK_NODE_TRAINING_TRAINING foreign key (TRA_ID)
	references TRAINING (TRA_ID);

create index NODE_TRAINING_TRAINING_FK on CHATBOT_NODE (TRA_ID asc);

alter table PERSON
	add constraint FK_PERSON_GROUPS_GROUPS foreign key (GRP_ID)
	references GROUPS (GRP_ID);

create index PERSON_GROUPS_GROUPS_FK on PERSON (GRP_ID asc);

alter table PERSON
	add constraint FK_PERSON_ROLE_PERSON_ROLE foreign key (ROL_CD)
	references PERSON_ROLE (ROL_CD);

create index PERSON_ROLE_PERSON_ROLE_FK on PERSON (ROL_CD asc);

alter table RESPONSE_BUTTON
	add constraint FK_RESPONSE_BUTTON_SMALL_TALK_RESPONSE_SMALL_TALK foreign key (SMT_ID_RESPONSE)
	references SMALL_TALK (SMT_ID);

create index RESPONSE_BUTTON_SMALL_TALK_RESPONSE_SMALL_TALK_FK on RESPONSE_BUTTON (SMT_ID_RESPONSE asc);

alter table SMALL_TALK
	add constraint FK_SMALL_TALK_CHATBOT_CHATBOT foreign key (BOT_ID)
	references CHATBOT (BOT_ID);

create index SMALL_TALK_CHATBOT_CHATBOT_FK on SMALL_TALK (BOT_ID asc);

alter table NLU_TRAINING_SENTENCE
	add constraint FK_SMALL_TALK_NLU_TRAINING_SENTENCE_SMALL_TALK foreign key (SMT_ID)
	references SMALL_TALK (SMT_ID);

create index SMALL_TALK_NLU_TRAINING_SENTENCE_SMALL_TALK_FK on NLU_TRAINING_SENTENCE (SMT_ID asc);

alter table RESPONSE_BUTTON
	add constraint FK_SMALL_TALK_RESPONSE_BUTTONS_SMALL_TALK foreign key (SMT_ID)
	references SMALL_TALK (SMT_ID);

create index SMALL_TALK_RESPONSE_BUTTONS_SMALL_TALK_FK on RESPONSE_BUTTON (SMT_ID asc);

alter table SMALL_TALK
	add constraint FK_SMALL_TALK_RESPONSE_TYPE_RESPONSE_TYPE foreign key (RTY_ID)
	references RESPONSE_TYPE (RTY_ID);

create index SMALL_TALK_RESPONSE_TYPE_RESPONSE_TYPE_FK on SMALL_TALK (RTY_ID asc);

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


create table CHA_PER_RIGHTS
(
	PER_ID      	 NUMERIC     	 not null,
	BOT_ID      	 NUMERIC     	 not null,
	constraint PK_CHA_PER_RIGHTS primary key (PER_ID, BOT_ID),
	constraint FK_hatbotPerson_PERSON 
		foreign key(PER_ID)
		references PERSON (PER_ID),
	constraint FK_hatbotPerson_CHATBOT 
		foreign key(BOT_ID)
		references CHATBOT (BOT_ID)
);

create index hatbotPerson_PERSON_FK on CHA_PER_RIGHTS (PER_ID asc);

create index hatbotPerson_CHATBOT_FK on CHA_PER_RIGHTS (BOT_ID asc);


-- ============================================================
--   Insert MasterData values : PERSON_ROLE                                        
-- ============================================================
insert into PERSON_ROLE(ROL_CD, LABEL, SORT_ORDER) values ('RAdmin', 'Administrateur', '1');
insert into PERSON_ROLE(ROL_CD, LABEL, SORT_ORDER) values ('RUser', 'User', '2');


-- ============================================================
--   Insert MasterData values : RESPONSE_TYPE                                        
-- ============================================================
insert into RESPONSE_TYPE(RTY_ID, LABEL, SORT_ORDER) values ('RICH_TEXT', 'Rich text', '1');
insert into RESPONSE_TYPE(RTY_ID, LABEL, SORT_ORDER) values ('RANDOM_TEXT', 'Random text', '2');


-- ============================================================
--   Add default user                                        
-- ============================================================

INSERT INTO public.person VALUES (1, 'Admin', 'Administrator', 'KbQOxaZn0cMh99Gxw2YKoCCBj-XDiuBvx4w2BS8Dd-r954wKxR38s=', null, 'RAdmin');
