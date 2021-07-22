alter table kind_topic
drop column TITLE_ENGLISH;

alter table kind_topic
drop column TITLE_FRENCH;

alter table kind_topic
drop column DESCRIPTION_FRENCH;

alter table kind_topic
drop column DEFAULT_FRENCH;

ALTER TABLE kind_topic 
RENAME COLUMN DESCRIPTION_ENGLISH TO DESCRIPTION;

ALTER TABLE kind_topic 
RENAME COLUMN DEFAULT_ENGLISH TO DEFAULT_TEXT;