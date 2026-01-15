-- Ajout de la colonne jsm_mode dans chatbot_custom_config si elle n'existe pas déjà
ALTER TABLE chatbot_custom_config ADD COLUMN IF NOT EXISTS jsm_mode BOOLEAN NOT NULL DEFAULT FALSE;
