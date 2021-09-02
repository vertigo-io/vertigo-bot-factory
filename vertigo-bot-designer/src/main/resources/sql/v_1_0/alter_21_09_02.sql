-- Chatbot 265 - corrections

update topic set tto_cd = 'SMALLTALK'
where top_id in (select top.top_id from topic top 
inner join small_talk smt on smt.top_id = top.top_id
where top.tto_cd = 'SCRIPTINTENTION');	
