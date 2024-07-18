create sequence SEQ_CONTEXT_POSSIBLE_VALUE
    start with 1000 cache 1;

-- ============================================================
--   Table : CONTEXT_POSSIBLE_VALUE
-- ============================================================
create table CONTEXT_POSSIBLE_VALUE
(
    CPV_ID      	 NUMERIC     	not null,
    VALUE       	 VARCHAR(100)	not null,
    CV_ID       	 NUMERIC     	not null,
    constraint PK_CONTEXT_POSSIBLE_VALUE primary key (CPV_ID)
);

comment on column CONTEXT_POSSIBLE_VALUE.CPV_ID is
'Context possible value id';

comment on column CONTEXT_POSSIBLE_VALUE.VALUE is
'Value';

comment on column CONTEXT_POSSIBLE_VALUE.CV_ID is
'Context value';

alter table CONTEXT_POSSIBLE_VALUE
    add constraint FK_A_CONTEXT_POSSIBLE_VALUE_CONTEXT_VALUE_CONTEXT_VALUE foreign key (CV_ID)
        references CONTEXT_VALUE (CVA_ID);

create index A_CONTEXT_POSSIBLE_VALUE_CONTEXT_VALUE_CONTEXT_VALUE_FK on CONTEXT_POSSIBLE_VALUE (CV_ID asc);
