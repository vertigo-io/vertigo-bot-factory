create sequence SEQ_PROFIL_PER_CHATBOT
start with 1000 cache 20;


-- ============================================================
--   Table : CHATBOT_PROFILES                                        
-- ============================================================
create table CHATBOT_PROFILES
(
    CHP_CD      	 VARCHAR(100)	not null,
    LABEL       	 VARCHAR(100)	not null,
    SORT_ORDER  	 NUMERIC     	not null,
    constraint PK_CHATBOT_PROFILES primary key (CHP_CD)
);

comment on column CHATBOT_PROFILES.CHP_CD is
'ID';

comment on column CHATBOT_PROFILES.LABEL is
'Title';

comment on column CHATBOT_PROFILES.SORT_ORDER is
'Order';



-- ============================================================
--   Table : PROFIL_PER_CHATBOT                                        
-- ============================================================
create table PROFIL_PER_CHATBOT
(
    CHP_ID      	 NUMERIC     	not null,
    BOT_ID      	 NUMERIC     	not null,
    PER_ID      	 NUMERIC     	not null,
    CHP_CD      	 VARCHAR(100)	not null,
    constraint PK_PROFIL_PER_CHATBOT primary key (CHP_ID)
);

comment on column PROFIL_PER_CHATBOT.CHP_ID is
'ID';

comment on column PROFIL_PER_CHATBOT.BOT_ID is
'Chatbot';

comment on column PROFIL_PER_CHATBOT.PER_ID is
'Person';

comment on column PROFIL_PER_CHATBOT.CHP_CD is
'Profil pour un chatbot';


alter table PROFIL_PER_CHATBOT
	add constraint FK_A_P_RROFIL_CHATBO_TO_CHATBOT_CHATBOT foreign key (BOT_ID)
	references CHATBOT (BOT_ID);

create index A_P_RROFIL_CHATBO_TO_CHATBOT_CHATBOT_FK on PROFIL_PER_CHATBOT (BOT_ID asc);

alter table PROFIL_PER_CHATBOT
	add constraint FK_A_P_RROFIL_CHATBOT_TO_CODE_PROFIL_CHATBOT_PROFILES foreign key (CHP_CD)
	references CHATBOT_PROFILES (CHP_CD);

create index A_P_RROFIL_CHATBOT_TO_CODE_PROFIL_CHATBOT_PROFILES_FK on PROFIL_PER_CHATBOT (CHP_CD asc);

alter table PROFIL_PER_CHATBOT
	add constraint FK_A_P_RROFIL_CHATBOT_TO_PERSON_PERSON foreign key (PER_ID)
	references PERSON (PER_ID);

create index A_P_RROFIL_CHATBOT_TO_PERSON_PERSON_FK on PROFIL_PER_CHATBOT (PER_ID asc);

