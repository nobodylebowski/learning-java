import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class UserService {
    private final UserRepository repository = new UserRepository();
    private final int MINIMUM_PASSWORD_LENGTH = 8;
    public User register(String name, String password) {
        if (name == null || password == null) {
            throw new IllegalArgumentException("Insert name and password for registration");
        }
        if (password.length() < MINIMUM_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Password must have at least 8 character");
        }
        try {
            if (repository.findByName(name) != null) {
                throw new IllegalArgumentException(name + " is already registered");
            }
            String hash = BCrypt.hashpw(password, BCrypt.gensalt());
            return repository.insert(name, hash);
        } catch (SQLException e) {
            throw new RuntimeException("Database error during registration: " + e);
        }
    }

    public User login(String name, String password) {
        try {
            User user = repository.findByName(name);

            if (user == null || !user.checkPassword(password)) {
                throw new IllegalArgumentException("Username or password are incorrect");
            }
            return user;
        } catch (SQLException e) {
            throw new RuntimeException("Database error during login: " + e);
        }
    }
}
