import java.util.HashMap;
import java.util.Map;

public class UserService {
    private final Map<String, User> users = new HashMap<>();

    public User register(String name, String password) {
        if (name == null || password == null) {
            throw new IllegalArgumentException("Insert name and password for registration");
        }
        if (users.containsKey(name)){
            throw new IllegalArgumentException(name + " is already registered");
        }
        User newUser = new User(name, password);
        users.put(name, newUser);
        return newUser;
    }

    public User login(String name, String password) {
        User user = users.get(name);
        if (user == null || ! user.checkPassword(password)) {
            throw new IllegalArgumentException("Username or password are incorrect");
        }
        return user;
    }
}
