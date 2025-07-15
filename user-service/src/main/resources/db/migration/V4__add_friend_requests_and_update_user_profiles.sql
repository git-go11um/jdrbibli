-- V4__add_friend_requests_and_update_user_profiles.sql

-- 1. Mise à jour table user_profiles

ALTER TABLE user_profiles
ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

ALTER TABLE user_profiles
ADD CONSTRAINT IF NOT EXISTS unique_pseudo UNIQUE (pseudo),
ADD CONSTRAINT IF NOT EXISTS unique_email UNIQUE (email);

-- 2. Création table friend_requests

CREATE TABLE IF NOT EXISTS friend_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    status ENUM(
        'PENDING',
        'ACCEPTED',
        'REJECTED'
    ) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_sender FOREIGN KEY (sender_id) REFERENCES user_profiles (id) ON DELETE CASCADE,
    CONSTRAINT fk_receiver FOREIGN KEY (receiver_id) REFERENCES user_profiles (id) ON DELETE CASCADE,
    CONSTRAINT uc_sender_receiver UNIQUE (sender_id, receiver_id)
);