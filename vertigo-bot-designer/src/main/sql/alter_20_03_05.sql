-- ============================================================
--   Table : RESPONSE_TYPE                                        
-- ============================================================
create table RESPONSE_TYPE
(
    RTY_ID      	 VARCHAR(100)	not null,
    LABEL       	 VARCHAR(100)	not null,
    SORT_ORDER  	 NUMERIC     	not null,
    constraint PK_RESPONSE_TYPE primary key (RTY_ID)
);

comment on column RESPONSE_TYPE.RTY_ID is
'ID';

comment on column RESPONSE_TYPE.LABEL is
'Title';


-- ============================================================
--   Insert MasterData values : RESPONSE_TYPE                                        
-- ============================================================
insert into RESPONSE_TYPE(RTY_ID, LABEL, SORT_ORDER) values ('RICH_TEXT', 'Rich text', '1');
insert into RESPONSE_TYPE(RTY_ID, LABEL, SORT_ORDER) values ('RANDOM_TEXT', 'Random text', '2');



-- =========================
-- ALTER TABLE SMALL_TALK
-- =========================
ALTER TABLE SMALL_TALK 
ADD COLUMN RTY_ID      	 VARCHAR(100)	not null	default 'RICH_TEXT';

comment on column SMALL_TALK.RTY_ID is
'Response type';


alter table SMALL_TALK
	add constraint FK_SMALL_TALK_RESPONSE_TYPE_RESPONSE_TYPE foreign key (RTY_ID)
	references RESPONSE_TYPE (RTY_ID);

create index SMALL_TALK_RESPONSE_TYPE_RESPONSE_TYPE_FK on SMALL_TALK (RTY_ID asc);


-- random text if already multiple responses
UPDATE SMALL_TALK
SET RTY_ID = 'RANDOM_TEXT'
WHERE SMT_ID in
  (SELECT SMT_ID
   FROM UTTER_TEXT
   GROUP BY SMT_ID
   HAVING count(SMT_ID) > 1
  )

-- update to new format for pause
UPDATE UTTER_TEXT
SET text = replace(text, '[pause]', '<hr>')