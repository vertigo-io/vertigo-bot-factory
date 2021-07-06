drop table IF EXISTS MEANING cascade;
drop sequence IF EXISTS SEQ_MEANING;

drop table IF EXISTS SYNONYM cascade;
drop sequence IF EXISTS SEQ_SYNONYM;

create sequence SEQ_MEANING
	start with 1000 cache 20; 
	
create sequence SEQ_SYNONYM
	start with 1000 cache 20; 

-- ============================================================
--   Table : MEANING                                        
-- ============================================================
create table MEANING
(
    MEA_ID      	 NUMERIC     	not null,
    LABEL       	 VARCHAR(100)	not null,
    BOT_ID      	 NUMERIC     	not null,
    constraint PK_MEANING primary key (MEA_ID)
);

comment on column MEANING.MEA_ID is
'Meaning id';

comment on column MEANING.LABEL is
'Label';

comment on column MEANING.BOT_ID is
'Chatbot';

-- ============================================================
--   Table : SYNONYM                                        
-- ============================================================
create table SYNONYM
(
    SYN_ID      	 NUMERIC     	not null,
    LABEL       	 VARCHAR(100)	not null,
    BOT_ID      	 NUMERIC     	not null,
    MEA_ID      	 NUMERIC     	not null,
    constraint PK_SYNONYM primary key (SYN_ID)
);

comment on column SYNONYM.SYN_ID is
'Synonym id';

comment on column SYNONYM.LABEL is
'Label';

comment on column SYNONYM.BOT_ID is
'Chatbot';

comment on column SYNONYM.MEA_ID is
'Intention';

alter table MEANING
	add constraint FK_A_MEANING_CHATBOT_CHATBOT foreign key (BOT_ID)
	references CHATBOT (BOT_ID);

create index A_MEANING_CHATBOT_CHATBOT_FK on MEANING (BOT_ID asc);

alter table SYNONYM
	add constraint FK_A_SYNONYM_CHATBOT_CHATBOT foreign key (BOT_ID)
	references CHATBOT (BOT_ID);

create index A_SYNONYM_CHATBOT_CHATBOT_FK on SYNONYM (BOT_ID asc);

alter table SYNONYM
	add constraint FK_A_SYNONYM_MEANING_MEANING foreign key (MEA_ID)
	references MEANING (MEA_ID);

create index A_SYNONYM_MEANING_MEANING_FK on SYNONYM (MEA_ID asc);


alter table meaning
add constraint meaning_unique UNIQUE (bot_id, label);
