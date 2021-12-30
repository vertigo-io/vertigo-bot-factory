create sequence SEQ_HISTORY
    start with 1000 cache 20;

-- ============================================================
--   Table : HISTORY
-- ============================================================
create table HISTORY
(
    HIST_ID     	 NUMERIC     	not null,
    DATE        	 TIMESTAMP   	not null,
    CLASS_NAME  	 VARCHAR(100)	not null,
    MESSAGE     	 VARCHAR(100)	not null,
    USER_NAME   	 VARCHAR(100)	not null,
    HAC_CD      	 VARCHAR(100)	not null,
    BOT_ID      	 NUMERIC     	not null,
    constraint PK_HISTORY primary key (HIST_ID)
);

comment on column HISTORY.HIST_ID is
'History id';

comment on column HISTORY.DATE is
'Date';

comment on column HISTORY.CLASS_NAME is
'Class name';

comment on column HISTORY.MESSAGE is
'Label';

comment on column HISTORY.USER_NAME is
'User';

comment on column HISTORY.HAC_CD is
'Action';

comment on column HISTORY.BOT_ID is
'Chatbot';

-- ============================================================
--   Table : HISTORY_ACTION
-- ============================================================
create table HISTORY_ACTION
(
    HAC_CD      	 VARCHAR(100)	not null,
    LABEL       	 VARCHAR(100)	not null,
    LABEL_FR    	 VARCHAR(100)	not null,
    constraint PK_HISTORY_ACTION primary key (HAC_CD)
);

comment on column HISTORY_ACTION.HAC_CD is
'ID';

comment on column HISTORY_ACTION.LABEL is
'Title';

comment on column HISTORY_ACTION.LABEL_FR is
'TitleFr';


alter table HISTORY
    add constraint FK_A_HISTORY_CHATBOT_CHATBOT foreign key (BOT_ID)
        references CHATBOT (BOT_ID);

create index A_HISTORY_CHATBOT_CHATBOT_FK on HISTORY (BOT_ID asc);

alter table HISTORY
    add constraint FK_A_HISTORY_HISTORY_ACTION_HISTORY_ACTION foreign key (HAC_CD)
        references HISTORY_ACTION (HAC_CD);

create index A_HISTORY_HISTORY_ACTION_HISTORY_ACTION_FK on HISTORY (HAC_CD asc);


-- ============================================================
--   Insert MasterData values : HISTORY_ACTION
-- ============================================================
insert into HISTORY_ACTION(HAC_CD, LABEL, LABEL_FR) values ('ADDED', 'Added', 'Ajouté');
insert into HISTORY_ACTION(HAC_CD, LABEL, LABEL_FR) values ('DELETED', 'Deleted', 'Supprimé');
insert into HISTORY_ACTION(HAC_CD, LABEL, LABEL_FR) values ('UPDATED', 'Updated', 'Modifié');