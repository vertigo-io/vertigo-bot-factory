-- ============================================================
--   SGBD      		  :  H2                     
-- ============================================================

-- ============================================================
--   Drop                                       
-- ============================================================
drop table IF EXISTS INTENT cascade;
drop sequence IF EXISTS SEQ_INTENT;




-- ============================================================
--   Sequences                                      
-- ============================================================
create sequence SEQ_INTENT
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



