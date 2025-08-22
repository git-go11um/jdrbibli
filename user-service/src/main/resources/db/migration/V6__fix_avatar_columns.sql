-- Correction du nom de table : ajout des colonnes avatar_url et avatar_path
ALTER TABLE user_profiles
ADD COLUMN IF NOT EXISTS avatar_url VARCHAR(255),
ADD COLUMN IF NOT EXISTS avatar_path VARCHAR(500);
