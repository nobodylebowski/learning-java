# Java Chat

A simple real-time chat built with plain Java, MySQL and WebSocket,
made to learn how web apps work without a framework.

## Features
- Registration and login (BCrypt password hashing)
- Session cookies (HttpOnly, SameSite=Strict)
- One-to-one conversations saved in MySQL
- Live messages through WebSocket
- Messages sent with fetch (no page reload)

## Requirements
- JDK 21
- Maven
- MySQL 8

## Setup

1. Create the database and tables:
   ```bash
   sudo mysql < schema.sql
   ```
2. Create a database user:
   ```sql
   CREATE USER 'chatuser'@'localhost' IDENTIFIED BY 'your_password';
   GRANT ALL PRIVILEGES ON chat.* TO 'chatuser'@'localhost';
   ```
3. Set the environment variables:
   ```bash
   export DB_USER=chatuser
   export DB_PASS=your_password
   ```
4. Run the server (`WebServer` class) from IntelliJ, or with Maven.
5. Open http://localhost:8080

## Ports
- 8080: HTTP
- 8887: WebSocket

## Project structure
- `WebServer`: HTTP handlers, sessions, pages
- `ChatSocketServer`: WebSocket connections and live push
- `UserService`, `MessageService`: application logic
- `UserRepository`, `MessageRepository`: SQL
- `SessionManager`: session tokens

## Notes
This is a learning project. Sessions are kept in memory, and the server
is not ready for production (no HTTPS, no rate limiting).