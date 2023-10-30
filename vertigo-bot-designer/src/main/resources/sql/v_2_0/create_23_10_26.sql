create sequence SEQ_CONTEXT_ENVIRONMENT
    start with 1000 cache 1;

create sequence SEQ_CONTEXT_ENVIRONMENT_VALUE
    start with 1000 cache 1;

-- ============================================================
--   Table : CONTEXT_ENVIRONMENT
-- ============================================================
create table CONTEXT_ENVIRONMENT
(
    CENV_ID     	 NUMERIC     	not null,
    LABEL       	 VARCHAR(100)	not null,
    BOT_ID      	 NUMERIC     	not null,
    constraint PK_CONTEXT_ENVIRONMENT primary key (CENV_ID)
);

comment on column CONTEXT_ENVIRONMENT.CENV_ID is
'Context environment id';

comment on column CONTEXT_ENVIRONMENT.LABEL is
'Label';

comment on column CONTEXT_ENVIRONMENT.BOT_ID is
'Chatbot';

-- ============================================================
--   Table : CONTEXT_ENVIRONMENT_VALUE
-- ============================================================
create table CONTEXT_ENVIRONMENT_VALUE
(
    CENVAL_ID   	 NUMERIC     	not null,
    VALUE       	 VARCHAR(100)	,
    CVA_ID      	 NUMERIC     	not null,
    CENV_ID     	 NUMERIC     	not null,
    constraint PK_CONTEXT_ENVIRONMENT_VALUE primary key (CENVAL_ID)
);

comment on column CONTEXT_ENVIRONMENT_VALUE.CENVAL_ID is
'Context environment value id';

comment on column CONTEXT_ENVIRONMENT_VALUE.VALUE is
'Value';

comment on column CONTEXT_ENVIRONMENT_VALUE.CVA_ID is
'Context';

comment on column CONTEXT_ENVIRONMENT_VALUE.CENV_ID is
'Environment';

alter table CONTEXT_ENVIRONMENT
    add constraint FK_A_CONTEXT_ENVIRONMENT_CHATBOT_CHATBOT foreign key (BOT_ID)
        references CHATBOT (BOT_ID);

create index A_CONTEXT_ENVIRONMENT_CHATBOT_CHATBOT_FK on CONTEXT_ENVIRONMENT (BOT_ID asc);

alter table CONTEXT_ENVIRONMENT_VALUE
    add constraint FK_A_CONTEXT_ENVIRONMENT_VALUE_CONTEXT_ENVIRONMENT foreign key (CENV_ID)
        references CONTEXT_ENVIRONMENT (CENV_ID);

create index A_CONTEXT_ENVIRONMENT_VALUE_CONTEXT_ENVIRONMENT_FK on CONTEXT_ENVIRONMENT_VALUE (CENV_ID asc);

alter table CONTEXT_ENVIRONMENT_VALUE
    add constraint FK_A_CONTEXT_ENVIRONMENT_VALUE_CONTEXT_CONTEXT_VALUE foreign key (CVA_ID)
        references CONTEXT_VALUE (CVA_ID);

create index A_CONTEXT_ENVIRONMENT_VALUE_CONTEXT_CONTEXT_VALUE_FK on CONTEXT_ENVIRONMENT_VALUE (CVA_ID asc);
