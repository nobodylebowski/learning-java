import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        User tom = new User("Tom", "psw1");
        User sara = new User("Sara", "psw2");
        User mark = new User("Mark", "x");

        Message msg1 = new Message(tom, sara, "Hello Sara");
        Message msg2 = new Message(sara, mark, "hello, how are you?");
        Message msg3 = new Message(tom, sara, "All fine thanks, and you?");
        Message msg4 = new Message(sara, tom, "not bad");
        Message msg5 = new Message(mark, sara, "Hi baby");

        ArrayList<Message> messageList = new ArrayList<>();
        messageList.add(msg1);
        messageList.add(msg2);
        messageList.add(msg3);
        messageList.add(msg4);
        messageList.add(msg5);

        printConversation(messageList, tom, mark);

    }

    public static void printConversation(List<Message> messageList, User a, User b) {

        if (messageList == null || a == null || b == null) {
            throw new IllegalArgumentException("The parameters cannot be null");
        }

        boolean gotMessage = false;

        for (Message message : messageList) {
            if (isBetween(message, a, b)) {
                gotMessage = true;
                System.out.println(message.getSender().getName() + " : " + message.getContent());
            }

        }
        if(!gotMessage){
            System.out.println("No messages yet");
        }
    }

    private static boolean isBetween(Message msg, User a, User b) {
        User sender = msg.getSender();
        User receiver = msg.getReceiver();

        return (sender.equals(a) && receiver.equals(b) || sender.equals(b) && receiver.equals(a));
    }
}

