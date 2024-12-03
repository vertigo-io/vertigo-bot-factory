-- ============================================================
--   Table : TYPE_OPERATOR
-- ============================================================
create table TYPE_OPERATOR
(
    TYOP_CD      	 VARCHAR(100)	not null,
    LABEL       	 VARCHAR(100)	not null,
    LABEL_FR    	 VARCHAR(100)	not null,
    constraint PK_TYPE_OPERATOR primary key (TYOP_CD)
);

comment on column TYPE_OPERATOR.TYOP_CD is
'ID';

comment on column TYPE_OPERATOR.LABEL is
'Label';

comment on column TYPE_OPERATOR.LABEL_FR is
'LabelFr';


-- ============================================================
--   Insert MasterData values : TYPE_OPERATOR
-- ============================================================
insert into TYPE_OPERATOR(TYOP_CD, LABEL, LABEL_FR) values ('EQUALS', 'Equals', 'Égal');
insert into TYPE_OPERATOR(TYOP_CD, LABEL, LABEL_FR) values ('CONTAINS', 'Contains', 'Contient');


ALTER TABLE CONTEXT_POSSIBLE_VALUE
    ADD COLUMN TYOP_CD VARCHAR(100) ;

alter table CONTEXT_POSSIBLE_VALUE
    add constraint FK_A_CONTEXT_POSSIBLE_VALUE_TYPE_OPERATOR_TYPE_OPERATOR foreign key (TYOP_CD)
        references TYPE_OPERATOR (TYOP_CD);

create index A_CONTEXT_POSSIBLE_VALUE_TYPE_OPERATOR_TYPE_OPERATOR_FK on CONTEXT_POSSIBLE_VALUE (TYOP_CD asc);


ALTER TABLE CONTEXT_ENVIRONMENT_VALUE
    ADD COLUMN TYOP_CD VARCHAR(100) DEFAULT 'EQUALS';

alter table CONTEXT_ENVIRONMENT_VALUE
    add constraint FK_A_CONTEXT_ENVIRONMENT_VALUE_TYPE_OPERATOR_TYPE_OPERATOR foreign key (TYOP_CD)
        references TYPE_OPERATOR (TYOP_CD);

create index A_CONTEXT_ENVIRONMENT_VALUE_TYPE_OPERATOR_TYPE_OPERATOR_FK on CONTEXT_ENVIRONMENT_VALUE (TYOP_CD asc);



