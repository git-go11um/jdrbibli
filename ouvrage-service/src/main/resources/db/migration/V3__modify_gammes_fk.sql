ALTER TABLE gammes DROP FOREIGN KEY fk_owner_id;

ALTER TABLE gammes
ADD CONSTRAINT fk_owner_id
FOREIGN KEY (owner_id) REFERENCES user_db.user_profiles(id) ON DELETE CASCADE;
