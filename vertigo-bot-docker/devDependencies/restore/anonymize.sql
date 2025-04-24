UPDATE chatbot_node SET url = 'http://localhost:8080/vertigo-bot-executor';
UPDATE chatbot_node SET api_key = 'MyNodeApiKey!';
UPDATE person SET login = REGEXP_REPLACE (login, '(?<=\@).*', 'mail.com');

-- Paramétrage des connecteurs Confluence et Jira
UPDATE confluence_setting
   SET url = 'https://confluence.rct01.kleegroup.com/display/CHATBOTPOC/POC+Chatbot+Home',
       login = 'achatbot',
       password = 'x!Yw73=i5M_eM7';

UPDATE jira_setting
   SET url = 'https://jirasoft.rct01.kleegroup.com/projects/CHATBOTPOC/issues/?filter=allopenissues',
       login = 'achatbot',
       password = 'x!Yw73=i5M_eM7';