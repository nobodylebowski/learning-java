import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class Main {
    public static void main(String[] args){
        UserService usrService = new UserService();
        User Delia = usrService.register("Delia", "password");
        User Katia = usrService.register("Katia", "password");
        User Aldo = usrService.register("Aldo", "password");

        MessageService msgService = new MessageService();

        msgService.send(Delia, Katia, "ciao katia");
        msgService.send(Katia, Delia, "Ciao Delia");
        msgService.send(Katia, Aldo, "Delia puzza");
        msgService.send(Aldo, Katia, "Lavati tu!");

        System.out.println(msgService.getConversation(Aldo, Katia));

    }
}

