/* -- V7__refactor_user_tables.sql
-- Nettoyage et refonte de la gestion des utilisateurs

-- 1. Supprimer les tables qui dépendent de user_profiles (si elles existent)
DROP TABLE IF EXISTS user_friends;
DROP TABLE IF EXISTS friend_requests;
DROP TABLE IF EXISTS user_ludotheque;
DROP TABLE IF EXISTS ouvrages;
DROP TABLE IF EXISTS gammes;

-- 2. Supprimer l'ancienne table user_profiles
DROP TABLE IF EXISTS user_profiles;

-- 3. Créer la nouvelle table users (alignée avec auth-service)
CREATE TABLE users (
    id BIGINT PRIMARY KEY,  -- On garde le même id que dans auth-service
    pseudo VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    avatar_url VARCHAR(255),
    avatar_path VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 4. Recréer la table gammes
CREATE TABLE gammes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT
);

-- 5. Recréer la table ouvrages
CREATE TABLE ouvrages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    image_url VARCHAR(255),
    gamme_id BIGINT,
    FOREIGN KEY (gamme_id) REFERENCES gammes(id) ON DELETE CASCADE
);

-- 6. Recréer la table user_ludotheque (avec users au lieu de user_profiles)
CREATE TABLE user_ludotheque (
    user_id BIGINT NOT NULL,
    ouvrage_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, ouvrage_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (ouvrage_id) REFERENCES ouvrages(id) ON DELETE CASCADE
);

-- 7. Recréer la table friend_requests (avec users au lieu de user_profiles)
CREATE TABLE friend_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    status ENUM('PENDING', 'ACCEPTED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_sender FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_receiver FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uc_sender_receiver UNIQUE (sender_id, receiver_id)
);

-- 8. (optionnel) Recréer la table user_friends si tu veux garder une liste d’amis validés
CREATE TABLE user_friends (
    user_id BIGINT NOT NULL,
    friend_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES users(id) ON DELETE CASCADE
);
 */