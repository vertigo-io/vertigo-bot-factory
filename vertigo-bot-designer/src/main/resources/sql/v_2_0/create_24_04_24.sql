create sequence SEQ_CONFLUENCE_SETTING_SPACE
    start with 1000 cache 1;

create table CONFLUENCE_SETTING_SPACE
(
    CON_SET_SPACE_ID	 NUMERIC     	not null,
    SPACE       	 VARCHAR(100)	not null,
    CONFLUENCESETTING_ID	 NUMERIC     	not null,
    constraint PK_CONFLUENCE_SETTING_SPACE primary key (CON_SET_SPACE_ID)
);

comment on column CONFLUENCE_SETTING_SPACE.CON_SET_SPACE_ID is
'Confluence setting space id';

comment on column CONFLUENCE_SETTING_SPACE.SPACE is
'Space';

comment on column CONFLUENCE_SETTING_SPACE.CONFLUENCESETTING_ID is
'ConfluenceSetting';