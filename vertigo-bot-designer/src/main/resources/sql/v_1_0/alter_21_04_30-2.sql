alter table topic 
add column code numeric;

with rownumbers as (
	select top.top_id,
		row_number() over() as code
		from topic top
		order by top_id
)

update topic 
set code = rownumbers.code
from rownumbers 
where rownumbers.top_id = topic.top_id;

