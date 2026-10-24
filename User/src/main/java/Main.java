import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class Main {
    public static void main(String[] args){

        try (Connection c=Database.connect()) {
            System.out.println("Connected: "+ !c.isClosed());
        } catch(SQLException e){
            System.out.println("Error: " + e.getMessage());
        }

        //        User tom = new User("Tom", "psw1");
//        User sara = new User("Sara", "psw2");
//        User mark = new User("Mark", "x");
//
//        Message msg1 = new Message(tom, sara, "Hello Sara");
//        Message msg2 = new Message(sara, mark, "hello, how are you?");
//        Message msg3 = new Message(tom, sara, "All fine thanks, and you?");
//        Message msg4 = new Message(sara, tom, "not bad");
//        Message msg5 = new Message(mark, sara, "Hi baby");
//
//        ArrayList<Message> messageList = new ArrayList<>();
//        messageList.add(msg1);
//        messageList.add(msg2);
//        messageList.add(msg3);
//        messageList.add(msg4);
//        messageList.add(msg5);
//
//        UserService users = new UserService();
//        MessageService msgService = new MessageService();
//
//        msgService.send(tom, sara, "Hello Sara");
//        msgService.send(sara, tom, "Hello Tom");
//        msgService.send(mark, sara, "Hi baby");
//
//        for (Message msg : msgService.getConversation(tom ,mark)){
//            System.out.println(msg);
//        }
//
//        User harrison = users.register("Harrison", "psw1");
//        User logged = users.login("Harrison", "psw1");
    }
}

