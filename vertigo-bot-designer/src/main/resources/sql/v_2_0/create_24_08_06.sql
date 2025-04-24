-- ============================================================
--   Table : ATTACHMENT_TYPE
-- ============================================================
create table ATTACHMENT_TYPE
(
    ATT_TYPE_CD 	 VARCHAR(100)	not null,
    LABEL       	 VARCHAR(100)	not null,
    LABEL_FR    	 VARCHAR(100)	not null,
    constraint PK_ATTACHMENT_TYPE primary key (ATT_TYPE_CD)
);

comment on column ATTACHMENT_TYPE.ATT_TYPE_CD is
'Code';

comment on column ATTACHMENT_TYPE.LABEL is
'Label';

comment on column ATTACHMENT_TYPE.LABEL_FR is
'LabelFr';

insert into ATTACHMENT_TYPE(ATT_TYPE_CD, LABEL, LABEL_FR) values ('DOCUMENT', 'Document', 'Document');
insert into ATTACHMENT_TYPE(ATT_TYPE_CD, LABEL, LABEL_FR) values ('ATTACHMENT', 'Attachment', 'Attachment');


ALTER TABLE ATTACHMENT ADD COLUMN ATT_TYPE_CD VARCHAR(100) DEFAULT 'ATTACHMENT';
comment on column ATTACHMENT.ATT_TYPE_CD is 'Attachment type';

alter table ATTACHMENT
    add constraint FK_A_ATTACHMENT_ATTACHMENT_TYPE_ATTACHMENT_TYPE foreign key (ATT_TYPE_CD)
        references ATTACHMENT_TYPE (ATT_TYPE_CD);

create index A_ATTACHMENT_ATTACHMENT_TYPE_ATTACHMENT_TYPE_FK on ATTACHMENT (ATT_TYPE_CD asc);
