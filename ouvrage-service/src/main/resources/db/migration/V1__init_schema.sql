CREATE TABLE gammes (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nom VARCHAR(255) NOT NULL,
  description VARCHAR(255)
);

CREATE TABLE ouvrages (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  titre VARCHAR(255) NOT NULL,
  description TEXT,
  version VARCHAR(50),
  type_ouvrage VARCHAR(100),
  date_publication DATE,
  langue VARCHAR(100),
  editeur VARCHAR(255),
  etat VARCHAR(100),
  isbn VARCHAR(50),
  ouvrage_lie VARCHAR(255),
  scenario_lie VARCHAR(255),
  pret BOOLEAN,
  errata TEXT,
  notes TEXT,
  gamme_id BIGINT,
  FOREIGN KEY (gamme_id) REFERENCES gammes(id)
);
