create sequence SEQ_CONFLUENCE_SETTING
    start with 1000 cache 20;

-- ============================================================
--   Table : CONFLUENCE_SETTING
-- ============================================================
create table CONFLUENCE_SETTING
(
    CON_SET_ID  	 NUMERIC     	not null,
    URL         	 TEXT        	not null,
    LOGIN       	 VARCHAR(100)	not null,
    PASSWORD    	 VARCHAR(100)	not null,
    NUMBER_OF_RESULTS	 NUMERIC     	not null,
    BOT_ID      	 NUMERIC     	not null,
    NOD_ID      	 NUMERIC     	not null,
    constraint PK_CONFLUENCE_SETTING primary key (CON_SET_ID)
);

comment on column CONFLUENCE_SETTING.CON_SET_ID is
'Confluence setting id';

comment on column CONFLUENCE_SETTING.URL is
'Knowledge base URL';

comment on column CONFLUENCE_SETTING.LOGIN is
'Login';

comment on column CONFLUENCE_SETTING.PASSWORD is
'Password';

comment on column CONFLUENCE_SETTING.NUMBER_OF_RESULTS is
'Number max of results';

comment on column CONFLUENCE_SETTING.BOT_ID is
'Chatbot';

comment on column CONFLUENCE_SETTING.NOD_ID is
'Node';

alter table CONFLUENCE_SETTING
    add constraint FK_A_CONFLUENCE_SETTING_CHATBOT_CHATBOT foreign key (BOT_ID)
        references CHATBOT (BOT_ID);

create index A_CONFLUENCE_SETTING_CHATBOT_CHATBOT_FK on CONFLUENCE_SETTING (BOT_ID asc);

alter table CONFLUENCE_SETTING
    add constraint FK_A_CONFLUENCE_SETTING_NODE_CHATBOT_NODE foreign key (NOD_ID)
        references CHATBOT_NODE (NOD_ID);

create index A_CONFLUENCE_SETTING_NODE_CHATBOT_NODE_FK on CONFLUENCE_SETTING (NOD_ID asc);
