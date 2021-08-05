alter table topic_category
add column code varchar(10);

update topic_category
set code = substr(label, 0, 10);

alter table topic_category
alter column code set not null;

alter table topic_category
add constraint topic_category_unique_code UNIQUE (bot_id,code);

