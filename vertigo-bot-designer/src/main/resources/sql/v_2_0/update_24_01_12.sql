CREATE OR REPLACE FUNCTION add_url_context_value() RETURNS void AS $$
DECLARE
  bot chatbot;
  cvaid NUMERIC;
  envid NUMERIC;
BEGIN
    FOR bot IN (select * from chatbot)
            LOOP
        select cva_id from context_value where bot_id = bot.bot_id and lower(label) = 'url' into cvaid;
        if not found then
            select nextval('SEQ_CONTEXT_VALUE') into cvaid;
            insert into context_value values (cvaid, 'url', '', bot.bot_id);
            for envid in (select cenv_id from context_environment where bot_id = bot.bot_id) loop
                insert into context_environment_value values (nextval('SEQ_CONTEXT_ENVIRONMENT_VALUE'), null, cvaid, envid);
            end loop;
        else
            update context_value set xpath = '' where cva_id = cvaid;
        end if;
    END LOOP;
END;
$$
LANGUAGE plpgsql;

SELECT add_url_context_value();