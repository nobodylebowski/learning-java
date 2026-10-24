import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class UserService {
    private final UserRepository repository = new UserRepository();

    public User register(String name, String password) {
        if (name == null || password == null) {
            throw new IllegalArgumentException("Insert name and password for registration");
        }
        try {
            if (repository.findByName(name) != null) {
                throw new IllegalArgumentException(name + " is already registered");
            }
            return repository.insert(name, password);
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
