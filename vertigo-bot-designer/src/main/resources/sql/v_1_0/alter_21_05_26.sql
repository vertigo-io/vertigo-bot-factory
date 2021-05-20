drop table IF EXISTS TRAINING_STATUS cascade;
alter table TRAINING add column STR_CD VARCHAR(100);

comment on column TRAINING.STR_CD is 'Status';

-- ============================================================
--   Table : TRAINING_STATUS                                        
-- ============================================================
create table TRAINING_STATUS
(
    STR_CD      	 VARCHAR(100)	not null,
    LABEL       	 VARCHAR(100)	not null,
    LABEL_FR    	 VARCHAR(100)	not null,
    constraint PK_TRAINING_STATUS primary key (STR_CD)
);

comment on column TRAINING_STATUS.STR_CD is
'ID';

comment on column TRAINING_STATUS.LABEL is
'Label';

comment on column TRAINING_STATUS.LABEL_FR is
'LabelFr';

alter table TRAINING
	add constraint FK_A_TRAINING_TRAINING_STATUS_TRAINING_STATUS foreign key (STR_CD)
	references TRAINING_STATUS (STR_CD);

create index A_TRAINING_TRAINING_STATUS_TRAINING_STATUS_FK on TRAINING (STR_CD asc);

-- ============================================================
--   Insert MasterData values : TRAINING_STATUS                                        
-- ============================================================
insert into TRAINING_STATUS(STR_CD, LABEL, LABEL_FR) values ('OK', 'OK', 'OK');
insert into TRAINING_STATUS(STR_CD, LABEL, LABEL_FR) values ('KO', 'KO', 'KO');
insert into TRAINING_STATUS(STR_CD, LABEL, LABEL_FR) values ('TRAINING', 'TRAINING', 'En cours');

UPDATE training set str_cd = status;

alter table TRAINING drop column status;
alter table TRAINING alter column str_cd set not null;

