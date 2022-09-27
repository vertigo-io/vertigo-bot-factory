create table FONT_FAMILY
(
    FOF_CD      	 VARCHAR(100)	not null,
    LABEL       	 VARCHAR(100)	not null,
    LABEL_FR    	 VARCHAR(100)	not null,
    constraint PK_FONT_FAMILY primary key (FOF_CD)
);

comment on column FONT_FAMILY.FOF_CD is
'ID';

comment on column FONT_FAMILY.LABEL is
'Title';

comment on column FONT_FAMILY.LABEL_FR is
'TitleFr';

ALTER TABLE CHATBOT_CUSTOM_CONFIG ADD COLUMN FOF_CD VARCHAR(100);

comment on column CHATBOT_CUSTOM_CONFIG.FOF_CD is
'fontFamily';

alter table CHATBOT_CUSTOM_CONFIG
    add constraint FK_A_CHABOT_CUSTOM_CONFIG_FONT_FAMILY_FONT_FAMILY foreign key (FOF_CD)
        references FONT_FAMILY (FOF_CD);

create index A_CHABOT_CUSTOM_CONFIG_FONT_FAMILY_FONT_FAMILY_FK on CHATBOT_CUSTOM_CONFIG (FOF_CD asc);

insert into FONT_FAMILY(FOF_CD, LABEL, LABEL_FR) values ('ARIAL', 'Arial, sans-serif', 'Arial, sans-serif');
insert into FONT_FAMILY(FOF_CD, LABEL, LABEL_FR) values ('VERDANA', 'Verdana, sans-serif', 'Verdana, sans-serif');
insert into FONT_FAMILY(FOF_CD, LABEL, LABEL_FR) values ('HELVETICA', 'Helvetica, sans-serif', 'Helvetica, sans-serif');
insert into FONT_FAMILY(FOF_CD, LABEL, LABEL_FR) values ('TAHOMA', 'Tahoma, sans-serif', 'Tahoma, sans-serif');
insert into FONT_FAMILY(FOF_CD, LABEL, LABEL_FR) values ('TREBUCHET', 'Trebuchet MS, sans-serif', 'Trebuchet MS, sans-serif');
insert into FONT_FAMILY(FOF_CD, LABEL, LABEL_FR) values ('TIME_NEW_ROMAN', 'Times New Roman, serif', 'Times New Roman, serif');
insert into FONT_FAMILY(FOF_CD, LABEL, LABEL_FR) values ('GEORGIA', 'Georgia, serif', 'Georgia, serif');
insert into FONT_FAMILY(FOF_CD, LABEL, LABEL_FR) values ('GARAMOND', 'Garamond, serif', 'Garamond, serif');
insert into FONT_FAMILY(FOF_CD, LABEL, LABEL_FR) values ('COURIER', 'Courier New, monospace', 'Courier New, monospace');
insert into FONT_FAMILY(FOF_CD, LABEL, LABEL_FR) values ('BRUSH', 'Brush Script MT, cursive', 'Brush Script MT, cursive');

UPDATE chatbot_custom_config SET fof_cd = font_family.fof_cd FROM font_family WHERE font_family.label = chatbot_custom_config.font_family;

ALTER TABLE CHATBOT_CUSTOM_CONFIG DROP COLUMN font_family;