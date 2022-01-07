-- ============================================================
--   Insert MasterData values : KIND_TOPIC                                        
-- ============================================================
insert into KIND_TOPIC(KTO_CD, LABEL, DESCRIPTION, DEFAULT_TEXT) values ('START', 'Start', 'Default start response', 'Hello !');
insert into KIND_TOPIC(KTO_CD, LABEL, DESCRIPTION, DEFAULT_TEXT) values ('END', 'End', 'Default end response', 'Bye !');
insert into KIND_TOPIC(KTO_CD, LABEL, DESCRIPTION, DEFAULT_TEXT) values ('FAILURE', 'Failure', 'Default failure response', 'Sorry, I don't understand.');
insert into KIND_TOPIC(KTO_CD, LABEL, DESCRIPTION, DEFAULT_TEXT) values ('NORMAL', 'Normal', 'Normal', 'Text');
insert into KIND_TOPIC(KTO_CD, LABEL, DESCRIPTION, DEFAULT_TEXT) values ('UNREACHABLE', 'Unreachable', 'Unreachable', 'Text');
insert into KIND_TOPIC(KTO_CD, LABEL, DESCRIPTION, DEFAULT_TEXT) values ('IDLE', 'Idle', 'Idle', 'Something else ?');
