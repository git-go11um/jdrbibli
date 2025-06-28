CREATE TABLE gammes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT
);

CREATE TABLE ouvrages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    image_url VARCHAR(255),
    gamme_id BIGINT,
    FOREIGN KEY (gamme_id) REFERENCES gammes(id)
);

CREATE TABLE user_ludotheque (
    user_id BIGINT NOT NULL,
    ouvrage_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, ouvrage_id),
    FOREIGN KEY (user_id) REFERENCES user_profiles(id),
    FOREIGN KEY (ouvrage_id) REFERENCES ouvrages(id)
);
