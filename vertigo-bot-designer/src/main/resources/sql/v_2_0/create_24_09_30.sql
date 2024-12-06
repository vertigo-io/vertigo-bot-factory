create sequence SEQ_DOCUMENTARY_RESOURCE_CONTEXT
    start with 1000 cache 1;

create sequence SEQ_QUESTION_ANSWER_CONTEXT
    start with 1000 cache 1;

-- ============================================================
--   Table : DOCUMENTARY_RESOURCE_CONTEXT
-- ============================================================
create table DOCUMENTARY_RESOURCE_CONTEXT
(
    DRC_ID      	 NUMERIC     	not null,
    DRE_ID      	 NUMERIC     	not null,
    CVA_ID      	 NUMERIC     	not null,
    CPV_ID      	 NUMERIC     	,
    constraint PK_DOCUMENTARY_RESOURCE_CONTEXT primary key (DRC_ID)
);

comment on column DOCUMENTARY_RESOURCE_CONTEXT.DRC_ID is
'ID';

comment on column DOCUMENTARY_RESOURCE_CONTEXT.DRE_ID is
'Documentary resource id';

comment on column DOCUMENTARY_RESOURCE_CONTEXT.CVA_ID is
'Context value id';

comment on column DOCUMENTARY_RESOURCE_CONTEXT.CPV_ID is
'Context possible value id';

alter table DOCUMENTARY_RESOURCE_CONTEXT
    add constraint FK_A_DOCUMENTARY_RESOURCE_CONTEXT_CONTEXT_POSSIBLE_VALUE_CONTEXT_POSSIBLE_VALUE foreign key (CPV_ID)
        references CONTEXT_POSSIBLE_VALUE (CPV_ID);

create index A_DOCUMENTARY_RESOURCE_CONTEXT_CONTEXT_POSSIBLE_VALUE_CONTEXT_POSSIBLE_VALUE_FK on DOCUMENTARY_RESOURCE_CONTEXT (CPV_ID asc);

alter table DOCUMENTARY_RESOURCE_CONTEXT
    add constraint FK_A_DOCUMENTARY_RESOURCE_CONTEXT_CONTEXT_VALUE_CONTEXT_VALUE foreign key (CVA_ID)
        references CONTEXT_VALUE (CVA_ID);

create index A_DOCUMENTARY_RESOURCE_CONTEXT_CONTEXT_VALUE_CONTEXT_VALUE_FK on DOCUMENTARY_RESOURCE_CONTEXT (CVA_ID asc);

alter table DOCUMENTARY_RESOURCE_CONTEXT
    add constraint FK_A_DOCUMENTARY_RESOURCE_CONTEXT_DOCUMENTARY_RESOURCE_DOCUMENTARY_RESOURCE foreign key (DRE_ID)
        references DOCUMENTARY_RESOURCE (DRE_ID);

create index A_DOCUMENTARY_RESOURCE_CONTEXT_DOCUMENTARY_RESOURCE_DOCUMENTARY_RESOURCE_FK on DOCUMENTARY_RESOURCE_CONTEXT (DRE_ID asc);

-- ============================================================
--   Table : QUESTION_ANSWER_CONTEXT
-- ============================================================
create table QUESTION_ANSWER_CONTEXT
(
    QAC_ID      	 NUMERIC     	not null,
    QA_ID       	 NUMERIC     	not null,
    CVA_ID      	 NUMERIC     	not null,
    CPV_ID      	 NUMERIC     	,
    constraint PK_QUESTION_ANSWER_CONTEXT primary key (QAC_ID)
);

comment on column QUESTION_ANSWER_CONTEXT.QAC_ID is
'ID';

comment on column QUESTION_ANSWER_CONTEXT.QA_ID is
'Question answer id';

comment on column QUESTION_ANSWER_CONTEXT.CVA_ID is
'Context value id';

comment on column QUESTION_ANSWER_CONTEXT.CPV_ID is
'Context possible value id';

alter table QUESTION_ANSWER_CONTEXT
    add constraint FK_A_QUESTION_ANSWER_CONTEXT_CONTEXT_POSSIBLE_VALUE_CONTEXT_POSSIBLE_VALUE foreign key (CPV_ID)
        references CONTEXT_POSSIBLE_VALUE (CPV_ID);

create index A_QUESTION_ANSWER_CONTEXT_CONTEXT_POSSIBLE_VALUE_CONTEXT_POSSIBLE_VALUE_FK on QUESTION_ANSWER_CONTEXT (CPV_ID asc);

alter table QUESTION_ANSWER_CONTEXT
    add constraint FK_A_QUESTION_ANSWER_CONTEXT_CONTEXT_VALUE_CONTEXT_VALUE foreign key (CVA_ID)
        references CONTEXT_VALUE (CVA_ID);

create index A_QUESTION_ANSWER_CONTEXT_CONTEXT_VALUE_CONTEXT_VALUE_FK on QUESTION_ANSWER_CONTEXT (CVA_ID asc);

alter table QUESTION_ANSWER_CONTEXT
    add constraint FK_A_QUESTION_ANSWER_CONTEXT_QUESTION_ANSWER_QUESTION_ANSWER foreign key (QA_ID)
        references QUESTION_ANSWER (QA_ID);

create index A_QUESTION_ANSWER_CONTEXT_QUESTION_ANSWER_QUESTION_ANSWER_FK on QUESTION_ANSWER_CONTEXT (QA_ID asc);
