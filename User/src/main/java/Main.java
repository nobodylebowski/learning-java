import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class Main {
    public static void main(String[] args){
        UserService usrService = new UserService();
        User katia = usrService.register("Delia", "password");

        System.out.println(usrService.login("Delia", "password"));

        usrService.login("Delia", "worng");


    }
}

