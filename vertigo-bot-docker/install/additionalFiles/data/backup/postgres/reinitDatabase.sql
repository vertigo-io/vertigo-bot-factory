SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname = 'chatbot';

DROP DATABASE chatbot;

CREATE DATABASE chatbot WITH OWNER = chatbot;