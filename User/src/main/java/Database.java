import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/"
            + System.getenv().getOrDefault("DB_NAME", "chat");

    public static Connection connect() throws SQLException{
        String user = System.getenv("DB_USER");
        String pass = System.getenv("DB_PASS");
        if (user == null || pass == null) {
            throw new IllegalStateException("Set DB_USER and DB_PASS environment variables");
        }
        return DriverManager.getConnection(URL, user, pass);
    }
}
