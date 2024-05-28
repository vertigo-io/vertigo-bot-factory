alter table SAVED_TRAINING
    add column ATT_FILE_INFO_ID numeric,
    add constraint FK_A_SAVED_TRAINING_ATTACHMENT_FILE_INFO foreign key (ATT_FILE_INFO_ID)
        references ATTACHMENT_FILE_INFO (ATT_FI_ID);

create index A_SAVED_TRAINING_ATTACHMENT_FILE_INFO_FK on SAVED_TRAINING (ATT_FILE_INFO_ID asc);

comment on column SAVED_TRAINING.ATT_FILE_INFO_ID is 'Attachment File Info';
