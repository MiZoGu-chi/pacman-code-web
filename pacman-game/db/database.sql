CREATE DATABASE IF NOT EXISTS pacman_db;
USE pacman_db;


CREATE TABLE IF NOT EXISTS high_scores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    pseudo VARCHAR(50) NOT NULL,
    score INT NOT NULL,
    date_atteinte DATETIME DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO high_scores (pseudo, score) VALUES ('PacMaster', 5000), ('GhostHunter', 3500);