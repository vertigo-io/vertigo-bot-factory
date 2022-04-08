UPDATE KIND_TOPIC set label = 'Start topic',
                      label_fr = 'Intention d''accueil',
                      description = 'Start topic describes bot behavior when starting a new conversation.',
                      description_fr = 'L''intention d''accueil décrit le comportement du bot à l''ouverture de la conversation avec un utilisateur.'
                WHERE KTO_CD = 'START';

UPDATE KIND_TOPIC set label = 'End topic',
                      label_fr = 'Intention de clôture',
                      description = 'End topic describes bot behavior when a conversation is ending. It can be followed by an evaluation question.',
                      description_fr = 'L''intention de clôture décrit le comportement du bot lorsque la conversation se termine. Cette intention peut être suivie ou non de la phase d''évaluation.'
                WHERE KTO_CD = 'END';

UPDATE KIND_TOPIC set label = 'Failure topic',
                      label_fr = 'Intention d''erreur',
                      description = 'Failure topic describes bot behavior when user question was not understood.',
                      description_fr = 'L''intention d''erreur décrit le comportement du bot dans le cas où celui-ci n''a pas compris la question de l''utilisateur.'
                WHERE KTO_CD = 'FAILURE';

UPDATE KIND_TOPIC set label_fr = 'Normal',
                      description_fr = 'Normal'
                WHERE KTO_CD = 'NORMAL';

UPDATE KIND_TOPIC set label_fr = 'Non atteignable',
                      description_fr = 'Non atteignable'
                WHERE KTO_CD = 'UNREACHABLE';

UPDATE KIND_TOPIC set label = 'Idle topic',
                      label_fr = 'Intention de reprise de conversation',
                      description = 'When bot answers a question but conversation isn''t over, the bot offers to ask another question.',
                      description_fr = 'Dans le cas où le bot répond à la question d''un utilisateur mais que la conversation n''est pas terminée, alors celui-ci relance la conversation en donnant à l''utilisateur l''opportunité de reposer une question ou en lui faisant une nouvelle proposition. C''est ce comportement qui est décrit dans l''intention de reprise de conversation.'
                WHERE KTO_CD = 'IDLE';