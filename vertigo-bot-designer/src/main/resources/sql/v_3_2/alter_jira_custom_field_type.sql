-- ============================================================
--   Migration script: Add JIRA_CUSTOM_FIELD_TYPE table and FK
-- ============================================================

-- Creation de la table JIRA_CUSTOM_FIELD_TYPE
CREATE TABLE JIRA_CUSTOM_FIELD_TYPE (
    JCF_TYPE_CD     VARCHAR(100)    PRIMARY KEY,
    LABEL           VARCHAR(100)    NOT NULL,
    LABEL_FR        VARCHAR(100)    NOT NULL
);

-- Insertion des valeurs de reference
INSERT INTO JIRA_CUSTOM_FIELD_TYPE(JCF_TYPE_CD, LABEL, LABEL_FR) VALUES ('STRING', 'String', 'Texte');
INSERT INTO JIRA_CUSTOM_FIELD_TYPE(JCF_TYPE_CD, LABEL, LABEL_FR) VALUES ('NUMBER', 'Number', 'Nombre');
INSERT INTO JIRA_CUSTOM_FIELD_TYPE(JCF_TYPE_CD, LABEL, LABEL_FR) VALUES ('ARRAY_LABELS', 'Array of labels', 'Liste de labels');
INSERT INTO JIRA_CUSTOM_FIELD_TYPE(JCF_TYPE_CD, LABEL, LABEL_FR) VALUES ('DATE', 'Date', 'Date');
INSERT INTO JIRA_CUSTOM_FIELD_TYPE(JCF_TYPE_CD, LABEL, LABEL_FR) VALUES ('DATETIME', 'Date and time', 'Date et heure');
INSERT INTO JIRA_CUSTOM_FIELD_TYPE(JCF_TYPE_CD, LABEL, LABEL_FR) VALUES ('SINGLE_OPTION', 'Single option', 'Option unique');
INSERT INTO JIRA_CUSTOM_FIELD_TYPE(JCF_TYPE_CD, LABEL, LABEL_FR) VALUES ('MULTIPLE_OPTION', 'Multiple options', 'Options multiples');
INSERT INTO JIRA_CUSTOM_FIELD_TYPE(JCF_TYPE_CD, LABEL, LABEL_FR) VALUES ('TREE_OPTION', 'Tree option', 'Option arborescente');
INSERT INTO JIRA_CUSTOM_FIELD_TYPE(JCF_TYPE_CD, LABEL, LABEL_FR) VALUES ('SINGLE_USER', 'Single user', 'Utilisateur unique');
INSERT INTO JIRA_CUSTOM_FIELD_TYPE(JCF_TYPE_CD, LABEL, LABEL_FR) VALUES ('MULTIPLE_USER', 'Multiple users', 'Utilisateurs multiples');

-- Ajout de la colonne avec valeur par defaut pour les donnees existantes
ALTER TABLE JIRA_CUSTOM_FIELD_SETTING 
    ADD COLUMN JCF_TYPE_CD VARCHAR(100) DEFAULT 'STRING' NOT NULL;

-- Ajout de la contrainte de cle etrangere
ALTER TABLE JIRA_CUSTOM_FIELD_SETTING 
    ADD CONSTRAINT FK_JIRA_CUSTOM_FIELD_TYPE 
    FOREIGN KEY (JCF_TYPE_CD) REFERENCES JIRA_CUSTOM_FIELD_TYPE(JCF_TYPE_CD);

-- Suppression de la valeur par defaut (le type doit etre explicite a la creation)
ALTER TABLE JIRA_CUSTOM_FIELD_SETTING 
    ALTER COLUMN JCF_TYPE_CD DROP DEFAULT;
