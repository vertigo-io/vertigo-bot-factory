
create sequence SEQ_TOPIC_CATEGORY
	start with 1000 cache 20; 
	

-- ============================================================
--   Table : TOPIC_CATEGORY                                        
-- ============================================================
create table TOPIC_CATEGORY
(
    TOP_CAT_ID  	 NUMERIC     	not null,
    LABEL       	 VARCHAR(100)	not null,
    LEVEL       	 NUMERIC     	,
    IS_ENABLED  	 bool        	not null,
    BOT_ID      	 NUMERIC     	not null,
    constraint PK_TOPIC_CATEGORY primary key (TOP_CAT_ID)
);

comment on column TOPIC_CATEGORY.TOP_CAT_ID is
'Topic category id';

comment on column TOPIC_CATEGORY.LABEL is
'Topic category label';

comment on column TOPIC_CATEGORY.LEVEL is
'Category level';

comment on column TOPIC_CATEGORY.IS_ENABLED is
'Enabled';

comment on column TOPIC_CATEGORY.BOT_ID is
'Chatbot';

alter table topic
add column TOP_CAT_ID  	 NUMERIC     	not null;

alter table TOPIC
	add constraint FK_A_TOPIC_CATEGORY_TOPIC_TOPIC_CATEGORY foreign key (TOP_CAT_ID)
	references TOPIC_CATEGORY (TOP_CAT_ID);


alter table TOPIC_CATEGORY
	add constraint FK_A_TOPIC_CATEGORY_CHATBOT_CHATBOT foreign key (BOT_ID)
	references CHATBOT (BOT_ID);

create index A_TOPIC_CATEGORY_CHATBOT_CHATBOT_FK on TOPIC_CATEGORY (BOT_ID asc);
