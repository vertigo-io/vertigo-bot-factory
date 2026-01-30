BEGIN;
-- Add QUESTION_ANSWERS export type
insert into TYPE_EXPORT_ANALYTICS(TEA_CD, LABEL, LABEL_FR) 
values ('QUESTION_ANSWERS', 'Questions & Answers', 'Questions / Réponses');
COMMIT;
