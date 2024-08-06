create sequence SEQ_DOCUMENTARY_RESOURCE
    start with 1000 cache 1;

create sequence SEQ_DOCUMENTARY_RESOURCE_FILE
    start with 1000 cache 1;

create sequence SEQ_DOCUMENTARY_RESOURCE_TEXT
    start with 1000 cache 1;

create sequence SEQ_DOCUMENTARY_RESOURCE_URL
    start with 1000 cache 1;

-- ============================================================
--   Table : DOCUMENTARY_RESOURCE
-- ============================================================
create table DOCUMENTARY_RESOURCE
(
    DRE_ID      	 NUMERIC     	not null,
    TITLE       	 VARCHAR(100)	not null,
    DESCRIPTION 	 TEXT 	        ,
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

comment on column DOCUMENTARY_RESOURCE.DRE_TYPE_CD is
'Documentary resource type';

comment on column DOCUMENTARY_RESOURCE.BOT_ID is
'Chatbot';

-- ============================================================
--   Table : DOCUMENTARY_RESOURCE_FILE
-- ============================================================
create table DOCUMENTARY_RESOURCE_FILE
(
    FILE_ID     	 NUMERIC     	not null,
    DRE_ID      	 NUMERIC     	not null,
    ATT_FI_ID      	 NUMERIC     	not null,
    constraint PK_DOCUMENTARY_RESOURCE_FILE primary key (FILE_ID)
);

comment on column DOCUMENTARY_RESOURCE_FILE.FILE_ID is
'ID';

comment on column DOCUMENTARY_RESOURCE_FILE.DRE_ID is
'Documentary resource id';

comment on column DOCUMENTARY_RESOURCE_FILE.ATT_FI_ID is
'Attachment file info  id';

-- ============================================================
--   Table : DOCUMENTARY_RESOURCE_TEXT
-- ============================================================
create table DOCUMENTARY_RESOURCE_TEXT
(
    TEXT_ID     	 NUMERIC     	not null,
    TEXT        	 TEXT        	not null,
    DRE_ID      	 NUMERIC     	not null,
    constraint PK_DOCUMENTARY_RESOURCE_TEXT primary key (TEXT_ID)
);

comment on column DOCUMENTARY_RESOURCE_TEXT.TEXT_ID is
'ID';

comment on column DOCUMENTARY_RESOURCE_TEXT.TEXT is
'Text';

comment on column DOCUMENTARY_RESOURCE_TEXT.DRE_ID is
'Documentary resource id';

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

-- ============================================================
--   Table : DOCUMENTARY_RESOURCE_URL
-- ============================================================
create table DOCUMENTARY_RESOURCE_URL
(
    URL_ID      	 NUMERIC     	not null,
    URL         	 TEXT        	not null,
    DRE_ID      	 NUMERIC     	not null,
    constraint PK_DOCUMENTARY_RESOURCE_URL primary key (URL_ID)
);

comment on column DOCUMENTARY_RESOURCE_URL.URL_ID is
'ID';

comment on column DOCUMENTARY_RESOURCE_URL.URL is
'url';

comment on column DOCUMENTARY_RESOURCE_URL.DRE_ID is
'Documentary resource id';

alter table DOCUMENTARY_RESOURCE
    add constraint FK_A_DOCUMENTARY_RESOURCE_CHATBOT_CHATBOT foreign key (BOT_ID)
        references CHATBOT (BOT_ID);

create index A_DOCUMENTARY_RESOURCE_CHATBOT_CHATBOT_FK on DOCUMENTARY_RESOURCE (BOT_ID asc);

alter table DOCUMENTARY_RESOURCE
    add constraint FK_A_DOCUMENTARY_RESOURCE_DOCUMENTARY_RESOURCE_TYPE_DOCUMENTARY_RESOURCE_TYPE foreign key (DRE_TYPE_CD)
        references DOCUMENTARY_RESOURCE_TYPE (DRE_TYPE_CD);

create index A_DOCUMENTARY_RESOURCE_DOCUMENTARY_RESOURCE_TYPE_DOCUMENTARY_RESOURCE_TYPE_FK on DOCUMENTARY_RESOURCE (DRE_TYPE_CD asc);

alter table DOCUMENTARY_RESOURCE_FILE
    add constraint FK_A_DOCUMENTARY_RESOURCE_FILE_ATTACHMENT_FILE_INFO_ATTACHMENT_FILE_INFO foreign key (ATT_FI_ID)
        references ATTACHMENT_FILE_INFO (ATT_FI_ID);

create index A_DOCUMENTARY_RESOURCE_FILE_ATTACHMENT_FILE_INFO_ATTACHMENT_FILE_INFO_FK on DOCUMENTARY_RESOURCE_FILE (ATT_FI_ID asc);

alter table DOCUMENTARY_RESOURCE_FILE
    add constraint FK_A_DOCUMENTARY_RESOURCE_FILE_DOCUMENTARY_RESOURCE_DOCUMENTARY_RESOURCE foreign key (DRE_ID)
        references DOCUMENTARY_RESOURCE (DRE_ID);

create index A_DOCUMENTARY_RESOURCE_FILE_DOCUMENTARY_RESOURCE_DOCUMENTARY_RESOURCE_FK on DOCUMENTARY_RESOURCE_FILE (DRE_ID asc);

alter table DOCUMENTARY_RESOURCE_TEXT
    add constraint FK_A_DOCUMENTARY_RESOURCE_TEXT_DOCUMENTARY_RESOURCE_DOCUMENTARY_RESOURCE foreign key (DRE_ID)
        references DOCUMENTARY_RESOURCE (DRE_ID);

create index A_DOCUMENTARY_RESOURCE_TEXT_DOCUMENTARY_RESOURCE_DOCUMENTARY_RESOURCE_FK on DOCUMENTARY_RESOURCE_TEXT (DRE_ID asc);

alter table DOCUMENTARY_RESOURCE_URL
    add constraint FK_A_DOCUMENTARY_RESOURCE_URL_DOCUMENTARY_RESOURCE_DOCUMENTARY_RESOURCE foreign key (DRE_ID)
        references DOCUMENTARY_RESOURCE (DRE_ID);

create index A_DOCUMENTARY_RESOURCE_URL_DOCUMENTARY_RESOURCE_DOCUMENTARY_RESOURCE_FK on DOCUMENTARY_RESOURCE_URL (DRE_ID asc);

-- ============================================================
--   Insert MasterData values : DOCUMENTARY_RESOURCE_TYPE
-- ============================================================
insert into DOCUMENTARY_RESOURCE_TYPE(DRE_TYPE_CD, LABEL, LABEL_FR) values ('URL', 'URL', 'URL');
insert into DOCUMENTARY_RESOURCE_TYPE(DRE_TYPE_CD, LABEL, LABEL_FR) values ('FILE', 'File', 'Document');
insert into DOCUMENTARY_RESOURCE_TYPE(DRE_TYPE_CD, LABEL, LABEL_FR) values ('TEXT', 'Text', 'Texte');
