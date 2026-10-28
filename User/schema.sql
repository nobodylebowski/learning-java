CREATE DATABASE IF NOT EXIST chat
       CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
       USE chat;

           CREATE TABLE IF NOT EXIST users (
               id INT AUTO_INCREMENT PRIMARY KEY,
               name VARCHAR(30) UNIQUE NOT NULL,
               password_hash VARCHAR(255) NOY NULL
           );

           CREATE TABLE IF NOT EXIST messages (
               id INT AUTO_INCREMENT PRIMARY KEY,
               sender_id INT NOT NULL,
               receiver_id INT NOT NULL,
               content TEXT NOT NULL,
               sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
               FOREIGN KEY (sender_id) REFERENCES users(id),
               FOREIGN KEY (receiver_id) REFERENCES user(id)
           );

CREATE INDEX idx_messages_pair ON messages(sender_id, receiver_id, sent_at);
