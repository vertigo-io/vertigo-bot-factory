alter table response_type
add column label_fr varchar(100);

update response_type
set label_fr = 'Texte'
where rty_id = 'RICH_TEXT';

update response_type
set label_fr = 'Texte aléatoire'
where rty_id = 'RANDOM_TEXT';

alter table response_type
alter column label_fr set not null;

alter table type_topic
add column label_fr varchar(100);

update type_topic
set label_fr = 'Small talk'
where tto_cd = 'SMALLTALK';

update type_topic
set label_fr = 'Intention scriptée'
where tto_cd = 'SCRIPTINTENTION';

alter table type_topic
alter column label_fr set not null;
