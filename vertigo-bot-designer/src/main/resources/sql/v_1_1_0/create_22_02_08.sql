create sequence SEQ_JIRA_FIELD_SETTING
    start with 1000 cache 20;

create sequence SEQ_JIRA_SETTING
    start with 1000 cache 20;

-- ============================================================
--   Table : JIRA_FIELD
-- ============================================================
create table JIRA_FIELD
(
    JIR_FIELD_CD	 VARCHAR(100)	not null,
    JIRA_ID     	 VARCHAR(100)	not null,
    LABEL       	 VARCHAR(100)	not null,
    LABEL_FR    	 VARCHAR(100)	not null,
    constraint PK_JIRA_FIELD primary key (JIR_FIELD_CD)
);

comment on column JIRA_FIELD.JIR_FIELD_CD is
'ID';

comment on column JIRA_FIELD.JIRA_ID is
'Jira id';

comment on column JIRA_FIELD.LABEL is
'Title';

comment on column JIRA_FIELD.LABEL_FR is
'TitleFr';

-- ============================================================
--   Table : JIRA_FIELD_SETTING
-- ============================================================
create table JIRA_FIELD_SETTING
(
    JIR_FIELD_SET_ID	 NUMERIC     	not null,
    ENABLED     	 bool        	not null,
    MANDATORY   	 bool        	not null,
    BOT_ID      	 NUMERIC     	not null,
    JIR_FIELD_CD	 VARCHAR(100)	not null,
    constraint PK_JIRA_FIELD_SETTING primary key (JIR_FIELD_SET_ID)
);

comment on column JIRA_FIELD_SETTING.JIR_FIELD_SET_ID is
'Jira field setting id';

comment on column JIRA_FIELD_SETTING.ENABLED is
'Jira field enabled';

comment on column JIRA_FIELD_SETTING.MANDATORY is
'Jira field mandatory';

comment on column JIRA_FIELD_SETTING.BOT_ID is
'Chatbot';

comment on column JIRA_FIELD_SETTING.JIR_FIELD_CD is
'Field';

-- ============================================================
--   Table : JIRA_SETTING
-- ============================================================
create table JIRA_SETTING
(
    JIR_SET_ID  	 NUMERIC     	not null,
    URL         	 TEXT        	not null,
    LOGIN       	 VARCHAR(100)	not null,
    PASSWORD    	 VARCHAR(100)	not null,
    PROJECT     	 VARCHAR(100)	not null,
    BOT_ID      	 NUMERIC     	not null,
    NOD_ID      	 NUMERIC     	not null,
    UNIQUE (BOT_ID, NOD_ID),
    constraint PK_JIRA_SETTING primary key (JIR_SET_ID)
);

comment on column JIRA_SETTING.JIR_SET_ID is
'Jira setting id';

comment on column JIRA_SETTING.URL is
'Jira URL';

comment on column JIRA_SETTING.LOGIN is
'Login';

comment on column JIRA_SETTING.PASSWORD is
'Password';

comment on column JIRA_SETTING.PROJECT is
'Jira project';

comment on column JIRA_SETTING.BOT_ID is
'Chatbot';

comment on column JIRA_SETTING.NOD_ID is
'Node';

alter table JIRA_FIELD_SETTING
    add constraint FK_A_JIRA_FIELD_SETTING_CHATBOT_CHATBOT foreign key (BOT_ID)
        references CHATBOT (BOT_ID);

create index A_JIRA_FIELD_SETTING_CHATBOT_CHATBOT_FK on JIRA_FIELD_SETTING (BOT_ID asc);

alter table JIRA_FIELD_SETTING
    add constraint FK_A_JIRA_FIELD_SETTING_JIRA_FIELD_JIRA_FIELD foreign key (JIR_FIELD_CD)
        references JIRA_FIELD (JIR_FIELD_CD);

create index A_JIRA_FIELD_SETTING_JIRA_FIELD_JIRA_FIELD_FK on JIRA_FIELD_SETTING (JIR_FIELD_CD asc);

alter table JIRA_SETTING
    add constraint FK_A_JIRA_SETTING_CHATBOT_CHATBOT foreign key (BOT_ID)
        references CHATBOT (BOT_ID);

create index A_JIRA_SETTING_CHATBOT_CHATBOT_FK on JIRA_SETTING (BOT_ID asc);

alter table JIRA_SETTING
    add constraint FK_A_JIRA_SETTING_NODE_CHATBOT_NODE foreign key (NOD_ID)
        references CHATBOT_NODE (NOD_ID);

create index A_JIRA_SETTING_NODE_CHATBOT_NODE_FK on JIRA_SETTING (NOD_ID asc);

insert into JIRA_FIELD(JIR_FIELD_CD, JIRA_ID, LABEL, LABEL_FR) values ('SUMMARY', 'summary', 'Summary', 'Résumé');
insert into JIRA_FIELD(JIR_FIELD_CD, JIRA_ID, LABEL, LABEL_FR) values ('DESCRIPTION', 'description', 'Description', 'Description');
insert into JIRA_FIELD(JIR_FIELD_CD, JIRA_ID, LABEL, LABEL_FR) values ('TYPE', 'issuetype', 'Issue Type', 'Type de ticket');
insert into JIRA_FIELD(JIR_FIELD_CD, JIRA_ID, LABEL, LABEL_FR) values ('COMPONENT', 'components', 'Component', 'Composant');
insert into JIRA_FIELD(JIR_FIELD_CD, JIRA_ID, LABEL, LABEL_FR) values ('AFFECTS_VERSION', 'versions', 'Affected version', 'Version impactée');
insert into JIRA_FIELD(JIR_FIELD_CD, JIRA_ID, LABEL, LABEL_FR) values ('FIX_VERSION', 'fixVersions', 'Fix version', 'Version corrective');
insert into JIRA_FIELD(JIR_FIELD_CD, JIRA_ID, LABEL, LABEL_FR) values ('PRIORITY', 'priority', 'Priority', 'Priorité');
insert into JIRA_FIELD(JIR_FIELD_CD, JIRA_ID, LABEL, LABEL_FR) values ('ASSIGNEE', 'assignee', 'Assignee', 'Responsable');
insert into JIRA_FIELD(JIR_FIELD_CD, JIRA_ID, LABEL, LABEL_FR) values ('REPORTER', 'reporter', 'Reporter', 'Rapporteur');