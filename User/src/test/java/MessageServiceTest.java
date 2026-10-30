import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MessageServiceTest {

    private final UserService userService = new UserService();
    private final MessageService messageService = new MessageService();

    private User tom;
    private User sara;
    private User mark;

    @BeforeEach
    void cleanDatabase() throws SQLException {
        try (Connection c = Database.connect();
        Statement st = c.createStatement() ) {
            st.executeUpdate("DELETE FROM messages");
            st.executeUpdate("DELETE FROM users");
        }
        tom = userService.register("Tom", "password123");
        sara = userService.register("Sara", "password456");
        mark = userService.register("Mark", "password789");
    }

    @Test
    void sendMessageWithId() {
        Message m = messageService.send(tom, sara, "Hello");
        assertTrue(m.getId() > 0);
        assertEquals("Hello", m.getContent());
        assertEquals(tom, m.getSender());
        assertEquals(sara, m.getReceiver());
    }

    @Test
    void conversationContainsBothDirectionInOrder() {
        messageService.send(tom, sara, "one");
        messageService.send(sara, tom, "two");
        messageService.send(tom, sara, "three");

        List<Message> conversation = messageService.getConversation(tom,sara);
        assertEquals(3, conversation.size());
        assertEquals("one", conversation.get(0).getContent());
        assertEquals("two", conversation.get(1).getContent());
        assertEquals("three", conversation.get(2).getContent());
    }

    @Test
    void conversationExcludeOtherUsers() {
        messageService.send(tom, sara, "private");
        messageService.send(mark, tom, "Hi Tom");

        List<Message> conversation = messageService.getConversation(tom, sara);
        assertEquals(1, conversation.size());
        assertEquals("private", conversation.get(0).getContent());
    }

    @Test
    void swappedArgumentsSameResult() {
        messageService.send(tom, sara, "one");
        messageService.send(sara,tom, "two");

        List<Message> forward = messageService.getConversation(tom, sara);
        List<Message> backward = messageService.getConversation(sara, tom);

        assertEquals(forward.size(), backward.size());
        for (int i = 0; i < forward.size(); i++) {
            assertEquals(forward.get(i).getId(), backward.get(i).getId());
        }
    }

    @Test
    void blankArgumentIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> messageService.send(tom, sara, ""));
    }

    @Test
    void nullSenderIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> messageService.send(null, sara, "Content"));
    }

    @Test
    void emptyConversationReturnEmptyList() {
        List<Message> conversation = messageService.getConversation(tom ,mark);
        assertNotNull(conversation);
        assertTrue(conversation.isEmpty());
    }

    @Test
    void getConversationRejectsNullUser() {
        assertThrows(IllegalArgumentException.class,
                () -> messageService.getConversation(null, mark));
    }
}
