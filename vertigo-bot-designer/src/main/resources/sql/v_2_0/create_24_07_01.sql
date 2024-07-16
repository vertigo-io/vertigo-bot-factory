create sequence SEQ_QUESTION_ANSWER
    start with 1000 cache 1;

create sequence SEQ_QUESTION_ANSWER_CATEGORY
    start with 1000 cache 1;

-- ============================================================
--   Table : QUESTION_ANSWER
-- ============================================================
create table QUESTION_ANSWER
(
    QA_ID       	 NUMERIC     	not null,
    QUESTION    	 TEXT       	not null,
    ANSWER      	 TEXT        	not null,
    IS_ENABLED  	 bool        	not null,
    CODE        	 TEXT        	not null,
    BOT_ID      	 NUMERIC     	not null,
    QA_CAT_ID   	 NUMERIC     	not null,
    constraint PK_QUESTION_ANSWER primary key (QA_ID)
);

comment on column QUESTION_ANSWER.QA_ID is
'Question-Answer id';

comment on column QUESTION_ANSWER.QUESTION is
'Question';

comment on column QUESTION_ANSWER.ANSWER is
'Answer';

comment on column QUESTION_ANSWER.IS_ENABLED is
'Enabled';

comment on column QUESTION_ANSWER.CODE is
'Code';

comment on column QUESTION_ANSWER.BOT_ID is
'Chatbot';

comment on column QUESTION_ANSWER.QA_CAT_ID is
'Category';

-- ============================================================
--   Table : QUESTION_ANSWER_CATEGORY
-- ============================================================
create table QUESTION_ANSWER_CATEGORY
(
    QA_CAT_ID   	 NUMERIC     	not null,
    LABEL       	 VARCHAR(100)	not null,
    IS_ENABLED  	 bool        	not null,
    BOT_ID      	 NUMERIC     	not null,
    constraint PK_QUESTION_ANSWER_CATEGORY primary key (QA_CAT_ID)
);

comment on column QUESTION_ANSWER_CATEGORY.QA_CAT_ID is
'Question-Answer category id';

comment on column QUESTION_ANSWER_CATEGORY.LABEL is
'Question-Answer category label';

comment on column QUESTION_ANSWER_CATEGORY.IS_ENABLED is
'Enabled';

comment on column QUESTION_ANSWER_CATEGORY.BOT_ID is
'Chatbot';

alter table QUESTION_ANSWER_CATEGORY
    add constraint FK_A_QUESTION_ANSWER_CATEGORY_CHATBOT_CHATBOT foreign key (BOT_ID)
        references CHATBOT (BOT_ID);

create index A_QUESTION_ANSWER_CATEGORY_CHATBOT_CHATBOT_FK on QUESTION_ANSWER_CATEGORY (BOT_ID asc);

alter table QUESTION_ANSWER
    add constraint FK_A_QUESTION_ANSWER_CATEGORY_QUESTION_ANSWER_QUESTION_ANSWER_CATEGORY foreign key (QA_CAT_ID)
        references QUESTION_ANSWER_CATEGORY (QA_CAT_ID);

create index A_QUESTION_ANSWER_CATEGORY_QUESTION_ANSWER_QUESTION_ANSWER_CATEGORY_FK on QUESTION_ANSWER (QA_CAT_ID asc);

alter table QUESTION_ANSWER
    add constraint FK_A_QUESTION_ANSWER_CHATBOT_CHATBOT foreign key (BOT_ID)
        references CHATBOT (BOT_ID);

create index A_QUESTION_ANSWER_CHATBOT_CHATBOT_FK on QUESTION_ANSWER (BOT_ID asc);

alter table QUESTION_ANSWER_CATEGORY
    add constraint QUESTION_ANSWER_CATEGORY_UNIQUE_LABEL UNIQUE (bot_id,label);
