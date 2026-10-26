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
        SessionManager session = new SessionManager();

        server.createContext("/", exchange ->{
            byte[] body = readResource("login.html");
            exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });

        server.createContext("/register", exchange -> {
            if(!exchange.getRequestMethod().equals("POST")) {
                send(exchange, 405, "<p>Method not allowed</p>");
                return;
            }
            Map<String, String> form = readForm(exchange);
            try {
                userService.register(form.get("username"), form.get("password"));
                send(exchange, 200, "<p>Registered! <a href='/'>Go to Login</a></p>");
            } catch (IllegalArgumentException e) {
                send(exchange, 400, "<p>Error: " + e.getMessage() + "<a href='/'>Back</a></p>");
            } catch (RuntimeException e) {
                e.printStackTrace();
                send(exchange, 500, "<p>Server error, try again later</p>" );
            }
        });

        server.createContext("/login", exchange -> {
           if(!exchange.getRequestMethod().equals("POST")) {
               send(exchange, 405, "<p>Method not allowed</p>");
           }
           Map<String, String> form = readForm(exchange);
           try {
               User user = userService.login(form.get("username"), form.get("password"));
               String token = session.create(user);
               exchange.getResponseHeaders().add("Set-Cookie",
                       "session=" + token + "; HttpOnly; SameSite=Strict; Path=/");
               exchange.getResponseHeaders().add("Location", "/chat");
               exchange.sendResponseHeaders(303, -1);
               exchange.close();
           } catch (IllegalArgumentException e) {
               send(exchange, 400, "<p>Error: " + escape(e.getMessage()) + "<a href='/'>Back</a></p>");
           } catch (RuntimeException e) {
               e.printStackTrace();
               send(exchange, 500, "<p>Server error, try again later</p>" );
           }
        });

        server.createContext("/chat", exchange -> {
            User user = currentUser(exchange, session);
            if (user == null) {
                exchange.getResponseHeaders().add("Location", "/");
                exchange.sendResponseHeaders(303, -1);
                exchange.close();
            }
            send(exchange, 200,
                    "<h1>Chat</h1><p>Logged in as: " + escape(user.getName()) + "</p>"
            + "<form method='post' action='/logout'><button>Logout</button></from>");
        });

        server.createContext("/logout", exchange -> {
           String token = getToken(exchange);
           session.remove(token);
           exchange.getResponseHeaders().add("Set-Cookie",
                   "session =; Max-Age=0; HttpOnly; SameSite=Strict; Path=/");
           exchange.getResponseHeaders().add("Location", "/");
           exchange.sendResponseHeaders(303,-1);
           exchange.close();
        });

        server.start();
        System.out.println("Server on http://localhost:8081");
    }

    private static String escape(String s) {
        if (s== null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
    private static byte[] readResource(String name) throws IOException {
        try (InputStream in = WebServer.class.getResourceAsStream("/" + name)) {
            if (in == null) {
                throw new IOException("Resource not found : " + name);
            }
            return in.readAllBytes();
        }
    }

    private static Map<String, String> readForm(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, String> form = new HashMap<>();
        for (String pair : body.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                form.put(
                        URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(kv[1], StandardCharsets.UTF_8)
                );
            }
        }
        return form;
    }

    private static void send(HttpExchange exchange, int status, String html) throws IOException {
        byte[] body = html.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(status, body.length);
        exchange.getResponseBody().write(body);
        exchange.close();

    }

    private static User currentUser(HttpExchange exchange, SessionManager sessions) {
        String token = getToken(exchange);
        if (token == null) return null;
        return sessions.get(token);
    }

    private static String getToken(HttpExchange exchange) {
        String header = exchange.getRequestHeaders().getFirst("Cookie");
        if (header == null) return null;
        for (String part : header.split(";")) {
            String[] kv = part.trim().split("=", 2);
            if (kv.length == 2 && kv[0].equals("session")) {
                return kv[1];
            }
        }
        return null;
    }
}
