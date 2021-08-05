
-- New tables

create sequence SEQ_SCRIPT_INTENTION
	start with 1000 cache 20; 
	
create sequence SEQ_TOPIC
	start with 1000 cache 20; 

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
--   Table : TOPIC                                        
-- ============================================================
create table TOPIC
(
    TOP_ID      	 NUMERIC     	not null,
    TITLE       	 VARCHAR(100)	not null,
    DESCRIPTION 	 VARCHAR(100)	,
    IS_ENABLED  	 bool        	not null,
    BOT_ID      	 NUMERIC     	not null,
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

comment on column TOPIC.BOT_ID is
'Chatbot';


-- change table
alter table small_talk 
add column top_id numeric;

update small_talk 
set top_id = nextval('SEQ_TOPIC');

insert into topic (top_id, title, description, is_enabled, bot_id)
select top_id, title, description, is_enabled, bot_id
from small_talk;

alter table small_talk 
drop constraint if exists FK_A_SMALL_TALK_CHATBOT_CHATBOT;

alter table small_talk 
drop column if exists title,
drop column if exists description,
drop column if exists is_enabled,
drop column if exists bot_id;

alter table small_talk
alter column top_id set not null;

alter table nlu_training_sentence 
add column top_id numeric;

update nlu_training_sentence 
set top_id = small_talk.top_id
from small_talk 
where nlu_training_sentence.smt_id  = small_talk.smt_id ; 

alter table nlu_training_sentence 
drop constraint if exists FK_A_SMALL_TALK_NLU_TRAINING_SENTENCE_SMALL_TALK;

alter table nlu_training_sentence 
drop column smt_id;

alter table nlu_training_sentence
alter column top_id set not null;

-- Add constraint 
alter table SCRIPT_INTENTION
	add constraint FK_A_SCRIPT_INTENTION_TOPIC_TOPIC foreign key (TOP_ID)
	references TOPIC (TOP_ID);

create index A_SCRIPT_INTENTION_TOPIC_TOPIC_FK on SCRIPT_INTENTION (TOP_ID asc);

alter table SMALL_TALK
	add constraint FK_A_SMALL_TALK_TOPIC_TOPIC foreign key (TOP_ID)
	references TOPIC (TOP_ID);

create index A_SMALL_TALK_TOPIC_TOPIC_FK on SMALL_TALK (TOP_ID asc);


alter table TOPIC
	add constraint FK_A_TOPIC_CHATBOT_CHATBOT foreign key (BOT_ID)
	references CHATBOT (BOT_ID);

create index A_TOPIC_CHATBOT_CHATBOT_FK on TOPIC (BOT_ID asc);

alter table NLU_TRAINING_SENTENCE
	add constraint FK_A_TOPIC_NLU_TRAINING_SENTENCE_TOPIC foreign key (TOP_ID)
	references TOPIC (TOP_ID);

create index A_TOPIC_NLU_TRAINING_SENTENCE_TOPIC_FK on NLU_TRAINING_SENTENCE (TOP_ID asc);
