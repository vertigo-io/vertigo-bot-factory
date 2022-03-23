create sequence SEQ_ATTACHMENT
    start with 1000 cache 20;

create sequence SEQ_ATTACHMENT_FILE_INFO
    start with 1000 cache 20;


-- ============================================================
--   Table : ATTACHMENT
-- ============================================================
create table ATTACHMENT
(
    ATT_ID      	 NUMERIC     	not null,
    LABEL       	 VARCHAR(100)	not null,
    TYPE        	 VARCHAR(100)	not null,
    LENGTH      	 NUMERIC     	not null,
    ATT_FI_ID   	 NUMERIC     	not null,
    BOT_ID      	 NUMERIC     	not null,
    constraint PK_ATTACHMENT primary key (ATT_ID)
);

comment on column ATTACHMENT.ATT_ID is
'Attachment id';

comment on column ATTACHMENT.LABEL is
'Label';

comment on column ATTACHMENT.TYPE is
'MimeType';

comment on column ATTACHMENT.LENGTH is
'Size';

comment on column ATTACHMENT.ATT_FI_ID is
'AttachmentFileInfo';

comment on column ATTACHMENT.BOT_ID is
'Chatbot';

-- ============================================================
--   Table : ATTACHMENT_FILE_INFO
-- ============================================================
create table ATTACHMENT_FILE_INFO
(
    ATT_FI_ID   	 NUMERIC     	not null,
    FILE_NAME   	 VARCHAR(100)	not null,
    MIME_TYPE   	 VARCHAR(100)	not null,
    LENGTH      	 NUMERIC     	not null,
    LAST_MODIFIED	 TIMESTAMP   	not null,
    FILE_PATH   	 VARCHAR(500)	,
    constraint PK_ATTACHMENT_FILE_INFO primary key (ATT_FI_ID)
);

comment on column ATTACHMENT_FILE_INFO.ATT_FI_ID is
'Id';

comment on column ATTACHMENT_FILE_INFO.FILE_NAME is
'Name';

comment on column ATTACHMENT_FILE_INFO.MIME_TYPE is
'MimeType';

comment on column ATTACHMENT_FILE_INFO.LENGTH is
'Size';

comment on column ATTACHMENT_FILE_INFO.LAST_MODIFIED is
'Modification Date';

comment on column ATTACHMENT_FILE_INFO.FILE_PATH is
'path';


alter table ATTACHMENT
    add constraint FK_A_ATTACHMENT_ATTACHMENT_FILE_INFO_ATTACHMENT_FILE_INFO foreign key (ATT_FI_ID)
        references ATTACHMENT_FILE_INFO (ATT_FI_ID);

create index A_ATTACHMENT_ATTACHMENT_FILE_INFO_ATTACHMENT_FILE_INFO_FK on ATTACHMENT (ATT_FI_ID asc);

alter table ATTACHMENT
    add constraint FK_A_ATTACHMENT_CHATBOT_CHATBOT foreign key (BOT_ID)
        references CHATBOT (BOT_ID);

create index A_ATTACHMENT_CHATBOT_CHATBOT_FK on ATTACHMENT (BOT_ID asc);
