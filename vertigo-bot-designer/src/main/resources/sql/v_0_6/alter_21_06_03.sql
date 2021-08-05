alter table SMALL_TALK add column IS_END bool;
comment on column SMALL_TALK.IS_END is
'Is conversation over ?';

UPDATE SMALL_TALK SET IS_END = false;

alter table SMALL_TALK alter column IS_END set not null;
