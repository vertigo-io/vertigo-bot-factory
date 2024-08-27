ALTER TABLE jira_setting ADD COLUMN NUMBER_OF_RESULTS NUMERIC DEFAULT 4;
comment on column jira_setting.NUMBER_OF_RESULTS is 'Number max of results';

