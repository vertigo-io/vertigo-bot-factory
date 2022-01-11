create sequence SEQ_WELCOME_TOUR
    start with 1000 cache 20;

-- ============================================================
--   Table : WELCOME_TOUR
-- ============================================================
create table WELCOME_TOUR
(
    WEL_ID      	 NUMERIC     	not null,
    LABEL       	 VARCHAR(100)	not null,
    TECHNICAL_CODE	 VARCHAR(100)	not null,
    BOT_ID      	 NUMERIC     	not null,
    constraint PK_WELCOME_TOUR primary key (WEL_ID)
);

comment on column WELCOME_TOUR.WEL_ID is
'Welcome tour id';

comment on column WELCOME_TOUR.LABEL is
'Label';

comment on column WELCOME_TOUR.TECHNICAL_CODE is
'Technical code';

comment on column WELCOME_TOUR.BOT_ID is
'Chatbot';

alter table WELCOME_TOUR
    add constraint FK_A_WELCOME_TOUR_CHATBOT_CHATBOT foreign key (BOT_ID)
        references CHATBOT (BOT_ID);

create index A_WELCOME_TOUR_CHATBOT_CHATBOT_FK on WELCOME_TOUR (BOT_ID asc);


