-- V4__update_foreign_key_constraint.sql
ALTER TABLE password_reset_token
DROP FOREIGN KEY FK83nsrttkwkb6ym0anu051mtxn;
-- Supprimer l'ancienne contrainte

ALTER TABLE password_reset_token
ADD CONSTRAINT FK83nsrttkwkb6ym0anu051mtxn FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;
-- Permet la suppression en cascade