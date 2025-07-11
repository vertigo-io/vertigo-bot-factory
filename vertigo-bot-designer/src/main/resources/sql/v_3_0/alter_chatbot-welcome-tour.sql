-- ============================================================
--   Table : WELCOME_TOUR_STEP_ADVANCE_EVENT
-- ============================================================
create table WELCOME_TOUR_STEP_ADVANCE_EVENT
(
    STEP_ADV_CD 	 VARCHAR(100)	not null,
    LABEL       	 VARCHAR(100)	not null,
    LABEL_FR    	 VARCHAR(100)	not null,
    constraint PK_WELCOME_TOUR_STEP_ADVANCE_EVENT primary key (STEP_ADV_CD)
);

comment on column WELCOME_TOUR_STEP_ADVANCE_EVENT.STEP_ADV_CD is
    'Code';

comment on column WELCOME_TOUR_STEP_ADVANCE_EVENT.LABEL is
    'Title';

comment on column WELCOME_TOUR_STEP_ADVANCE_EVENT.LABEL_FR is
    'TitleFr';

insert into WELCOME_TOUR_STEP_ADVANCE_EVENT(STEP_ADV_CD, LABEL, LABEL_FR) values ('CLICK', 'Click', 'Click');
insert into WELCOME_TOUR_STEP_ADVANCE_EVENT(STEP_ADV_CD, LABEL, LABEL_FR) values ('FOCUS', 'Focus', 'Focus');
insert into WELCOME_TOUR_STEP_ADVANCE_EVENT(STEP_ADV_CD, LABEL, LABEL_FR) values ('SUBMIT', 'Submit', 'Soumission formulaire');
insert into WELCOME_TOUR_STEP_ADVANCE_EVENT(STEP_ADV_CD, LABEL, LABEL_FR) values ('OVER', 'Mouse over', 'Survol de la souris');

-- ============================================================
--   Table : WELCOME_TOUR_STEP_PLACEMENT
-- ============================================================
create table WELCOME_TOUR_STEP_PLACEMENT
(
    STEP_PL_CD  	 VARCHAR(100)	not null,
    LABEL       	 VARCHAR(100)	not null,
    LABEL_FR    	 VARCHAR(100)	not null,
    constraint PK_WELCOME_TOUR_STEP_PLACEMENT primary key (STEP_PL_CD)
);

comment on column WELCOME_TOUR_STEP_PLACEMENT.STEP_PL_CD is
    'Code';

comment on column WELCOME_TOUR_STEP_PLACEMENT.LABEL is
    'Title';

comment on column WELCOME_TOUR_STEP_PLACEMENT.LABEL_FR is
    'TitleFr';

insert into WELCOME_TOUR_STEP_PLACEMENT(STEP_PL_CD, LABEL, LABEL_FR) values ('AUTO', 'Automatic', 'Automatique');
insert into WELCOME_TOUR_STEP_PLACEMENT(STEP_PL_CD, LABEL, LABEL_FR) values ('RIGHT', 'Right', 'A droite');
insert into WELCOME_TOUR_STEP_PLACEMENT(STEP_PL_CD, LABEL, LABEL_FR) values ('LEFT', 'Left', 'A gauche');
insert into WELCOME_TOUR_STEP_PLACEMENT(STEP_PL_CD, LABEL, LABEL_FR) values ('TOP', 'Top', 'En haut');
insert into WELCOME_TOUR_STEP_PLACEMENT(STEP_PL_CD, LABEL, LABEL_FR) values ('BOTTOM', 'Bottom', 'En bas');


ALTER TABLE welcome_tour DROP COLUMN config;
ALTER TABLE welcome_tour ADD COLUMN USE_MODAL_OVERLAY bool not null DEFAULT true;
ALTER TABLE welcome_tour ADD COLUMN USE_CANCEL_ICON	 bool not null DEFAULT true;
ALTER TABLE welcome_tour ADD COLUMN NEXT_BUTTON_LABEL VARCHAR(100) not null DEFAULT 'Next';
ALTER TABLE welcome_tour ADD COLUMN PREVIOUS_BUTTON_LABEL VARCHAR(100) not null DEFAULT 'Previous';
ALTER TABLE welcome_tour ADD COLUMN COMPLETE_BUTTON_LABEL VARCHAR(100) not null DEFAULT 'Terminer';
ALTER TABLE welcome_tour ADD COLUMN STEPS_CSS_CLASSES VARCHAR(100);

comment on column WELCOME_TOUR.USE_MODAL_OVERLAY is
    'Use modal overlay';

comment on column WELCOME_TOUR.USE_CANCEL_ICON is
    'Use cancel icon';

comment on column WELCOME_TOUR.NEXT_BUTTON_LABEL is
    'Next button label';

comment on column WELCOME_TOUR.PREVIOUS_BUTTON_LABEL is
    'Next button label';

comment on column WELCOME_TOUR.COMPLETE_BUTTON_LABEL is
    'Complete button label';

comment on column WELCOME_TOUR.STEPS_CSS_CLASSES is
    'Steps CSS classes';

ALTER TABLE WELCOME_TOUR_STEP DROP COLUMN internal_step_id;
ALTER TABLE WELCOME_TOUR_STEP ADD COLUMN ELEMENT_ATTACH_TO VARCHAR(100)	not null DEFAULT 'body';
ALTER TABLE WELCOME_TOUR_STEP ADD COLUMN ELEMENT_ATTACH_TO_PLACEMENT VARCHAR(100) not null DEFAULT 'AUTO';
ALTER TABLE WELCOME_TOUR_STEP ADD COLUMN ADVANCE_ON VARCHAR(100);
ALTER TABLE WELCOME_TOUR_STEP ADD COLUMN EVENT_ADVANCE_ON VARCHAR(100);
ALTER TABLE WELCOME_TOUR_STEP ADD COLUMN DISPLAY_NEXT_BUTTON bool not null DEFAULT true;
ALTER TABLE WELCOME_TOUR_STEP ADD COLUMN DISPLAY_PREVIOUS_BUTTON bool not null DEFAULT true;

comment on column WELCOME_TOUR_STEP.ELEMENT_ATTACH_TO is
    'Element attached to';

comment on column WELCOME_TOUR_STEP.ADVANCE_ON is
    'Advance on';

comment on column WELCOME_TOUR_STEP.DISPLAY_NEXT_BUTTON is
    'Display next button';

comment on column WELCOME_TOUR_STEP.DISPLAY_PREVIOUS_BUTTON is
    'Display previous button';

comment on column WELCOME_TOUR_STEP.ELEMENT_ATTACH_TO_PLACEMENT is
    'Placement';

comment on column WELCOME_TOUR_STEP.EVENT_ADVANCE_ON is
    'Event to advance on';

alter table WELCOME_TOUR_STEP
    add constraint FK_A_WELCOME_TOUR_STEP_WELCOME_TOUR_STEP_ADVANCE_EVENT_WELCOME_TOUR_STEP_ADVANCE_EVENT foreign key (EVENT_ADVANCE_ON)
        references WELCOME_TOUR_STEP_ADVANCE_EVENT (STEP_ADV_CD);

create index A_WELCOME_TOUR_STEP_WELCOME_TOUR_STEP_ADVANCE_EVENT_WELCOME_TOUR_STEP_ADVANCE_EVENT_FK on WELCOME_TOUR_STEP (EVENT_ADVANCE_ON asc);

alter table WELCOME_TOUR_STEP
    add constraint FK_A_WELCOME_TOUR_STEP_WELCOME_TOUR_STEP_PLACEMENT_WELCOME_TOUR_STEP_PLACEMENT foreign key (ELEMENT_ATTACH_TO_PLACEMENT)
        references WELCOME_TOUR_STEP_PLACEMENT (STEP_PL_CD);

create index A_WELCOME_TOUR_STEP_WELCOME_TOUR_STEP_PLACEMENT_WELCOME_TOUR_STEP_PLACEMENT_FK on WELCOME_TOUR_STEP (ELEMENT_ATTACH_TO_PLACEMENT asc);
