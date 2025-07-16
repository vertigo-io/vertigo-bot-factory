create sequence SEQ_GLOBAL_VARIABLE start with 1000 cache 1;

create sequence SEQ_GLOBAL_VARIABLE_TYPE start with 1000 cache 1;

-- ============================================================
--   Table : GLOBAL_VARIABLE
-- ============================================================
create table GLOBAL_VARIABLE
(
    GLV_ID      	 NUMERIC     	not null,
    PARAM_1     	 VARCHAR(100)	not null,
    PARAM_2     	 TEXT        	,
    PARAM_3     	 TEXT        	,
    PARAM_4     	 TEXT        	,
    VALUE       	 TEXT        	not null,
    BOT_ID      	 NUMERIC     	not null,
    GVT_ID      	 NUMERIC     	not null,
    constraint PK_GLOBAL_VARIABLE primary key (GLV_ID)
);

comment on column GLOBAL_VARIABLE.GLV_ID is
    'ID';

comment on column GLOBAL_VARIABLE.PARAM_1 is
    'Param 1 value';

comment on column GLOBAL_VARIABLE.PARAM_2 is
    'Param 2 value';

comment on column GLOBAL_VARIABLE.PARAM_3 is
    'Param 3 value';

comment on column GLOBAL_VARIABLE.PARAM_4 is
    'Param 4 value';

comment on column GLOBAL_VARIABLE.VALUE is
    'Global variable value';

comment on column GLOBAL_VARIABLE.BOT_ID is
    'Chatbot';

comment on column GLOBAL_VARIABLE.GVT_ID is
    'Global variable type';

-- ============================================================
--   Table : GLOBAL_VARIABLE_TYPE
-- ============================================================
create table GLOBAL_VARIABLE_TYPE
(
    GVT_ID      	 NUMERIC     	not null,
    LABEL       	 VARCHAR(100)	not null,
    BOT_ID      	 NUMERIC     	not null,
    constraint PK_GLOBAL_VARIABLE_TYPE primary key (GVT_ID)
);

comment on column GLOBAL_VARIABLE_TYPE.GVT_ID is
    'ID';

comment on column GLOBAL_VARIABLE_TYPE.LABEL is
    'Label';

comment on column GLOBAL_VARIABLE_TYPE.BOT_ID is
    'Chatbot';

alter table GLOBAL_VARIABLE
    add constraint FK_A_GLOBAL_VARIABLE_CHATBOT_CHATBOT foreign key (BOT_ID)
        references CHATBOT (BOT_ID);

create index A_GLOBAL_VARIABLE_CHATBOT_CHATBOT_FK on GLOBAL_VARIABLE (BOT_ID asc);

alter table GLOBAL_VARIABLE
    add constraint FK_A_GLOBAL_VARIABLE_GLOBAL_VARIABLE_TYPE_GLOBAL_VARIABLE_TYPE foreign key (GVT_ID)
        references GLOBAL_VARIABLE_TYPE (GVT_ID);

create index A_GLOBAL_VARIABLE_GLOBAL_VARIABLE_TYPE_GLOBAL_VARIABLE_TYPE_FK on GLOBAL_VARIABLE (GVT_ID asc);

alter table GLOBAL_VARIABLE_TYPE
    add constraint FK_A_GLOBAL_VARIABLE_TYPE_CHATBOT_CHATBOT foreign key (BOT_ID)
        references CHATBOT (BOT_ID);

create index A_GLOBAL_VARIABLE_TYPE_CHATBOT_CHATBOT_FK on GLOBAL_VARIABLE_TYPE (BOT_ID asc);
