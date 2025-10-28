-- Correction de la suppression en cascade pour les relations d'amis
USE user_db;

-- Pour sender_id
ALTER TABLE friend_requests
DROP FOREIGN KEY FK518hfyae04s5qrsdab0ppsai;

ALTER TABLE friend_requests
ADD CONSTRAINT FK518hfyae04s5qrsdab0ppsai
FOREIGN KEY (sender_id) REFERENCES user_profiles(id)
ON DELETE CASCADE;

-- Pour receiver_id
ALTER TABLE friend_requests
DROP FOREIGN KEY FK_receiver_user_profiles; -- à adapter au vrai nom FK dans ta base
ALTER TABLE friend_requests
ADD CONSTRAINT FK_receiver_user_profiles
FOREIGN KEY (receiver_id) REFERENCES user_profiles(id)
ON DELETE CASCADE;