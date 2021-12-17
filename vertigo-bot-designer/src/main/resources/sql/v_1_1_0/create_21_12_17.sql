create sequence SEQ_SAVED_TRAINING
    start with 1000 cache 20;

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

alter table SAVED_TRAINING
    add constraint FK_A_SAVED_TRAINING_CHATBOT_CHATBOT foreign key (BOT_ID)
        references CHATBOT (BOT_ID);

create index A_SAVED_TRAINING_CHATBOT_CHATBOT_FK on SAVED_TRAINING (BOT_ID asc);

alter table SAVED_TRAINING
    add constraint FK_A_SAVED_TRAINING_TRAINING_TRAINING foreign key (TRA_ID)
        references TRAINING (TRA_ID);

create index A_SAVED_TRAINING_TRAINING_TRAINING_FK on SAVED_TRAINING (TRA_ID asc);