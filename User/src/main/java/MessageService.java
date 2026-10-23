import java.util.*;

public class MessageService {
    private final List<Message> messages = new ArrayList<>();

    public Message send(User sender, User receiver, String content) {
        Message msg =  new Message(sender, receiver, content);
        messages.add(msg);
        return msg;
    }

    public List<Message> getConversation(User a, User b){
        if (a == null || b == null ) {
            throw new IllegalArgumentException("Users cannot be null");
        }
        List<Message> conversation =  new ArrayList<>();
        for( Message message : messages ) {
            if (isBetween(message, a, b)) {
                conversation.add(message);
            }
        }
        return conversation;
    }

    private static boolean isBetween(Message msg, User a, User b) {
        User sender = msg.getSender();
        User receiver = msg.getReceiver();

        return ((sender.equals(a) && receiver.equals(b)) || (sender.equals(b) && receiver.equals(a)));
    }
}
