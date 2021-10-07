-- Chatbot 286 : Module Récupération du contexte : IHM

drop table IF EXISTS CONTEXT_VALUE cascade;
drop sequence IF EXISTS SEQ_CONTEXT_VALUE;

create sequence SEQ_CONTEXT_VALUE
	start with 1000 cache 20; 

-- ============================================================
--   Table : CONTEXT_VALUE                                        
-- ============================================================
create table CONTEXT_VALUE
(
    CVA_ID      	 NUMERIC     	not null,
    LABEL       	 VARCHAR(100)	not null,
    KEY         	 VARCHAR(100)	not null,
    BOT_ID      	 NUMERIC     	not null,
    constraint PK_CONTEXT_VALUE primary key (CVA_ID)
);

comment on column CONTEXT_VALUE.CVA_ID is
'Context value id';

comment on column CONTEXT_VALUE.LABEL is
'Label';

comment on column CONTEXT_VALUE.KEY is
'Key';

comment on column CONTEXT_VALUE.BOT_ID is
'Chatbot';

alter table CONTEXT_VALUE
	add constraint FK_A_CONTEXT_VALUE_CHATBOT_CHATBOT foreign key (BOT_ID)
	references CHATBOT (BOT_ID);

create index A_CONTEXT_VALUE_CHATBOT_CHATBOT_FK on CONTEXT_VALUE (BOT_ID asc);

alter table context_value
add constraint context_value_unique_key UNIQUE (bot_id,key);
