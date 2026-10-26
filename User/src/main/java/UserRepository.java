import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    public User insert(String name, String pswHash) throws SQLException {
        String sql = "INSERT INTO users (name, password_hash) VALUES (?, ?)";
        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, pswHash);
            ps.executeUpdate();
            try(ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return new User(keys.getInt(1), name, pswHash);
            }
        }
    }

    public User findByName(String name) throws SQLException {
            String sql = "SELECT id, name, password_hash FROM users WHERE name=?";
            try (Connection c= Database.connect();
            PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, name);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new User(rs.getInt("id"), rs.getString("name"), rs.getString("password_hash"));
                    } else {
                        return null;
                    }
                }
            }
    }

    public User findById(int id) throws SQLException {
        String sql = "SELECT id, name, password_hash FROM users WHERE id=?";
        try (Connection c= Database.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("id"), rs.getString("name"), rs.getString("password_hash"));
                } else {
                    return null;
                }
            }
        }
    }

    public List<User> findAllExcept(int id) throws SQLException {
        String sql = "SELECT id, name, password_hash FROM users WHERE id <> ? ORDER BY name";
        List<User> users = new ArrayList<>();
        try (Connection c = Database.connect();
        PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()){
                while(rs.next()) {
                    users.add(new User(rs.getInt("id"), rs.getString("name"), rs.getString("password_hash")));
                }

            }

        }
        return users;
    }
}
