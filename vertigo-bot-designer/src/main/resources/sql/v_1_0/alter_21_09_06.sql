-- Chatbot 242 - Ajout d'un message d'avertissement indiquant que le modèle n'a pas été entrainé depuis les dernières modifications

ALTER TABLE chatbot_node
ADD column IS_UP_TO_DATE bool;

UPDATE chatbot_node SET IS_UP_TO_DATE = false;

alter table chatbot_node
alter column IS_UP_TO_DATE set not null;
