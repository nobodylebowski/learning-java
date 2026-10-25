import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class Main {
    public static void main(String[] args){
        UserService usrService = new UserService();
        User katia = usrService.login("Katia", "hash");
        User giorgio = usrService.login("Giorgio", "hash");
        User sergio = usrService.login("Segio", "hash");

        MessageService service = new MessageService();
        service.send(giorgio, katia, "Hello K");
        service.send(katia, giorgio, "Hello G");
        service.send(sergio, katia, "Hi K");
        service.send(katia, sergio, "Hi S");

        System.out.println(service.getConversation(katia, giorgio));
        System.out.println(service.getConversation(sergio, giorgio));


    }
}

