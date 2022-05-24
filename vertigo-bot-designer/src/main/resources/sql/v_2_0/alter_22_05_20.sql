insert into TYPE_EXPORT_ANALYTICS(TEA_CD, LABEL, LABEL_FR, BOT_RELATED) values ('RATING', 'Rating', 'Evaluation', 'true');
insert into TYPE_EXPORT_ANALYTICS(TEA_CD, LABEL, LABEL_FR, BOT_RELATED) values ('TOPIC_USAGE', 'Topic usage', 'Utilisation des intentions', 'true');
update TYPE_EXPORT_ANALYTICS set TEA_CD = 'USER_ACTIONS_CONVERSATIONS',
                                 LABEL = 'User actions and conversations',
                                 LABEL_FR = 'Actions utilisateur et conversations'
where TEA_CD = 'SESSIONS';
update TYPE_EXPORT_ANALYTICS set LABEL_FR = 'Intentions non reconnues' where TEA_CD = 'UNKNOWN_MESSAGES';