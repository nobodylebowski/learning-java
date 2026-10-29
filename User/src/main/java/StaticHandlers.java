import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;

public class StaticHandlers {
    public void register(HttpServer server) {
        server.createContext("/chat.js", exchange -> serve(exchange, "chat.js", "application/javascript; charset=UTF-8"));
        server.createContext("/", exchange -> serve(exchange, "login.html", "text/html; charset=UTF-8"));
    }

    private void serve(HttpExchange exchange, String resource, String type) throws IOException {
        byte[] body = Http.readResource(resource);
        exchange.getResponseHeaders().add("Content-Type", type);
        exchange.sendResponseHeaders(200, body.length);
        exchange.getResponseBody().write(body);
        exchange.close();
    }
}
