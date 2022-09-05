ALTER TABLE WELCOME_TOUR ADD COLUMN CONFIG TEXT DEFAULT '{}';
comment on column WELCOME_TOUR.CONFIG is
'Shepherd config';

create sequence SEQ_WELCOME_TOUR_STEP
    start with 1000 cache 20;

-- ============================================================
--   Table : WELCOME_TOUR_STEP
-- ============================================================
create table WELCOME_TOUR_STEP
(
    WEL_STEP_ID 	 NUMERIC     	not null,
    INTERNAL_STEP_ID	 VARCHAR(100)	not null,
    TEXT        	 TEXT        	not null,
    TITLE       	 VARCHAR(100)	not null,
    SEQUENCE    	 NUMERIC     	not null,
    ENABLED     	 bool        	not null,
    TOUR_ID     	 NUMERIC     	,
    constraint PK_WELCOME_TOUR_STEP primary key (WEL_STEP_ID)
);

comment on column WELCOME_TOUR_STEP.WEL_STEP_ID is
'Welcome tour step id';

comment on column WELCOME_TOUR_STEP.INTERNAL_STEP_ID is
'Internal step id';

comment on column WELCOME_TOUR_STEP.TEXT is
'Text';

comment on column WELCOME_TOUR_STEP.TITLE is
'Title';

comment on column WELCOME_TOUR_STEP.SEQUENCE is
'Sequence';

comment on column WELCOME_TOUR_STEP.ENABLED is
'Enabled';

comment on column WELCOME_TOUR_STEP.TOUR_ID is
'Tour';

alter table WELCOME_TOUR_STEP
    add constraint FK_A_WELCOME_TOUR_WELCOME_TOUR_STEPS_WELCOME_TOUR foreign key (TOUR_ID)
        references WELCOME_TOUR (WEL_ID);

create index A_WELCOME_TOUR_WELCOME_TOUR_STEPS_WELCOME_TOUR_FK on WELCOME_TOUR_STEP (TOUR_ID asc);

