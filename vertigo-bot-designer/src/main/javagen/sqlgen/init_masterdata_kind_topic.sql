-- ============================================================
--   Insert MasterData values : KIND_TOPIC                                        
-- ============================================================
insert into KIND_TOPIC(KTO_CD, LABEL, TITLE_ENGLISH, TITLE_FRENCH, DESCRIPTION_ENGLISH, DESCRIPTION_FRENCH, DEFAULT_ENGLISH, DEFAULT_FRENCH) values ('START', 'Start', 'Start', 'Début', 'Default start response', 'Réponse de début par défaut', 'Hello !', 'Bonjour !');
insert into KIND_TOPIC(KTO_CD, LABEL, TITLE_ENGLISH, TITLE_FRENCH, DESCRIPTION_ENGLISH, DESCRIPTION_FRENCH, DEFAULT_ENGLISH, DEFAULT_FRENCH) values ('END', 'End', 'End', 'Fin', 'Default end response', 'Réponse de fin par défaut', 'Bye !', 'Au revoir !');
insert into KIND_TOPIC(KTO_CD, LABEL, TITLE_ENGLISH, TITLE_FRENCH, DESCRIPTION_ENGLISH, DESCRIPTION_FRENCH, DEFAULT_ENGLISH, DEFAULT_FRENCH) values ('FAILURE', 'Failure', 'Failure', 'Echec', 'Default failure response', 'Réponse de d'échec par défaut', 'Sorry, I don't understand.', 'Désolé, je n'ai pas compris.');
insert into KIND_TOPIC(KTO_CD, LABEL, TITLE_ENGLISH, TITLE_FRENCH, DESCRIPTION_ENGLISH, DESCRIPTION_FRENCH, DEFAULT_ENGLISH, DEFAULT_FRENCH) values ('NORMAL', 'Normal', 'Normal', 'Normal', 'null', 'null', 'Text', 'Texte');
