create table TYPE_BOT_EXPORT
(
    TBE_CD      	 VARCHAR(100)	not null,
    LABEL       	 VARCHAR(100)	not null,
    LABEL_FR    	 VARCHAR(100)	not null,
    constraint PK_TYPE_BOT_EXPORT primary key (TBE_CD)
);

comment on column TYPE_BOT_EXPORT.TBE_CD is
'Code';

comment on column TYPE_BOT_EXPORT.LABEL is
'Title';

comment on column TYPE_BOT_EXPORT.LABEL_FR is
'Titre';

insert into TYPE_BOT_EXPORT(TBE_CD, LABEL, LABEL_FR) values ('CATEGORIES', 'Categories', 'Catégories');
insert into TYPE_BOT_EXPORT(TBE_CD, LABEL, LABEL_FR) values ('TOPICS', 'Topics', 'Intentions');
insert into TYPE_BOT_EXPORT(TBE_CD, LABEL, LABEL_FR) values ('DICTIONARY', 'Dictionary', 'Dictionnaire');