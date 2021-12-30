-- ============================================================
--   SGBD      		  :  Postgres                     
-- ============================================================

-- ============================================================
--   Drop                                       
-- ============================================================
drop table IF EXISTS TOPIC_TOPIC_LABEL cascade;
drop table IF EXISTS CHATBOT cascade;
drop sequence IF EXISTS SEQ_CHATBOT;
drop table IF EXISTS CHATBOT_CUSTOM_CONFIG cascade;
drop sequence IF EXISTS SEQ_CHATBOT_CUSTOM_CONFIG;
drop table IF EXISTS CHATBOT_NODE cascade;
drop sequence IF EXISTS SEQ_CHATBOT_NODE;
drop table IF EXISTS CHATBOT_PROFILES cascade;
drop table IF EXISTS CONTEXT_VALUE cascade;
drop sequence IF EXISTS SEQ_CONTEXT_VALUE;
drop table IF EXISTS DICTIONARY_ENTITY cascade;
drop sequence IF EXISTS SEQ_DICTIONARY_ENTITY;
drop table IF EXISTS GROUPS cascade;
drop sequence IF EXISTS SEQ_GROUPS;
drop table IF EXISTS HISTORY cascade;
drop sequence IF EXISTS SEQ_HISTORY;
drop table IF EXISTS HISTORY_ACTION cascade;
drop table IF EXISTS KIND_TOPIC cascade;
drop table IF EXISTS MEDIA_FILE_INFO cascade;
drop sequence IF EXISTS SEQ_MEDIA_FILE_INFO;
drop table IF EXISTS NLU_TRAINING_SENTENCE cascade;
drop sequence IF EXISTS SEQ_NLU_TRAINING_SENTENCE;
drop table IF EXISTS PERSON cascade;
drop sequence IF EXISTS SEQ_PERSON;
drop table IF EXISTS PERSON_ROLE cascade;
drop table IF EXISTS PROFIL_PER_CHATBOT cascade;
drop sequence IF EXISTS SEQ_PROFIL_PER_CHATBOT;
drop table IF EXISTS RESPONSE_BUTTON cascade;
drop sequence IF EXISTS SEQ_RESPONSE_BUTTON;
drop table IF EXISTS RESPONSE_TYPE cascade;
drop table IF EXISTS SAVED_TRAINING cascade;
drop sequence IF EXISTS SEQ_SAVED_TRAINING;
drop table IF EXISTS SCRIPT_INTENTION cascade;
drop sequence IF EXISTS SEQ_SCRIPT_INTENTION;
drop table IF EXISTS SMALL_TALK cascade;
drop sequence IF EXISTS SEQ_SMALL_TALK;
drop table IF EXISTS SYNONYM cascade;
drop sequence IF EXISTS SEQ_SYNONYM;
drop table IF EXISTS TOPIC cascade;
drop sequence IF EXISTS SEQ_TOPIC;
drop table IF EXISTS TOPIC_CATEGORY cascade;
drop sequence IF EXISTS SEQ_TOPIC_CATEGORY;
drop table IF EXISTS TOPIC_LABEL cascade;
drop sequence IF EXISTS SEQ_TOPIC_LABEL;
drop table IF EXISTS TRAINING cascade;
drop sequence IF EXISTS SEQ_TRAINING;
drop table IF EXISTS TRAINING_STATUS cascade;
drop table IF EXISTS TYPE_EXPORT_ANALYTICS cascade;
drop table IF EXISTS TYPE_TOPIC cascade;
drop table IF EXISTS UNKNOWN_SENTENCE_DETAIL cascade;
drop sequence IF EXISTS SEQ_UNKNOWN_SENTENCE_DETAIL;
drop table IF EXISTS UNKNOWN_SENTENCE_STATUS cascade;
drop table IF EXISTS UTTER_TEXT cascade;
drop sequence IF EXISTS SEQ_UTTER_TEXT;




-- ============================================================
--   Sequences                                      
-- ============================================================
create sequence SEQ_CHATBOT
	start with 1000 cache 20; 

create sequence SEQ_CHATBOT_CUSTOM_CONFIG
	start with 1000 cache 20; 

create sequence SEQ_CHATBOT_NODE
	start with 1000 cache 20; 


create sequence SEQ_CONTEXT_VALUE
	start with 1000 cache 20; 

create sequence SEQ_DICTIONARY_ENTITY
	start with 1000 cache 20; 

create sequence SEQ_GROUPS
	start with 1000 cache 20; 

create sequence SEQ_HISTORY
	start with 1000 cache 20; 



create sequence SEQ_MEDIA_FILE_INFO
	start with 1000 cache 20; 

create sequence SEQ_NLU_TRAINING_SENTENCE
	start with 1000 cache 20; 

create sequence SEQ_PERSON
	start with 1000 cache 20; 


create sequence SEQ_PROFIL_PER_CHATBOT
	start with 1000 cache 20; 

create sequence SEQ_RESPONSE_BUTTON
	start with 1000 cache 20; 


create sequence SEQ_SAVED_TRAINING
	start with 1000 cache 20; 

create sequence SEQ_SCRIPT_INTENTION
	start with 1000 cache 20; 

create sequence SEQ_SMALL_TALK
	start with 1000 cache 20; 

create sequence SEQ_SYNONYM
	start with 1000 cache 20; 

create sequence SEQ_TOPIC
	start with 1000 cache 20; 

create sequence SEQ_TOPIC_CATEGORY
	start with 1000 cache 20; 

create sequence SEQ_TOPIC_LABEL
	start with 1000 cache 20; 

create sequence SEQ_TRAINING
	start with 1000 cache 20; 




create sequence SEQ_UNKNOWN_SENTENCE_DETAIL
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

-- ============================================================
--   Table : CHATBOT_CUSTOM_CONFIG                                        
-- ============================================================
create table CHATBOT_CUSTOM_CONFIG
(
    CCC_ID      	 NUMERIC     	not null,
    RATING      	 bool        	not null,
    RATING_MESSAGE	 VARCHAR(100)	,
    BOT_ID      	 NUMERIC     	not null,
    constraint PK_CHATBOT_CUSTOM_CONFIG primary key (CCC_ID)
);

comment on column CHATBOT_CUSTOM_CONFIG.CCC_ID is
'Context value id';

comment on column CHATBOT_CUSTOM_CONFIG.RATING is
'Rating';

comment on column CHATBOT_CUSTOM_CONFIG.RATING_MESSAGE is
'Rating message';

comment on column CHATBOT_CUSTOM_CONFIG.BOT_ID is
'Chatbot';

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
    IS_UP_TO_DATE	 bool        	not null,
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

comment on column CHATBOT_NODE.IS_UP_TO_DATE is
'Is up to date';

comment on column CHATBOT_NODE.BOT_ID is
'Chatbot';

comment on column CHATBOT_NODE.TRA_ID is
'Loaded model';

-- ============================================================
--   Table : CHATBOT_PROFILES                                        
-- ============================================================
create table CHATBOT_PROFILES
(
    CHP_CD      	 VARCHAR(100)	not null,
    LABEL       	 VARCHAR(100)	not null,
    SORT_ORDER  	 NUMERIC     	not null,
    constraint PK_CHATBOT_PROFILES primary key (CHP_CD)
);

comment on column CHATBOT_PROFILES.CHP_CD is
'ID';

comment on column CHATBOT_PROFILES.LABEL is
'Title';

comment on column CHATBOT_PROFILES.SORT_ORDER is
'Order';

-- ============================================================
--   Table : CONTEXT_VALUE                                        
-- ============================================================
create table CONTEXT_VALUE
(
    CVA_ID      	 NUMERIC     	not null,
    LABEL       	 VARCHAR(100)	not null,
    XPATH       	 VARCHAR(100)	not null,
    BOT_ID      	 NUMERIC     	not null,
    constraint PK_CONTEXT_VALUE primary key (CVA_ID)
);

comment on column CONTEXT_VALUE.CVA_ID is
'Context value id';

comment on column CONTEXT_VALUE.LABEL is
'Label';

comment on column CONTEXT_VALUE.XPATH is
'Xpath';

comment on column CONTEXT_VALUE.BOT_ID is
'Chatbot';

-- ============================================================
--   Table : DICTIONARY_ENTITY                                        
-- ============================================================
create table DICTIONARY_ENTITY
(
    DIC_ENT_ID  	 NUMERIC     	not null,
    LABEL       	 VARCHAR(100)	not null,
    BOT_ID      	 NUMERIC     	not null,
    constraint PK_DICTIONARY_ENTITY primary key (DIC_ENT_ID)
);

comment on column DICTIONARY_ENTITY.DIC_ENT_ID is
'Dictionary entity id';

comment on column DICTIONARY_ENTITY.LABEL is
'Label';

comment on column DICTIONARY_ENTITY.BOT_ID is
'Chatbot';

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
--   Table : HISTORY                                        
-- ============================================================
create table HISTORY
(
    HIST_ID     	 NUMERIC     	not null,
    DATE        	 TIMESTAMP   	not null,
    CLASS_NAME  	 VARCHAR(100)	not null,
    MESSAGE     	 VARCHAR(100)	not null,
    USER_NAME   	 VARCHAR(100)	not null,
    HAC_CD      	 VARCHAR(100)	not null,
    BOT_ID      	 NUMERIC     	not null,
    constraint PK_HISTORY primary key (HIST_ID)
);

comment on column HISTORY.HIST_ID is
'History id';

comment on column HISTORY.DATE is
'Date';

comment on column HISTORY.CLASS_NAME is
'Class name';

comment on column HISTORY.MESSAGE is
'Label';

comment on column HISTORY.USER_NAME is
'User';

comment on column HISTORY.HAC_CD is
'Action';

comment on column HISTORY.BOT_ID is
'Chatbot';

-- ============================================================
--   Table : HISTORY_ACTION                                        
-- ============================================================
create table HISTORY_ACTION
(
    HAC_CD      	 VARCHAR(100)	not null,
    LABEL       	 VARCHAR(100)	not null,
    LABEL_FR    	 VARCHAR(100)	not null,
    constraint PK_HISTORY_ACTION primary key (HAC_CD)
);

comment on column HISTORY_ACTION.HAC_CD is
'ID';

comment on column HISTORY_ACTION.LABEL is
'Title';

comment on column HISTORY_ACTION.LABEL_FR is
'TitleFr';

-- ============================================================
--   Table : KIND_TOPIC                                        
-- ============================================================
create table KIND_TOPIC
(
    KTO_CD      	 VARCHAR(100)	not null,
    LABEL       	 VARCHAR(100)	not null,
    DESCRIPTION 	 VARCHAR(100)	not null,
    DEFAULT_TEXT	 TEXT        	not null,
    constraint PK_KIND_TOPIC primary key (KTO_CD)
);

comment on column KIND_TOPIC.KTO_CD is
'ID';

comment on column KIND_TOPIC.LABEL is
'Label';

comment on column KIND_TOPIC.DESCRIPTION is
'Description';

comment on column KIND_TOPIC.DEFAULT_TEXT is
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
    TOP_ID      	 NUMERIC     	not null,
    constraint PK_NLU_TRAINING_SENTENCE primary key (NTS_ID)
);

comment on column NLU_TRAINING_SENTENCE.NTS_ID is
'ID';

comment on column NLU_TRAINING_SENTENCE.TEXT is
'Text';

comment on column NLU_TRAINING_SENTENCE.TOP_ID is
'SmallTalk';

-- ============================================================
--   Table : PERSON                                        
-- ============================================================
create table PERSON
(
    PER_ID      	 NUMERIC     	not null,
    LOGIN       	 VARCHAR(100)	not null,
    NAME        	 VARCHAR(100)	not null,
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
--   Table : PROFIL_PER_CHATBOT                                        
-- ============================================================
create table PROFIL_PER_CHATBOT
(
    CHP_ID      	 NUMERIC     	not null,
    BOT_ID      	 NUMERIC     	not null,
    PER_ID      	 NUMERIC     	not null,
    CHP_CD      	 VARCHAR(100)	not null,
    constraint PK_PROFIL_PER_CHATBOT primary key (CHP_ID)
);

comment on column PROFIL_PER_CHATBOT.CHP_ID is
'ID';

comment on column PROFIL_PER_CHATBOT.BOT_ID is
'Chatbot';

comment on column PROFIL_PER_CHATBOT.PER_ID is
'Person';

comment on column PROFIL_PER_CHATBOT.CHP_CD is
'Profil pour un chatbot';

-- ============================================================
--   Table : RESPONSE_BUTTON                                        
-- ============================================================
create table RESPONSE_BUTTON
(
    BTN_ID      	 NUMERIC     	not null,
    TEXT        	 TEXT        	not null,
    SMT_ID      	 NUMERIC     	,
    TOP_ID_RESPONSE	 NUMERIC     	not null,
    constraint PK_RESPONSE_BUTTON primary key (BTN_ID)
);

comment on column RESPONSE_BUTTON.BTN_ID is
'ID';

comment on column RESPONSE_BUTTON.TEXT is
'Text';

comment on column RESPONSE_BUTTON.SMT_ID is
'SmallTalk';

comment on column RESPONSE_BUTTON.TOP_ID_RESPONSE is
'TopicResponse';

-- ============================================================
--   Table : RESPONSE_TYPE                                        
-- ============================================================
create table RESPONSE_TYPE
(
    RTY_ID      	 VARCHAR(100)	not null,
    LABEL       	 VARCHAR(100)	not null,
    LABEL_FR    	 VARCHAR(100)	not null,
    SORT_ORDER  	 NUMERIC     	not null,
    constraint PK_RESPONSE_TYPE primary key (RTY_ID)
);

comment on column RESPONSE_TYPE.RTY_ID is
'ID';

comment on column RESPONSE_TYPE.LABEL is
'Title';

comment on column RESPONSE_TYPE.LABEL_FR is
'TitleFR';

comment on column RESPONSE_TYPE.SORT_ORDER is
'Order';

-- ============================================================
--   Table : SAVED_TRAINING                                        
-- ============================================================
create table SAVED_TRAINING
(
    SAVED_TRA_ID	 NUMERIC     	not null,
    NAME        	 VARCHAR(100)	not null,
    CREATION_TIME	 TIMESTAMP   	not null,
    DESCRIPTION 	 TEXT        	,
    BOT_EXPORT  	 JSONB       	not null,
    TRA_ID      	 NUMERIC     	not null,
    BOT_ID      	 NUMERIC     	not null,
    constraint PK_SAVED_TRAINING primary key (SAVED_TRA_ID)
);

comment on column SAVED_TRAINING.SAVED_TRA_ID is
'ID';

comment on column SAVED_TRAINING.NAME is
'Name';

comment on column SAVED_TRAINING.CREATION_TIME is
'Creation time';

comment on column SAVED_TRAINING.DESCRIPTION is
'Description';

comment on column SAVED_TRAINING.BOT_EXPORT is
'Bot Export';

comment on column SAVED_TRAINING.TRA_ID is
'Training';

comment on column SAVED_TRAINING.BOT_ID is
'Bot';

-- ============================================================
--   Table : SCRIPT_INTENTION                                        
-- ============================================================
create table SCRIPT_INTENTION
(
    SIN_ID      	 NUMERIC     	not null,
    SCRIPT      	 TEXT        	,
    TOP_ID      	 NUMERIC     	not null,
    constraint PK_SCRIPT_INTENTION primary key (SIN_ID)
);

comment on column SCRIPT_INTENTION.SIN_ID is
'ID';

comment on column SCRIPT_INTENTION.SCRIPT is
'Script';

comment on column SCRIPT_INTENTION.TOP_ID is
'Topic';

-- ============================================================
--   Table : SMALL_TALK                                        
-- ============================================================
create table SMALL_TALK
(
    SMT_ID      	 NUMERIC     	not null,
    IS_END      	 bool        	not null,
    TOP_ID      	 NUMERIC     	not null,
    RTY_ID      	 VARCHAR(100)	not null,
    constraint PK_SMALL_TALK primary key (SMT_ID)
);

comment on column SMALL_TALK.SMT_ID is
'ID';

comment on column SMALL_TALK.IS_END is
'Is conversation over ?';

comment on column SMALL_TALK.TOP_ID is
'Topic';

comment on column SMALL_TALK.RTY_ID is
'Response type';

-- ============================================================
--   Table : SYNONYM                                        
-- ============================================================
create table SYNONYM
(
    SYN_ID      	 NUMERIC     	not null,
    LABEL       	 VARCHAR(100)	not null,
    BOT_ID      	 NUMERIC     	not null,
    DIC_ENT_ID  	 NUMERIC     	not null,
    constraint PK_SYNONYM primary key (SYN_ID)
);

comment on column SYNONYM.SYN_ID is
'Synonym id';

comment on column SYNONYM.LABEL is
'Label';

comment on column SYNONYM.BOT_ID is
'Chatbot';

comment on column SYNONYM.DIC_ENT_ID is
'DictionaryEntity';

-- ============================================================
--   Table : TOPIC                                        
-- ============================================================
create table TOPIC
(
    TOP_ID      	 NUMERIC     	not null,
    TITLE       	 VARCHAR(100)	not null,
    DESCRIPTION 	 VARCHAR(100)	,
    IS_ENABLED  	 bool        	not null,
    CODE        	 VARCHAR(100)	not null,
    TTO_CD      	 VARCHAR(100)	not null,
    BOT_ID      	 NUMERIC     	not null,
    TOP_CAT_ID  	 NUMERIC     	not null,
    KTO_CD      	 VARCHAR(100)	not null,
    constraint PK_TOPIC primary key (TOP_ID)
);

comment on column TOPIC.TOP_ID is
'ID';

comment on column TOPIC.TITLE is
'Title';

comment on column TOPIC.DESCRIPTION is
'Description';

comment on column TOPIC.IS_ENABLED is
'Enabled';

comment on column TOPIC.CODE is
'Code';

comment on column TOPIC.TTO_CD is
'Type du topic';

comment on column TOPIC.BOT_ID is
'Chatbot';

comment on column TOPIC.TOP_CAT_ID is
'Topic';

comment on column TOPIC.KTO_CD is
'Kind of topic';

-- ============================================================
--   Table : TOPIC_CATEGORY                                        
-- ============================================================
create table TOPIC_CATEGORY
(
    TOP_CAT_ID  	 NUMERIC     	not null,
    CODE        	 VARCHAR(100)	not null,
    LABEL       	 VARCHAR(100)	not null,
    LEVEL       	 NUMERIC     	,
    IS_ENABLED  	 bool        	not null,
    IS_TECHNICAL	 bool        	not null,
    BOT_ID      	 NUMERIC     	not null,
    constraint PK_TOPIC_CATEGORY primary key (TOP_CAT_ID)
);

comment on column TOPIC_CATEGORY.TOP_CAT_ID is
'Topic category id';

comment on column TOPIC_CATEGORY.CODE is
'Code';

comment on column TOPIC_CATEGORY.LABEL is
'Topic category label';

comment on column TOPIC_CATEGORY.LEVEL is
'Category level';

comment on column TOPIC_CATEGORY.IS_ENABLED is
'Enabled';

comment on column TOPIC_CATEGORY.IS_TECHNICAL is
'Technical';

comment on column TOPIC_CATEGORY.BOT_ID is
'Chatbot';

-- ============================================================
--   Table : TOPIC_LABEL                                        
-- ============================================================
create table TOPIC_LABEL
(
    LABEL_ID    	 NUMERIC     	not null,
    LABEL       	 VARCHAR(100)	not null,
    BOT_ID      	 NUMERIC     	not null,
    constraint PK_TOPIC_LABEL primary key (LABEL_ID)
);

comment on column TOPIC_LABEL.LABEL_ID is
'Label id';

comment on column TOPIC_LABEL.LABEL is
'Label label';

comment on column TOPIC_LABEL.BOT_ID is
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
    LOG         	 TEXT        	,
    INFOS       	 TEXT        	,
    WARNINGS    	 TEXT        	,
    NLU_THRESHOLD	 NUMERIC(3,2)	not null,
    BOT_ID      	 NUMERIC     	not null,
    FIL_ID_MODEL	 NUMERIC     	,
    STR_CD      	 VARCHAR(100)	not null,
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

comment on column TRAINING.STR_CD is
'Status';

-- ============================================================
--   Table : TRAINING_STATUS                                        
-- ============================================================
create table TRAINING_STATUS
(
    STR_CD      	 VARCHAR(100)	not null,
    LABEL       	 VARCHAR(100)	not null,
    LABEL_FR    	 VARCHAR(100)	not null,
    constraint PK_TRAINING_STATUS primary key (STR_CD)
);

comment on column TRAINING_STATUS.STR_CD is
'ID';

comment on column TRAINING_STATUS.LABEL is
'Label';

comment on column TRAINING_STATUS.LABEL_FR is
'LabelFr';

-- ============================================================
--   Table : TYPE_EXPORT_ANALYTICS                                        
-- ============================================================
create table TYPE_EXPORT_ANALYTICS
(
    TEA_CD      	 VARCHAR(100)	not null,
    LABEL       	 VARCHAR(100)	not null,
    LABEL_FR    	 VARCHAR(100)	not null,
    constraint PK_TYPE_EXPORT_ANALYTICS primary key (TEA_CD)
);

comment on column TYPE_EXPORT_ANALYTICS.TEA_CD is
'Code';

comment on column TYPE_EXPORT_ANALYTICS.LABEL is
'Title';

comment on column TYPE_EXPORT_ANALYTICS.LABEL_FR is
'Titre';

-- ============================================================
--   Table : TYPE_TOPIC                                        
-- ============================================================
create table TYPE_TOPIC
(
    TTO_CD      	 VARCHAR(100)	not null,
    LABEL       	 VARCHAR(100)	not null,
    LABEL_FR    	 VARCHAR(100)	not null,
    constraint PK_TYPE_TOPIC primary key (TTO_CD)
);

comment on column TYPE_TOPIC.TTO_CD is
'ID';

comment on column TYPE_TOPIC.LABEL is
'Title';

comment on column TYPE_TOPIC.LABEL_FR is
'TitleFr';

-- ============================================================
--   Table : UNKNOWN_SENTENCE_DETAIL                                        
-- ============================================================
create table UNKNOWN_SENTENCE_DETAIL
(
    UNK_SE_ID   	 NUMERIC     	not null,
    DATE        	 TIMESTAMP   	,
    TEXT        	 VARCHAR(100)	,
    MODEL_NAME  	 VARCHAR(100)	,
    STATUS      	 VARCHAR(100)	not null,
    BOT_ID      	 NUMERIC     	not null,
    constraint PK_UNKNOWN_SENTENCE_DETAIL primary key (UNK_SE_ID)
);

comment on column UNKNOWN_SENTENCE_DETAIL.UNK_SE_ID is
'Unknown sentence id';

comment on column UNKNOWN_SENTENCE_DETAIL.DATE is
'Date';

comment on column UNKNOWN_SENTENCE_DETAIL.TEXT is
'User text';

comment on column UNKNOWN_SENTENCE_DETAIL.MODEL_NAME is
'Model Name';

comment on column UNKNOWN_SENTENCE_DETAIL.STATUS is
'Status';

comment on column UNKNOWN_SENTENCE_DETAIL.BOT_ID is
'Bot';

-- ============================================================
--   Table : UNKNOWN_SENTENCE_STATUS                                        
-- ============================================================
create table UNKNOWN_SENTENCE_STATUS
(
    STR_CD      	 VARCHAR(100)	not null,
    LABEL       	 VARCHAR(100)	not null,
    LABEL_FR    	 VARCHAR(100)	not null,
    constraint PK_UNKNOWN_SENTENCE_STATUS primary key (STR_CD)
);

comment on column UNKNOWN_SENTENCE_STATUS.STR_CD is
'ID';

comment on column UNKNOWN_SENTENCE_STATUS.LABEL is
'Label';

comment on column UNKNOWN_SENTENCE_STATUS.LABEL_FR is
'LabelFr';

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


alter table CHATBOT_CUSTOM_CONFIG
	add constraint FK_A_CHATBOT_CUSTOM_CONFIG_CHATBOT_CHATBOT foreign key (BOT_ID)
	references CHATBOT (BOT_ID);

create index A_CHATBOT_CUSTOM_CONFIG_CHATBOT_CHATBOT_FK on CHATBOT_CUSTOM_CONFIG (BOT_ID asc);

alter table CHATBOT
	add constraint FK_A_CHATBOT_MEDIA_FILE_INFO_MEDIA_FILE_INFO foreign key (FIL_ID_AVATAR)
	references MEDIA_FILE_INFO (FIL_ID);

create index A_CHATBOT_MEDIA_FILE_INFO_MEDIA_FILE_INFO_FK on CHATBOT (FIL_ID_AVATAR asc);

alter table CONTEXT_VALUE
	add constraint FK_A_CONTEXT_VALUE_CHATBOT_CHATBOT foreign key (BOT_ID)
	references CHATBOT (BOT_ID);

create index A_CONTEXT_VALUE_CHATBOT_CHATBOT_FK on CONTEXT_VALUE (BOT_ID asc);

alter table DICTIONARY_ENTITY
	add constraint FK_A_DICTIONARY_ENTITY_CHATBOT_CHATBOT foreign key (BOT_ID)
	references CHATBOT (BOT_ID);

create index A_DICTIONARY_ENTITY_CHATBOT_CHATBOT_FK on DICTIONARY_ENTITY (BOT_ID asc);

alter table HISTORY
	add constraint FK_A_HISTORY_CHATBOT_CHATBOT foreign key (BOT_ID)
	references CHATBOT (BOT_ID);

create index A_HISTORY_CHATBOT_CHATBOT_FK on HISTORY (BOT_ID asc);

alter table HISTORY
	add constraint FK_A_HISTORY_HISTORY_ACTION_HISTORY_ACTION foreign key (HAC_CD)
	references HISTORY_ACTION (HAC_CD);

create index A_HISTORY_HISTORY_ACTION_HISTORY_ACTION_FK on HISTORY (HAC_CD asc);

alter table CHATBOT_NODE
	add constraint FK_A_NODE_CHATBOT_CHATBOT foreign key (BOT_ID)
	references CHATBOT (BOT_ID);

create index A_NODE_CHATBOT_CHATBOT_FK on CHATBOT_NODE (BOT_ID asc);

alter table CHATBOT_NODE
	add constraint FK_A_NODE_TRAINING_TRAINING foreign key (TRA_ID)
	references TRAINING (TRA_ID);

create index A_NODE_TRAINING_TRAINING_FK on CHATBOT_NODE (TRA_ID asc);

alter table PROFIL_PER_CHATBOT
	add constraint FK_A_P_RROFIL_CHATBO_TO_CHATBOT_CHATBOT foreign key (BOT_ID)
	references CHATBOT (BOT_ID);

create index A_P_RROFIL_CHATBO_TO_CHATBOT_CHATBOT_FK on PROFIL_PER_CHATBOT (BOT_ID asc);

alter table PROFIL_PER_CHATBOT
	add constraint FK_A_P_RROFIL_CHATBOT_TO_CODE_PROFIL_CHATBOT_PROFILES foreign key (CHP_CD)
	references CHATBOT_PROFILES (CHP_CD);

create index A_P_RROFIL_CHATBOT_TO_CODE_PROFIL_CHATBOT_PROFILES_FK on PROFIL_PER_CHATBOT (CHP_CD asc);

alter table PROFIL_PER_CHATBOT
	add constraint FK_A_P_RROFIL_CHATBOT_TO_PERSON_PERSON foreign key (PER_ID)
	references PERSON (PER_ID);

create index A_P_RROFIL_CHATBOT_TO_PERSON_PERSON_FK on PROFIL_PER_CHATBOT (PER_ID asc);

alter table PERSON
	add constraint FK_A_PERSON_GROUPS_GROUPS foreign key (GRP_ID)
	references GROUPS (GRP_ID);

create index A_PERSON_GROUPS_GROUPS_FK on PERSON (GRP_ID asc);

alter table PERSON
	add constraint FK_A_PERSON_ROLE_PERSON_ROLE foreign key (ROL_CD)
	references PERSON_ROLE (ROL_CD);

create index A_PERSON_ROLE_PERSON_ROLE_FK on PERSON (ROL_CD asc);

alter table RESPONSE_BUTTON
	add constraint FK_A_RESPONSE_BUTTON_TOPIC_RESPONSE_TOPIC foreign key (TOP_ID_RESPONSE)
	references TOPIC (TOP_ID);

create index A_RESPONSE_BUTTON_TOPIC_RESPONSE_TOPIC_FK on RESPONSE_BUTTON (TOP_ID_RESPONSE asc);

alter table SAVED_TRAINING
	add constraint FK_A_SAVED_TRAINING_CHATBOT_CHATBOT foreign key (BOT_ID)
	references CHATBOT (BOT_ID);

create index A_SAVED_TRAINING_CHATBOT_CHATBOT_FK on SAVED_TRAINING (BOT_ID asc);

alter table SAVED_TRAINING
	add constraint FK_A_SAVED_TRAINING_TRAINING_TRAINING foreign key (TRA_ID)
	references TRAINING (TRA_ID);

create index A_SAVED_TRAINING_TRAINING_TRAINING_FK on SAVED_TRAINING (TRA_ID asc);

alter table SCRIPT_INTENTION
	add constraint FK_A_SCRIPT_INTENTION_TOPIC_TOPIC foreign key (TOP_ID)
	references TOPIC (TOP_ID);

create index A_SCRIPT_INTENTION_TOPIC_TOPIC_FK on SCRIPT_INTENTION (TOP_ID asc);

alter table RESPONSE_BUTTON
	add constraint FK_A_SMALL_TALK_RESPONSE_BUTTONS_SMALL_TALK foreign key (SMT_ID)
	references SMALL_TALK (SMT_ID);

create index A_SMALL_TALK_RESPONSE_BUTTONS_SMALL_TALK_FK on RESPONSE_BUTTON (SMT_ID asc);

alter table SMALL_TALK
	add constraint FK_A_SMALL_TALK_RESPONSE_TYPE_RESPONSE_TYPE foreign key (RTY_ID)
	references RESPONSE_TYPE (RTY_ID);

create index A_SMALL_TALK_RESPONSE_TYPE_RESPONSE_TYPE_FK on SMALL_TALK (RTY_ID asc);

alter table SMALL_TALK
	add constraint FK_A_SMALL_TALK_TOPIC_TOPIC foreign key (TOP_ID)
	references TOPIC (TOP_ID);

create index A_SMALL_TALK_TOPIC_TOPIC_FK on SMALL_TALK (TOP_ID asc);

alter table UTTER_TEXT
	add constraint FK_A_SMALL_TALK_UTTER_TEXT_SMALL_TALK foreign key (SMT_ID)
	references SMALL_TALK (SMT_ID);

create index A_SMALL_TALK_UTTER_TEXT_SMALL_TALK_FK on UTTER_TEXT (SMT_ID asc);

alter table SYNONYM
	add constraint FK_A_SYNONYM_CHATBOT_CHATBOT foreign key (BOT_ID)
	references CHATBOT (BOT_ID);

create index A_SYNONYM_CHATBOT_CHATBOT_FK on SYNONYM (BOT_ID asc);

alter table SYNONYM
	add constraint FK_A_SYNONYM_DICTIONARY_ENTITY_DICTIONARY_ENTITY foreign key (DIC_ENT_ID)
	references DICTIONARY_ENTITY (DIC_ENT_ID);

create index A_SYNONYM_DICTIONARY_ENTITY_DICTIONARY_ENTITY_FK on SYNONYM (DIC_ENT_ID asc);

alter table TOPIC_CATEGORY
	add constraint FK_A_TOPIC_CATEGORY_CHATBOT_CHATBOT foreign key (BOT_ID)
	references CHATBOT (BOT_ID);

create index A_TOPIC_CATEGORY_CHATBOT_CHATBOT_FK on TOPIC_CATEGORY (BOT_ID asc);

alter table TOPIC
	add constraint FK_A_TOPIC_CATEGORY_TOPIC_TOPIC_CATEGORY foreign key (TOP_CAT_ID)
	references TOPIC_CATEGORY (TOP_CAT_ID);

create index A_TOPIC_CATEGORY_TOPIC_TOPIC_CATEGORY_FK on TOPIC (TOP_CAT_ID asc);

alter table TOPIC
	add constraint FK_A_TOPIC_CHATBOT_CHATBOT foreign key (BOT_ID)
	references CHATBOT (BOT_ID);

create index A_TOPIC_CHATBOT_CHATBOT_FK on TOPIC (BOT_ID asc);

alter table TOPIC
	add constraint FK_A_TOPIC_KIND_TOPIC_KIND_TOPIC foreign key (KTO_CD)
	references KIND_TOPIC (KTO_CD);

create index A_TOPIC_KIND_TOPIC_KIND_TOPIC_FK on TOPIC (KTO_CD asc);

alter table TOPIC_LABEL
	add constraint FK_A_TOPIC_LABEL_CHATBOT_CHATBOT foreign key (BOT_ID)
	references CHATBOT (BOT_ID);

create index A_TOPIC_LABEL_CHATBOT_CHATBOT_FK on TOPIC_LABEL (BOT_ID asc);

alter table NLU_TRAINING_SENTENCE
	add constraint FK_A_TOPIC_NLU_TRAINING_SENTENCE_TOPIC foreign key (TOP_ID)
	references TOPIC (TOP_ID);

create index A_TOPIC_NLU_TRAINING_SENTENCE_TOPIC_FK on NLU_TRAINING_SENTENCE (TOP_ID asc);

alter table TOPIC
	add constraint FK_A_TOPIC_TYPE_TOPIC_TYPE_TOPIC foreign key (TTO_CD)
	references TYPE_TOPIC (TTO_CD);

create index A_TOPIC_TYPE_TOPIC_TYPE_TOPIC_FK on TOPIC (TTO_CD asc);

alter table TRAINING
	add constraint FK_A_TRAINING_CHATBOT_CHATBOT foreign key (BOT_ID)
	references CHATBOT (BOT_ID);

create index A_TRAINING_CHATBOT_CHATBOT_FK on TRAINING (BOT_ID asc);

alter table TRAINING
	add constraint FK_A_TRAINING_MEDIA_FILE_INFO_MEDIA_FILE_INFO foreign key (FIL_ID_MODEL)
	references MEDIA_FILE_INFO (FIL_ID);

create index A_TRAINING_MEDIA_FILE_INFO_MEDIA_FILE_INFO_FK on TRAINING (FIL_ID_MODEL asc);

alter table TRAINING
	add constraint FK_A_TRAINING_TRAINING_STATUS_TRAINING_STATUS foreign key (STR_CD)
	references TRAINING_STATUS (STR_CD);

create index A_TRAINING_TRAINING_STATUS_TRAINING_STATUS_FK on TRAINING (STR_CD asc);

alter table UNKNOWN_SENTENCE_DETAIL
	add constraint FK_A_UNKNOWN_SENTENCE_CHATBOT_CHATBOT foreign key (BOT_ID)
	references CHATBOT (BOT_ID);

create index A_UNKNOWN_SENTENCE_CHATBOT_CHATBOT_FK on UNKNOWN_SENTENCE_DETAIL (BOT_ID asc);

alter table UNKNOWN_SENTENCE_DETAIL
	add constraint FK_A_UNKNOWN_SENTENCE_UNKNOWN_SENTENCE_STATUS_UNKNOWN_SENTENCE_STATUS foreign key (STATUS)
	references UNKNOWN_SENTENCE_STATUS (STR_CD);

create index A_UNKNOWN_SENTENCE_UNKNOWN_SENTENCE_STATUS_UNKNOWN_SENTENCE_STATUS_FK on UNKNOWN_SENTENCE_DETAIL (STATUS asc);


create table TOPIC_TOPIC_LABEL
(
	TOP_ID      	 NUMERIC     	 not null,
	LABEL_ID    	 NUMERIC     	 not null,
	constraint PK_TOPIC_TOPIC_LABEL primary key (TOP_ID, LABEL_ID),
	constraint FK_ANN_TOPIC_LABEL_TOPIC 
		foreign key(TOP_ID)
		references TOPIC (TOP_ID),
	constraint FK_ANN_TOPIC_LABEL_TOPIC_LABEL 
		foreign key(LABEL_ID)
		references TOPIC_LABEL (LABEL_ID)
);

create index ANN_TOPIC_LABEL_TOPIC_FK on TOPIC_TOPIC_LABEL (TOP_ID asc);

create index ANN_TOPIC_LABEL_TOPIC_LABEL_FK on TOPIC_TOPIC_LABEL (LABEL_ID asc);

