-- ============================================================
--   SGBD      		  :  H2                     
-- ============================================================

-- ============================================================
--   Drop                                       
-- ============================================================
drop table IF EXISTS INTENT cascade;
drop sequence IF EXISTS SEQ_INTENT;
drop table IF EXISTS INTENT_TEXT cascade;
drop sequence IF EXISTS SEQ_INTENT_TEXT;




-- ============================================================
--   Sequences                                      
-- ============================================================
create sequence SEQ_INTENT
	start with 1000 cache 20; 

create sequence SEQ_INTENT_TEXT
	start with 1000 cache 20; 


-- ============================================================
--   Table : INTENT                                        
-- ============================================================
create table INTENT
(
    INT_ID      	 NUMERIC     	not null,
    TITLE       	 TEXT        	not null,
    constraint PK_INTENT primary key (INT_ID)
);

comment on column INTENT.INT_ID is
'ID';

comment on column INTENT.TITLE is
'Titre';

-- ============================================================
--   Table : INTENT_TEXT                                        
-- ============================================================
create table INTENT_TEXT
(
    ITT_ID      	 NUMERIC     	not null,
    TEXT        	 TEXT        	not null,
    INT_ID      	 NUMERIC     	not null,
    constraint PK_INTENT_TEXT primary key (ITT_ID)
);

comment on column INTENT_TEXT.ITT_ID is
'ID';

comment on column INTENT_TEXT.TEXT is
'Text';

comment on column INTENT_TEXT.INT_ID is
'Intent';


alter table INTENT_TEXT
	add constraint FK_INTENT_INTENT_TEXT_INTENT foreign key (INT_ID)
	references INTENT (INT_ID);

create index INTENT_INTENT_TEXT_INTENT_FK on INTENT_TEXT (INT_ID asc);


