create sequence SEQ_CHATBOT_CUSTOM_CONFIG
    start with 1000 cache 20;

-- ============================================================
--   Table : CHATBOT_CUSTOM_CONFIG
-- ============================================================
create table CHATBOT_CUSTOM_CONFIG
(
    CCC_ID      	 NUMERIC     	not null,
    VALUE       	 JSONB       	not null,
    BOT_ID      	 NUMERIC     	not null,
    constraint PK_CHATBOT_CUSTOM_CONFIG primary key (CCC_ID)
);

comment on column CHATBOT_CUSTOM_CONFIG.CCC_ID is
'Context value id';

comment on column CHATBOT_CUSTOM_CONFIG.VALUE is
'Value';

comment on column CHATBOT_CUSTOM_CONFIG.BOT_ID is
'Chatbot';

alter table CHATBOT_CUSTOM_CONFIG
    add constraint FK_A_CHATBOT_CUSTOM_CONFIG_CHATBOT_CHATBOT foreign key (BOT_ID)
        references CHATBOT (BOT_ID);

create index A_CHATBOT_CUSTOM_CONFIG_CHATBOT_CHATBOT_FK on CHATBOT_CUSTOM_CONFIG (BOT_ID asc);