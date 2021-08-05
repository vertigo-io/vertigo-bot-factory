drop table IF EXISTS TYPE_EXPORT_ANALYTICS cascade;

-- ============================================================
--   Table : TYPE_EXPORT_ANALYTICS                                        
-- ============================================================
create table TYPE_EXPORT_ANALYTICS
(
    TEA_CD      	 VARCHAR(100)	not null,
    LABEL       	 VARCHAR(100)	not null,
    LABEL_FR    	 VARCHAR(100)	not null,
    constraint PK_TYPE_EXPORT_ANALYTICS primary key (TEA_CD)
);

comment on column TYPE_EXPORT_ANALYTICS.TEA_CD is
'Code';

comment on column TYPE_EXPORT_ANALYTICS.LABEL is
'Title';

comment on column TYPE_EXPORT_ANALYTICS.LABEL_FR is
'Titre';

-- ============================================================
--   Insert MasterData values : TYPE_EXPORT_ANALYTICS                                        
-- ============================================================
insert into TYPE_EXPORT_ANALYTICS(TEA_CD, LABEL, LABEL_FR) values ('UNKNOWN_MESSAGES', 'Unknown messages', 'Messages inconnus');
insert into TYPE_EXPORT_ANALYTICS(TEA_CD, LABEL, LABEL_FR) values ('SESSIONS', 'Sessions', 'Sessions');
