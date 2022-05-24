ALTER TABLE topic ALTER COLUMN DESCRIPTION TYPE TEXT;

UPDATE topic set title = 'Intention d''accueil',
                 description = 'L''intention d''accueil décrit le comportement du bot à l''ouverture de la conversation avec un utilisateur'
                WHERE code = 'START';

UPDATE topic set title = 'Intention d''erreur',
                 description = 'L''intention d''erreur décrit le comportement du bot dans le cas où celui-ci n''a pas compris la question de l''utilisateur'
                WHERE code = 'FAILURE';

UPDATE topic set title = 'Intention de reprise de conversation',
                 description = 'Dans le cas où le bot répond à la question d''un utilisateur mais que la conversation n''est pas terminée, alors celui-ci relance la conversation en donnant à l''utilisateur l''opportunité de reposer une question ou en lui faisant une nouvelle proposition. C''est ce comportement qui est décrit dans l''intention de reprise de conversation'
                WHERE code = 'IDLE';

UPDATE topic set title = 'Intention de clôture',
                 description = 'L''intention de clôture décrit le comportement du bot lorsque la conversation se termine.
                                Cette intention peut être suivie ou non de la phase d''évaluation.'
                WHERE code = 'END';

UPDATE topic_category set code = 'DEFAULT', label = 'Intentions par défaut' WHERE is_technical = true;
