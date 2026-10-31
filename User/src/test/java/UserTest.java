import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    void nullNameRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new User(1, null, "hash"));
    }

    @Test
    void blankNameRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new User(1, "", "hash"));
    }

    @Test
    void checkPasswordAcceptsCorrectPassword() {
        String hash = BCrypt.hashpw("secret", BCrypt.gensalt());
        User u = new User(1, "Tom", hash);
        assertTrue(u.checkPassword("secret"));
    }

    @Test
    void checkPasswordRejectsWrongPassword() {
        String hash = BCrypt.hashpw("secret", BCrypt.gensalt());
        User u = new User(1,"Tom", hash);
        assertFalse(u.checkPassword("wrong"));
    }

    @Test
    void userWithSameIdAreEquals() {
        User a = new User(1, "Tom", "a");
        User b = new User(1, "Tom", "b");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }
}
