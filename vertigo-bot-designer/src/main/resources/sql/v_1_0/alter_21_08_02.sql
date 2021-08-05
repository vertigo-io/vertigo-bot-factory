create sequence SEQ_TOPIC_LABEL
	start with 1000 cache 20; 

-- ============================================================
--   Table : TOPIC_LABEL                                        
-- ============================================================
create table TOPIC_LABEL
(
    LABEL_ID    	 NUMERIC     	not null,
    LABEL       	 VARCHAR(100)	not null,
    BOT_ID      	 NUMERIC     	not null,
    constraint PK_TOPIC_LABEL primary key (LABEL_ID)
);

comment on column TOPIC_LABEL.LABEL_ID is
'Label id';

comment on column TOPIC_LABEL.LABEL is
'Label label';

comment on column TOPIC_LABEL.BOT_ID is
'Chatbot';

alter table TOPIC_LABEL
	add constraint FK_A_TOPIC_LABEL_CHATBOT_CHATBOT foreign key (BOT_ID)
	references CHATBOT (BOT_ID);
	
alter table topic_label
add constraint label_botId_topic_label unique (bot_id , label);

create index A_TOPIC_LABEL_CHATBOT_CHATBOT_FK on TOPIC_LABEL (BOT_ID asc);


create table TOPIC_TOPIC_LABEL
(
	TOP_ID      	 NUMERIC     	 not null,
	LABEL_ID    	 NUMERIC     	 not null,
	constraint PK_TOPIC_TOPIC_LABEL primary key (TOP_ID, LABEL_ID),
	constraint FK_ANN_TOPIC_CATEGORY_TOPIC 
		foreign key(TOP_ID)
		references TOPIC (TOP_ID),
	constraint FK_ANN_TOPIC_CATEGORY_TOPIC_LABEL 
		foreign key(LABEL_ID)
		references TOPIC_LABEL (LABEL_ID)
);

create index ANN_TOPIC_CATEGORY_TOPIC_FK on TOPIC_TOPIC_LABEL (TOP_ID asc);

create index ANN_TOPIC_CATEGORY_TOPIC_LABEL_FK on TOPIC_TOPIC_LABEL (LABEL_ID asc);
