create table CHATBOT_FORMAT
(
    CFT_CD      	 VARCHAR(100)	not null,
    LABEL       	 VARCHAR(100)	not null,
    LABEL_FR    	 VARCHAR(100)	not null,
    SORT_ORDER  	 NUMERIC     	not null,
    constraint PK_CHATBOT_FORMAT primary key (CFT_CD)
);

comment on column CHATBOT_FORMAT.CFT_CD is
'ID';

comment on column CHATBOT_FORMAT.LABEL is
'Title';

comment on column CHATBOT_FORMAT.LABEL_FR is
'TitleFr';

comment on column CHATBOT_FORMAT.SORT_ORDER is
'Order';

ALTER TABLE CHATBOT_CUSTOM_CONFIG ADD COLUMN CFT_CD VARCHAR(100);
ALTER TABLE CHATBOT_CUSTOM_CONFIG DROP COLUMN disable_nlu;

alter table CHATBOT_CUSTOM_CONFIG
    add constraint FK_A_CHABOT_CUSTOM_CONFIG_CHATBOT_FORMAT_CHATBOT_FORMAT foreign key (CFT_CD)
        references CHATBOT_FORMAT (CFT_CD);

create index A_CHABOT_CUSTOM_CONFIG_CHATBOT_FORMAT_CHATBOT_FORMAT_FK on CHATBOT_CUSTOM_CONFIG (CFT_CD asc);

insert into CHATBOT_FORMAT(CFT_CD, LABEL, LABEL_FR, SORT_ORDER) values ('CLASSIC', 'Classic Chatbot', 'Chatbot classique', '1');
insert into CHATBOT_FORMAT(CFT_CD, LABEL, LABEL_FR, SORT_ORDER) values ('INTERACTIVE_HELP', 'Interactive Help', 'Aide intéractive', '2');
insert into CHATBOT_FORMAT(CFT_CD, LABEL, LABEL_FR, SORT_ORDER) values ('FAQ', 'Dynamic FAQ', 'FAQ dynamique', '3');

UPDATE CHATBOT_CUSTOM_CONFIG set CFT_CD = 'CLASSIC' where CFT_CD IS NULL;

