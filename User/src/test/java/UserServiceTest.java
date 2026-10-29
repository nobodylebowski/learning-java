import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    private final UserService service = new UserService();

    @BeforeEach
    void cleanDatabase() throws SQLException {
        try (Connection c = Database.connect();
        Statement st = c.createStatement()) {
            st.executeUpdate("DELETE FROM messages");
            st.executeUpdate("DELETE FROM users");
        }
    }

    @Test
    void registerCreateUserWithId() {
        User u = service.register("Tom", "password123");
        assertEquals("Tom", u.getName());
        assertTrue(u.getId() > 0);
    }

    @Test
    void registerRejectsDuplicateName() {
        service.register("Tom", "password123");
        assertThrows(IllegalArgumentException.class,
                () -> service.register("Tom", "other123"));
    }

    @Test
    void loginWorksWithCorrectPassword() {
        service.register("Tom", "password123");
        assertEquals("Tom", service.login("Tom", "password123").getName());
    }

    @Test
    void loginFailsWithWrongPassword() {
        service.register("Tom", "password123");
        assertThrows(IllegalArgumentException.class,
                () -> service.login("Tom", "wrong"));
    }

    @Test
    void loginFailsForUnknowUser() {
        assertThrows(IllegalArgumentException.class,
                () -> service.login("Unknow", "password123"));
    }
}
