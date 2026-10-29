import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class WebServer {


    public static void main(String[] args) throws Exception{
        HttpServer server = HttpServer.create(new InetSocketAddress(Config.HTTP_PORT), 0);
        UserService userService = new UserService();
        MessageService messageService = new MessageService();
        SessionManager session = new SessionManager();
        ChatSocketServer socketServer = new ChatSocketServer(Config.WS_PORT, session);
        new AuthHandlers(userService, session).register(server);
        new ChatHandlers(userService, messageService, session, socketServer).register(server);
        new StaticHandlers().register(server);

        socketServer.start();
        server.start();
        System.out.println("Server on " + Config.ALLOWED_ORIGIN);
    }
}
