alter table response_button
drop constraint FK_A_RESPONSE_BUTTON_SMALL_TALK_RESPONSE_SMALL_TALK;

alter table response_button
add column top_id_response numeric;

update response_button res
set top_id_response = top.top_id
from topic top
join small_talk smt on (smt.top_id = top.top_id)
where res.smt_id_response = smt.smt_id;

alter table response_button
alter column top_id_response set not null,
drop column smt_id_response;

comment on column RESPONSE_BUTTON.TOP_ID_RESPONSE is
'TopicResponse';

alter table RESPONSE_BUTTON
	add constraint FK_A_RESPONSE_BUTTON_TOPIC_RESPONSE_TOPIC foreign key (TOP_ID_RESPONSE)
	references TOPIC (TOP_ID);

create index A_RESPONSE_BUTTON_TOPIC_RESPONSE_TOPIC_FK on RESPONSE_BUTTON (TOP_ID_RESPONSE asc);

drop index if exists A_RESPONSE_BUTTON_SMALL_TALK_RESPONSE_SMALL_TALK_FK ;