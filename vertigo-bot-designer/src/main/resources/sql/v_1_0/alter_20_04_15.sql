create index PERSON_ROLE_PERSON_ROLE_FK on PERSON (ROL_CD asc);


create index CHA_PER_RIGHTS_PERSON_FK on CHA_PER_RIGHTS (PER_ID asc);

create index CHA_PER_RIGHTS_CHATBOT_FK on CHA_PER_RIGHTS (BOT_ID asc);
