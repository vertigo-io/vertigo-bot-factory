-- ============================================================
--   Table : TYPE_TOPIC                                        
-- ============================================================
create table TYPE_TOPIC
(
    TTO_CD      	 VARCHAR(100)	not null,
    LABEL       	 VARCHAR(100)	not null,
    constraint PK_TYPE_TOPIC primary key (TTO_CD)
);

comment on column TYPE_TOPIC.TTO_CD is
'ID';

comment on column TYPE_TOPIC.LABEL is
'Title';

ALTER TABLE TOPIC
ADD COLUMN TTO_CD VARCHAR(100) not null;

-- Add constraint 
alter table TOPIC
	add constraint FK_A_TOPIC_TYPE_TOPIC foreign key (TTO_CD)
	references TYPE_TOPIC (TTO_CD);
	
create index A_TOPIC_TO_TYPE_TOPIC_FK on TOPIC (TTO_CD asc);

