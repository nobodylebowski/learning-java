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
        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);
        UserService userService = new UserService();
        MessageService messageService = new MessageService();
        SessionManager session = new SessionManager();
        ChatSocketServer socketServer = new ChatSocketServer(8887, session);
        new AuthHandlers(userService, session).register(server);
        new ChatHandlers(userService, messageService, session, socketServer).register(server);
        new StaticHandlers().register(server);

        socketServer.start();
        server.start();
        System.out.println("Server on http://localhost:8081");
    }



    private static String getToken(HttpExchange exchange) {
        String header = exchange.getRequestHeaders().getFirst("Cookie");
        return SessionManager.getCookieData(header, "session");
    }

    private static Integer readWith(String query) {
        if (query == null || !query.startsWith("with=")) return null;
        try {
            return Integer.parseInt(query.substring(5));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
