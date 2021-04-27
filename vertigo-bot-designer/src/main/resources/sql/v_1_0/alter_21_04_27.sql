-- ============================================================
--   Table : KIND_TOPIC                                        
-- ============================================================
create table KIND_TOPIC
(
    KTO_CD      	 VARCHAR(100)	not null,
    LABEL       	 VARCHAR(100)	not null,
    constraint PK_KIND_TOPIC primary key (KTO_CD)
);

comment on column KIND_TOPIC.KTO_CD is
'ID';

comment on column KIND_TOPIC.LABEL is
'Title';

-- ============================================================
--   Insert MasterData values : KIND_TOPIC                                        
-- ============================================================
insert into KIND_TOPIC(KTO_CD, LABEL) values ('START', 'Start');
insert into KIND_TOPIC(KTO_CD, LABEL) values ('END', 'End');
insert into KIND_TOPIC(KTO_CD, LABEL) values ('FAILURE', 'Failure');
insert into KIND_TOPIC(KTO_CD, LABEL) values ('NORMAL', 'Normal');

ALTER TABLE TOPIC
ADD COLUMN KTO_CD VARCHAR(100) ;

-- Add constraint 
alter table TOPIC
	add constraint FK_A_TOPIC_KIND_TOPIC foreign key (KTO_CD)
	references KIND_TOPIC (KTO_CD);
	
create index A_TOPIC_TO_KIND_TOPIC_FK on TOPIC (TTO_CD asc);

UPDATE TOPIC SET KTO_CD = 'NORMAL';

ALTER TABLE TOPIC
ALTER COLUMN KTO_CD SET NOT NULL;

ALTER TABLE CHATBOT
DROP COLUMN UTT_ID_WELCOME,
DROP COLUMN UTT_ID_DEFAULT;

ALTER TABLE RESPONSE_BUTTON
DROP COLUMN BOT_ID_WELCOME,
DROP COLUMN BOT_ID_DEFAULT;

ALTER TABLE TOPIC_CATEGORY
ADD COLUMN IS_TECHNICAL BOOLEAN;
