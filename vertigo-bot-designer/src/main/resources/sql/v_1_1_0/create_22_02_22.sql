create sequence SEQ_RESPONSE_BUTTON_URL
    start with 1000 cache 20;

-- ============================================================
--   Table : RESPONSE_BUTTON_URL
-- ============================================================
create table RESPONSE_BUTTON_URL
(
    BTN_ID      	 NUMERIC     	not null,
    TEXT        	 TEXT        	not null,
    URL         	 TEXT        	not null,
    NEW_TAB     	 bool        	not null,
    SMT_ID      	 NUMERIC     	,
    constraint PK_RESPONSE_BUTTON_URL primary key (BTN_ID)
);

comment on column RESPONSE_BUTTON_URL.BTN_ID is
'ID';

comment on column RESPONSE_BUTTON_URL.TEXT is
'Text';

comment on column RESPONSE_BUTTON_URL.URL is
'URL';

comment on column RESPONSE_BUTTON_URL.NEW_TAB is
'New tab';

comment on column RESPONSE_BUTTON_URL.SMT_ID is
'SmallTalk';

alter table RESPONSE_BUTTON_URL
    add constraint FK_A_SMALL_TALK_RESPONSE_BUTTONS_URL_SMALL_TALK foreign key (SMT_ID)
        references SMALL_TALK (SMT_ID);

create index A_SMALL_TALK_RESPONSE_BUTTONS_URL_SMALL_TALK_FK on RESPONSE_BUTTON_URL (SMT_ID asc);
