alter table topic
alter column code set data TYPE varchar(10);

update  topic
set code = concat('rat', code);

