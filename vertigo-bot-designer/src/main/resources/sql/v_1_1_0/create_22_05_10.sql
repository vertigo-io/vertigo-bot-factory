create sequence SEQ_RATING_OPTION
    start with 1000 cache 20;

create table RATING_OPTION
(
    RA_OPT_CD   	 NUMERIC     	not null,
    LABEL       	 VARCHAR(100)	not null,
    LABEL_FR    	 VARCHAR(100)	not null,
    constraint PK_RATING_OPTION primary key (RA_OPT_CD)
);

comment on column RATING_OPTION.RA_OPT_CD is
'Code';

comment on column RATING_OPTION.LABEL is
'Title';

comment on column RATING_OPTION.LABEL_FR is
'Titre';

insert into RATING_OPTION(RA_OPT_CD, LABEL, LABEL_FR) values ('1', 'One star', 'Une étoile');
insert into RATING_OPTION(RA_OPT_CD, LABEL, LABEL_FR) values ('2', 'Two stars', 'Deux étoiles');
insert into RATING_OPTION(RA_OPT_CD, LABEL, LABEL_FR) values ('3', 'Three stars', 'Trois étoiles');
insert into RATING_OPTION(RA_OPT_CD, LABEL, LABEL_FR) values ('4', 'Four stars', 'Quatre étoiles');
insert into RATING_OPTION(RA_OPT_CD, LABEL, LABEL_FR) values ('5', 'Five stars', 'Cinq étoiles');