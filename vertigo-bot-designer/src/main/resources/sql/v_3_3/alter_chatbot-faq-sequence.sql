ALTER TABLE question_answer_category ADD COLUMN sequence NUMERIC;
ALTER TABLE question_answer ADD COLUMN sequence NUMERIC;

-- Initialisation basee sur l'ordre alphabetique actuel (label pour les categories, question pour les Q/R)
UPDATE question_answer_category c1 SET sequence = (
  SELECT COUNT(*) FROM question_answer_category c2
  WHERE c2.bot_id = c1.bot_id AND c2.label <= c1.label
);
UPDATE question_answer qa1 SET sequence = (
  SELECT COUNT(*) FROM question_answer qa2
  WHERE qa2.qa_cat_id = qa1.qa_cat_id AND qa2.question <= qa1.question
);

ALTER TABLE question_answer_category ALTER COLUMN sequence SET NOT NULL;
ALTER TABLE question_answer ALTER COLUMN sequence SET NOT NULL;
