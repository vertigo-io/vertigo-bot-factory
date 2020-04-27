
-- ============================================================
--   Table : PERSON                                        
-- ============================================================
alter table PERSON ADD ROL_CD  VARCHAR(100);	--not null default 'RUser';

comment on column PERSON.ROL_CD is
'Role';

-- ============================================================
--   Table : PERSON_ROLE                                        
-- ============================================================
create table PERSON_ROLE
(
    ROL_CD      	 VARCHAR(100)	not null,
    LABEL       	 VARCHAR(100)	not null,
    SORT_ORDER  	 NUMERIC     	not null,
    constraint PK_PERSON_ROLE primary key (ROL_CD)
);

comment on column PERSON_ROLE.ROL_CD is
'Code';

comment on column PERSON_ROLE.LABEL is
'Label';

comment on column PERSON_ROLE.SORT_ORDER is
'Order';


alter table PERSON
	add constraint FK_PERSON_ROLE_PERSON_ROLE foreign key (ROL_CD)
	references PERSON_ROLE (ROL_CD);

create index PERSON_ROLE_PERSON_ROLE_FK on PERSON (ROL_CD asc);


create sequence SEQ_CHA_PER_RIGHTS
	start with 1000 cache 20;

create table CHA_PER_RIGHTS
(
	PER_ID      	 NUMERIC     	 not null,
	BOT_ID      	 NUMERIC     	 not null,
	constraint PK_CHA_PER_RIGHTS primary key (PER_ID, BOT_ID),
	constraint FK_hatbotPerson_PERSON 
		foreign key(PER_ID)
		references PERSON (PER_ID),
	constraint FK_hatbotPerson_CHATBOT 
		foreign key(BOT_ID)
		references CHATBOT (BOT_ID)
);

create index hatbotPerson_PERSON_FK on CHA_PER_RIGHTS (PER_ID asc);

create index hatbotPerson_CHATBOT_FK on CHA_PER_RIGHTS (BOT_ID asc);
