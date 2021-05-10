alter table topic 
add column code numeric;

create or replace function createCode() 
	returns void as '
declare 
	_botId int;
begin 
	for _botId in
		select bot_id from chatbot
	loop
		with rownumbers as (
			select top.top_id,
			row_number() over() as code
			from topic top
			where top.bot_id = _botId
			order by top_id
		)
			update topic 
			set code = rownumbers.code
			from rownumbers 
			where rownumbers.top_id = topic.top_id;
		end loop;
	return;
end;
' language plpgsql;

select createCode();

drop function createCode;

alter table topic
alter column code set not null;

alter table topic
add constraint topic_unique_code UNIQUE (bot_id,code);