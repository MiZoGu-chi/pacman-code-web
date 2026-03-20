CREATE DATABASE IF NOT EXISTS pacman_db;

USE pacman_db;

CREATE TABLE
    IF NOT EXISTS users (
        id INT AUTO_INCREMENT PRIMARY KEY,
        username VARCHAR(50) NOT NULL UNIQUE,
        email VARCHAR(100) NOT NULL UNIQUE,
        password VARCHAR(255) NOT NULL,
        registration_date DATETIME DEFAULT CURRENT_TIMESTAMP
    );	
    	
CREATE TABLE
    IF NOT EXISTS scores (
        id INT AUTO_INCREMENT PRIMARY KEY,
        id_user INT NOT NULL,
        score INT NOT NULL,
        achieved_date DATETIME DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT fk_user FOREIGN KEY (id_user) REFERENCES users (id) ON DELETE CASCADE
    );

    
INSERT INTO
    users (username, email, password)
VALUES
    ('Test', 'test@gmail.com', "1234");		
    
INSERT INTO
    scores (id_user, score)
VALUES
    (1, 5000);
