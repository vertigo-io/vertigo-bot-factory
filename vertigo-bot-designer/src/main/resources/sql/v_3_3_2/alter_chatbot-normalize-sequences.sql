-- ==========================================================================================
--   Script name         :	alter_chatbot-normalize-sequences.sql
--   Database name       :	chatbot
--   SGBD                :	PostgreSQL Version 16.4
--   Creation date       :	09/09/26
--   Application version : 3.3.2
--   Content             :	Rewrites FAQ category, question-answer and welcome-tour-step
--                          sequences as dense unique 1..N ranks, preserving relative order
--                          (current sequence, then id). Idempotent.
-- ==========================================================================================

UPDATE question_answer_category category
SET sequence = ranked.rn
FROM (
	SELECT qa_cat_id,
		ROW_NUMBER() OVER (PARTITION BY bot_id ORDER BY sequence, qa_cat_id) AS rn
	FROM question_answer_category
) ranked
WHERE category.qa_cat_id = ranked.qa_cat_id
	AND category.sequence IS DISTINCT FROM ranked.rn;

UPDATE question_answer qa
SET sequence = ranked.rn
FROM (
	SELECT qa_id,
		ROW_NUMBER() OVER (PARTITION BY qa_cat_id ORDER BY sequence, qa_id) AS rn
	FROM question_answer
) ranked
WHERE qa.qa_id = ranked.qa_id
	AND qa.sequence IS DISTINCT FROM ranked.rn;

UPDATE welcome_tour_step step
SET sequence = ranked.rn
FROM (
	SELECT wel_step_id,
		ROW_NUMBER() OVER (PARTITION BY tour_id ORDER BY sequence, wel_step_id) AS rn
	FROM welcome_tour_step
) ranked
WHERE step.wel_step_id = ranked.wel_step_id
	AND step.sequence IS DISTINCT FROM ranked.rn;
