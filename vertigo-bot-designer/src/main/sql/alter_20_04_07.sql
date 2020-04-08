create sequence SEQ_RESPONSE_BUTTON
	start with 1000 cache 20;
	
-- ============================================================
--   Table : RESPONSE_BUTTON                                        
-- ============================================================
create table RESPONSE_BUTTON
(
    BTN_ID      	 NUMERIC     	not null,
    TEXT        	 TEXT        	not null,
    SMT_ID      	 NUMERIC     	,
    SMT_ID_RESPONSE	 NUMERIC     	not null,
    BOT_ID_WELCOME	 NUMERIC     	,
    BOT_ID_DEFAULT	 NUMERIC     	,
    constraint PK_RESPONSE_BUTTON primary key (BTN_ID)
);

comment on column RESPONSE_BUTTON.BTN_ID is
'ID';

comment on column RESPONSE_BUTTON.TEXT is
'Text';

comment on column RESPONSE_BUTTON.SMT_ID is
'SmallTalk';

comment on column RESPONSE_BUTTON.SMT_ID_RESPONSE is
'SmallTalkResponse';

comment on column RESPONSE_BUTTON.BOT_ID_WELCOME is
'welcome buttons';

comment on column RESPONSE_BUTTON.BOT_ID_DEFAULT is
'Default buttons';


alter table RESPONSE_BUTTON
	add constraint FK_CHATBOT_DEFAULT_BUTTONS_CHATBOT foreign key (BOT_ID_DEFAULT)
	references CHATBOT (BOT_ID);

create index CHATBOT_DEFAULT_BUTTONS_CHATBOT_FK on RESPONSE_BUTTON (BOT_ID_DEFAULT asc);

alter table RESPONSE_BUTTON
	add constraint FK_CHATBOT_WELCOME_BUTTONS_CHATBOT foreign key (BOT_ID_WELCOME)
	references CHATBOT (BOT_ID);

create index CHATBOT_WELCOME_BUTTONS_CHATBOT_FK on RESPONSE_BUTTON (BOT_ID_WELCOME asc);

alter table RESPONSE_BUTTON
	add constraint FK_RESPONSE_BUTTON_SMALL_TALK_RESPONSE_SMALL_TALK foreign key (SMT_ID_RESPONSE)
	references SMALL_TALK (SMT_ID);

create index RESPONSE_BUTTON_SMALL_TALK_RESPONSE_SMALL_TALK_FK on RESPONSE_BUTTON (SMT_ID_RESPONSE asc);

alter table RESPONSE_BUTTON
	add constraint FK_SMALL_TALK_RESPONSE_BUTTONS_SMALL_TALK foreign key (SMT_ID)
	references SMALL_TALK (SMT_ID);

create index SMALL_TALK_RESPONSE_BUTTONS_SMALL_TALK_FK on RESPONSE_BUTTON (SMT_ID asc);
