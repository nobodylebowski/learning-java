import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessageRepository {
    private final UserRepository userRepo = new UserRepository();

    public Message insert(User sender, User receiver, String content) throws SQLException {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Message content cannot be empty");
        }

        String sql = "INSERT INTO messages (sender_id, receiver_id, content, sent_at) VALUES (?, ?, ?, ?)";
        LocalDateTime now = LocalDateTime.now();
        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, sender.getId());
            ps.setInt(2, receiver.getId());
            ps.setString(3, content);
            ps.setTimestamp(4, Timestamp.valueOf(now));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return new Message(keys.getInt(1), sender, receiver, content);
            }
        }
    }

    public List<Message> findMessageBetween(User a, User b) throws SQLException{
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT id, sender_id, receiver_id, content, sent_at FROM messages " +
                "WHERE (sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?) " +
                "ORDER BY sent_at, id";
        try(Connection c = Database.connect();
        PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, a.getId());
            ps.setInt(2, b.getId());
            ps.setInt(3, b.getId());
            ps.setInt(4, a.getId());
            try(ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    int senderId = rs.getInt("sender_id");
                    User sender = (senderId == a.getId()) ? a : b;
                    User receiver = (sender == a) ? b : a;
                    messages.add(new Message(rs.getInt("id"), sender,
                            receiver, rs.getString("content"),
                            rs.getTimestamp("sent_at").toLocalDateTime()));
                }
                return messages;
            }
        }

    }
}
