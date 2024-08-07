create sequence SEQ_DOCUMENTARY_RESOURCE
    start with 1000 cache 1;

-- ============================================================
--   Table : DOCUMENTARY_RESOURCE
-- ============================================================
create table DOCUMENTARY_RESOURCE
(
    DRE_ID      	 NUMERIC     	not null,
    TITLE       	 VARCHAR(100)	not null,
    DESCRIPTION 	 TEXT        	,
    TEXT        	 TEXT        	,
    URL         	 TEXT        	,
    ATT_ID   	     NUMERIC     	,
    DRE_TYPE_CD 	 VARCHAR(100)	not null,
    BOT_ID      	 NUMERIC     	not null,
    constraint PK_DOCUMENTARY_RESOURCE primary key (DRE_ID)
);

comment on column DOCUMENTARY_RESOURCE.DRE_ID is
'ID';

comment on column DOCUMENTARY_RESOURCE.TITLE is
'Title';

comment on column DOCUMENTARY_RESOURCE.DESCRIPTION is
'Description';

comment on column DOCUMENTARY_RESOURCE.TEXT is
'Text';

comment on column DOCUMENTARY_RESOURCE.URL is
'Url';

comment on column DOCUMENTARY_RESOURCE.ATT_ID is
'Attachment id';

comment on column DOCUMENTARY_RESOURCE.DRE_TYPE_CD is
'Documentary resource type';

comment on column DOCUMENTARY_RESOURCE.BOT_ID is
'Chatbot';

-- ============================================================
--   Table : DOCUMENTARY_RESOURCE_TYPE
-- ============================================================
create table DOCUMENTARY_RESOURCE_TYPE
(
    DRE_TYPE_CD 	 VARCHAR(100)	not null,
    LABEL       	 VARCHAR(100)	not null,
    LABEL_FR    	 VARCHAR(100)	not null,
    constraint PK_DOCUMENTARY_RESOURCE_TYPE primary key (DRE_TYPE_CD)
);

comment on column DOCUMENTARY_RESOURCE_TYPE.DRE_TYPE_CD is
'Code';

comment on column DOCUMENTARY_RESOURCE_TYPE.LABEL is
'Label';

comment on column DOCUMENTARY_RESOURCE_TYPE.LABEL_FR is
'LabelFr';


alter table DOCUMENTARY_RESOURCE
    add constraint FK_A_DOCUMENTARY_RESOURCE_CHATBOT_CHATBOT foreign key (BOT_ID)
        references CHATBOT (BOT_ID);

create index A_DOCUMENTARY_RESOURCE_CHATBOT_CHATBOT_FK on DOCUMENTARY_RESOURCE (BOT_ID asc);

alter table DOCUMENTARY_RESOURCE
    add constraint FK_A_DOCUMENTARY_RESOURCE_DOCUMENTARY_RESOURCE_TYPE_DOCUMENTARY_RESOURCE_TYPE foreign key (DRE_TYPE_CD)
        references DOCUMENTARY_RESOURCE_TYPE (DRE_TYPE_CD);

create index A_DOCUMENTARY_RESOURCE_DOCUMENTARY_RESOURCE_TYPE_DOCUMENTARY_RESOURCE_TYPE_FK on DOCUMENTARY_RESOURCE (DRE_TYPE_CD asc);

alter table DOCUMENTARY_RESOURCE
    add constraint FK_A_DOCUMENTARY_RESOURCE_FILE_ATTACHMENT_FILE_INFO_ATTACHMENT foreign key (ATT_ID)
        references ATTACHMENT (ATT_ID);

create index A_DOCUMENTARY_RESOURCE_FILE_ATTACHMENT_FILE_INFO_ATTACHMENT_FK on DOCUMENTARY_RESOURCE (ATT_ID asc);


-- ============================================================
--   Insert MasterData values : DOCUMENTARY_RESOURCE_TYPE
-- ============================================================
insert into DOCUMENTARY_RESOURCE_TYPE(DRE_TYPE_CD, LABEL, LABEL_FR) values ('URL', 'URL', 'URL');
insert into DOCUMENTARY_RESOURCE_TYPE(DRE_TYPE_CD, LABEL, LABEL_FR) values ('FILE', 'File', 'Document');
insert into DOCUMENTARY_RESOURCE_TYPE(DRE_TYPE_CD, LABEL, LABEL_FR) values ('TEXT', 'Text', 'Texte');
