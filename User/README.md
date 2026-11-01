# Java Chat

A simple real-time chat built with plain Java, MySQL and WebSocket,
made to learn how web apps work without a framework.

## Features
- Registration and login (BCrypt password hashing)
- Session cookies (HttpOnly, SameSite=Strict)
- One-to-one conversations saved in MySQL
- Live messages through WebSocket
- Messages sent with fetch (no page reload)
- Online status (live green/grey dots)
- JUnit tests (run with 'DB_NAME=chat_test mvn test'; needs a 'chat_test' database)

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

## Running tests

The tests use a **separate database** called `chat_test`, so they never touch your real data. The test classes delete all rows before each test, so they refuse to run unless `DB_NAME=chat_test`.

### 1. Create the test database

```bash
sudo mysql -e "CREATE DATABASE IF NOT EXISTS chat_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
sudo mysql -e "GRANT ALL PRIVILEGES ON chat_test.* TO 'chatuser'@'localhost'; FLUSH PRIVILEGES;"
```

Then create the tables inside it. Edit a copy of `schema.sql` so it uses `chat_test`, or run:

```bash
sed -e 's/CREATE DATABASE IF NOT EXISTS chat/CREATE DATABASE IF NOT EXISTS chat_test/' \
    -e 's/^USE chat;/USE chat_test;/' schema.sql | sudo mysql
```

Check that it worked:

```bash
sudo mysql -e "USE chat_test; SHOW TABLES;"
```

You should see `users` and `messages`.

### 2. Set the environment variables

```bash
export DB_USER=chatuser
export DB_PASS=your_password
export DB_NAME=chat_test
```

### 3. Run the tests

```bash
mvn test
```

Expected result:

```
Tests run: ..., Failures: 0, Errors: 0
BUILD SUCCESS
```

In IntelliJ, set the same three variables in **Run → Edit Configurations → Edit configuration templates → JUnit**, then use the green arrow next to a test class.

### What is tested

- `UserServiceTest`: register, duplicate names, login with correct and wrong passwords
- `MessageServiceTest`: sending, conversation order, privacy between users, validation
- `UserTest`: validation and `equals`/`hashCode` (no database needed)

### Troubleshooting

| Problem | Cause |
|---|---|
| `Access denied ... (using password: NO)` | `DB_USER` / `DB_PASS` are not exported in this terminal |
| `Access denied ... to database 'chat_test'` | the `GRANT` was not run, or the database name is wrong |
| `Tests must run with DB_NAME=chat_test` | the safety guard stopped the test; export `DB_NAME` |
| `Unknown host repo.maven.apache.org` | no internet; Maven needs to download dependencies once |

## Ports
- 8081: HTTP
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