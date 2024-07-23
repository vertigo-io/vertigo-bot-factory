create sequence SEQ_CONTEXT_POSSIBLE_VALUE
    start with 1000 cache 1;

-- ============================================================
--   Table : CONTEXT_POSSIBLE_VALUE
-- ============================================================
create table CONTEXT_POSSIBLE_VALUE
(
    CPV_ID      	 NUMERIC     	not null,
    VALUE       	 VARCHAR(100)	not null,
    OPERATOR    	 VARCHAR(100)	not null,
    CVA_ID       	 NUMERIC     	not null,
    BOT_ID      	 NUMERIC     	not null,
    constraint PK_CONTEXT_POSSIBLE_VALUE primary key (CPV_ID)
);

comment on column CONTEXT_POSSIBLE_VALUE.CPV_ID is
'Context possible value id';

comment on column CONTEXT_POSSIBLE_VALUE.VALUE is
'Value';

comment on column CONTEXT_POSSIBLE_VALUE.OPERATOR is
'Operator';

comment on column CONTEXT_POSSIBLE_VALUE.CVA_ID is
'Context value id';

comment on column CONTEXT_POSSIBLE_VALUE.BOT_ID is
'Chatbot';

alter table CONTEXT_POSSIBLE_VALUE
    add constraint FK_A_CONTEXT_POSSIBLE_VALUE_CONTEXT_VALUE_CONTEXT_VALUE foreign key (CVA_ID)
        references CONTEXT_VALUE (CVA_ID);

create index A_CONTEXT_POSSIBLE_VALUE_CONTEXT_VALUE_CONTEXT_VALUE_FK on CONTEXT_POSSIBLE_VALUE (CVA_ID asc);

alter table CONTEXT_POSSIBLE_VALUE
    add constraint FK_A_CONTEXT_POSSIBLE_VALUE_CHATBOT_CHATBOT foreign key (BOT_ID)
        references CHATBOT (BOT_ID);

create index A_CONTEXT_POSSIBLE_VALUE_CHATBOT_CHATBOT_FK on CONTEXT_POSSIBLE_VALUE (BOT_ID asc);
