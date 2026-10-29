import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.util.Map;

public class AuthHandlers {
    private final UserService userService;
    private final SessionManager sessions;

    public AuthHandlers(UserService userService, SessionManager sessions) {
        this.userService = userService;
        this.sessions = sessions;
    }

    public void register(HttpServer server) {
        server.createContext("/register", this::handleRegister);
        server.createContext("/login", this::handleLogin);
        server.createContext("/logout", this::handleLogout);
    }

    private void handleRegister(HttpExchange exchange) throws IOException {
        if(!exchange.getRequestMethod().equals("POST")) {
            Http.send(exchange, 405, "<p>Method not allowed</p>");
            return;
        }
        Map<String, String> form = Http.readForm(exchange);
        try {
            userService.register(form.get("username"), form.get("password"));
            Http.send(exchange, 200, "<p>Registered! <a href='/'>Go to Login</a></p>");
        } catch (IllegalArgumentException e) {
            Http.send(exchange, 400, "<p>Error: " + e.getMessage() + "<a href='/'>Back</a></p>");
        } catch (RuntimeException e) {
            e.printStackTrace();
            Http.send(exchange, 500, "<p>Server error, try again later</p>" );
        }
    }

    private void handleLogin(HttpExchange exchange) throws IOException {
        if(!exchange.getRequestMethod().equals("POST")) {
            Http.send(exchange, 405, "<p>Method not allowed</p>");
        }
        Map<String, String> form = Http.readForm(exchange);
        try {
            User user = userService.login(form.get("username"), form.get("password"));
            String token = sessions.create(user);
            exchange.getResponseHeaders().add("Set-Cookie",
                    "session=" + token + "; HttpOnly; SameSite=Strict; Path=/");
            exchange.getResponseHeaders().add("Location", "/chat");
            exchange.sendResponseHeaders(303, -1);
            exchange.close();
        } catch (IllegalArgumentException e) {
            Http.send(exchange, 400, "<p>Error: " + Http.escape(e.getMessage()) + "<a href='/'>Back</a></p>");
        } catch (RuntimeException e) {
            e.printStackTrace();
            Http.send(exchange, 500, "<p>Server error, try again later</p>" );
        }
    }

    private void handleLogout(HttpExchange exchange) throws IOException {
        String token = getToken(exchange);
        sessions.remove(token);
        exchange.getResponseHeaders().add("Set-Cookie",
                "session =; Max-Age=0; HttpOnly; SameSite=Strict; Path=/");
        exchange.getResponseHeaders().add("Location", "/");
        exchange.sendResponseHeaders(303,-1);
        exchange.close();
    }

    private static String getToken(HttpExchange exchange) {
        String header = exchange.getRequestHeaders().getFirst("Cookie");
        return SessionManager.getCookieData(header, "session");
    }
}
