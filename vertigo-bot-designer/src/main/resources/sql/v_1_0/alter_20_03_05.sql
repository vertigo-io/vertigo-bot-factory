-- random text if already multiple responses
UPDATE SMALL_TALK
SET RTY_ID = 'RANDOM_TEXT'
WHERE SMT_ID in
  (SELECT SMT_ID
   FROM UTTER_TEXT
   GROUP BY SMT_ID
   HAVING count(SMT_ID) > 1
  );

-- update to new format for pause
UPDATE UTTER_TEXT
SET text = replace(text, '[pause]', '<hr />');