create sequence SEQ_UNKNOWN_SENTENCE_DETAIL
    start with 1000 cache 20;

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

alter table UNKNOWN_SENTENCE_DETAIL
    add constraint FK_A_UNKNOWN_SENTENCE_CHATBOT_CHATBOT foreign key (BOT_ID)
        references CHATBOT (BOT_ID);

create index A_UNKNOWN_SENTENCE_CHATBOT_CHATBOT_FK on UNKNOWN_SENTENCE_DETAIL (BOT_ID asc);

alter table UNKNOWN_SENTENCE_DETAIL
    add constraint FK_A_UNKNOWN_SENTENCE_UNKNOWN_SENTENCE_STATUS_UNKNOWN_SENTENCE_STATUS foreign key (STATUS)
        references UNKNOWN_SENTENCE_STATUS (STR_CD);

create index A_UNKNOWN_SENTENCE_UNKNOWN_SENTENCE_STATUS_UNKNOWN_SENTENCE_STATUS_FK on UNKNOWN_SENTENCE_DETAIL (STATUS asc);

insert into UNKNOWN_SENTENCE_STATUS(STR_CD, LABEL, LABEL_FR) values ('TO_TREAT', 'To treat', 'A traiter');
insert into UNKNOWN_SENTENCE_STATUS(STR_CD, LABEL, LABEL_FR) values ('TREATED', 'Treated', 'Traité');
insert into UNKNOWN_SENTENCE_STATUS(STR_CD, LABEL, LABEL_FR) values ('REJECTED', 'Rejected', 'Rejetée');