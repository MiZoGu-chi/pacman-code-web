CREATE DATABASE IF NOT EXISTS pacman_db;

USE pacman_db;

CREATE TABLE
    IF NOT EXISTS scores (
        id INT AUTO_INCREMENT PRIMARY KEY,
        id_user INT NOT NULL,
        score INT NOT NULL,
        date_atteinte DATETIME DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (id_utilisateur) REFERENCES utilisateurs (id)
    );

CREATE TABLE
    IF NOT EXISTS users (
        id INT AUTO_INCREMENT PRIMARY KEY,
        pseudo VARCHAR(50) NOT NULL UNIQUE,
        email VARCHAR(100) NOT NULL UNIQUE,
        mot_de_passe VARCHAR(255) NOT NULL,
        date_inscription DATETIME DEFAULT CURRENT_TIMESTAMP
    );

INSERT INTO
    scores (pseudo, score)
VALUES
    ('Test', 5000);

INSERT INTO
    scores (pseudo, email, mot_de_passe)
VALUES
    ('Test', 'test@gmail.com', "1234");