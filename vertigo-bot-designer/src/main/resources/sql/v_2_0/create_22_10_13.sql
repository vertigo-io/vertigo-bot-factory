create sequence SEQ_MONITORING_ALERTING_SUBSCRIPTION
    start with 1000 cache 20;

create table MONITORING_ALERTING_SUBSCRIPTION
(
    MAS_I_D     	 NUMERIC     	not null,
    ALERTING_GLOBAL	 bool        	not null,
    PER_ID      	 NUMERIC     	,
    constraint PK_MONITORING_ALERTING_SUBSCRIPTION primary key (MAS_I_D)
);

comment on column MONITORING_ALERTING_SUBSCRIPTION.MAS_I_D is
'ID';

comment on column MONITORING_ALERTING_SUBSCRIPTION.ALERTING_GLOBAL is
'Alerting global enabled';

comment on column MONITORING_ALERTING_SUBSCRIPTION.PER_ID is
'Person';

alter table MONITORING_ALERTING_SUBSCRIPTION
    add constraint FK_A_MONITORING_ALERTING_SUBSCRIPTION_PERSON_PERSON foreign key (PER_ID)
        references PERSON (PER_ID);

create index A_MONITORING_ALERTING_SUBSCRIPTION_PERSON_PERSON_FK on MONITORING_ALERTING_SUBSCRIPTION (PER_ID asc);

create table ALERTING_SUBSCRIPTION_CHATBOT
(
    MAS_I_D     	 NUMERIC     	 not null,
    BOT_ID      	 NUMERIC     	 not null,
    constraint PK_ALERTING_SUBSCRIPTION_CHATBOT primary key (MAS_I_D, BOT_ID),
    constraint FK_ANN_ALERTING_SUBSCRIPTION_CHATBOT_MONITORING_ALERTING_SUBSCRIPTION
        foreign key(MAS_I_D)
            references MONITORING_ALERTING_SUBSCRIPTION (MAS_I_D),
    constraint FK_ANN_ALERTING_SUBSCRIPTION_CHATBOT_CHATBOT
        foreign key(BOT_ID)
            references CHATBOT (BOT_ID)
);

create index ANN_ALERTING_SUBSCRIPTION_CHATBOT_MONITORING_ALERTING_SUBSCRIPTION_FK on ALERTING_SUBSCRIPTION_CHATBOT (MAS_I_D asc);

create index ANN_ALERTING_SUBSCRIPTION_CHATBOT_CHATBOT_FK on ALERTING_SUBSCRIPTION_CHATBOT (BOT_ID asc);

create sequence SEQ_ALERTING_EVENT
    start with 1000 cache 20;

create table ALERTING_EVENT
(
    AGE_ID      	 NUMERIC     	not null,
    DATE        	 TIMESTAMP   	not null,
    COMPONENT_NAME	 VARCHAR(100)	not null,
    ALIVE       	 bool        	not null,
    BOT_ID      	 NUMERIC     	,
    NODE_ID     	 NUMERIC     	,
    constraint PK_ALERTING_EVENT primary key (AGE_ID)
);

comment on column ALERTING_EVENT.AGE_ID is
'ID';

comment on column ALERTING_EVENT.DATE is
'Date';

comment on column ALERTING_EVENT.COMPONENT_NAME is
'Component name';

comment on column ALERTING_EVENT.ALIVE is
'Alive';

comment on column ALERTING_EVENT.BOT_ID is
'Chatbot';

comment on column ALERTING_EVENT.NODE_ID is
'Node';

alter table ALERTING_EVENT
    add constraint FK_A_ALERTING_EVENT_CHATBOT_CHATBOT foreign key (BOT_ID)
        references CHATBOT (BOT_ID);

create index A_ALERTING_EVENT_CHATBOT_CHATBOT_FK on ALERTING_EVENT (BOT_ID asc);

alter table ALERTING_EVENT
    add constraint FK_A_ALERTING_EVENT_NODE_CHATBOT_NODE foreign key (NODE_ID)
        references CHATBOT_NODE (NOD_ID);

create index A_ALERTING_EVENT_NODE_CHATBOT_NODE_FK on ALERTING_EVENT (NODE_ID asc);


