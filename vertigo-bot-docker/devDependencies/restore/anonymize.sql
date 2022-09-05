UPDATE chatbot_node SET url = 'http://localhost:8080/vertigo-bot-executor';
UPDATE chatbot_node SET api_key = 'MyNodeApiKey!';
UPDATE person SET login = REGEXP_REPLACE (login, '(?<=\@).*', 'mail.com');