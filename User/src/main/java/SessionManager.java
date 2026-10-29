import com.sun.net.httpserver.HttpExchange;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private final Map<String, User> session = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();

    public String create(User user) {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        session.put(token, user);
        return token;
    }

    public User get(String token) {
        if (token==null) return null;
        return session.get(token);
    }

    public void remove(String token) {
        if (token != null) session.remove(token);
    }

    public static String getCookieData(String cookieHeader, String data) {
        String token = null;
        if (cookieHeader != null) {
            for (String part : cookieHeader.split(";")) {
                String[] kv = part.trim().split("=", 2);
                if (kv.length == 2 && kv[0].equals(data)) {
                    token = kv[1];
                }
            }
        }
        return token;
    }

    public User fromRequest(HttpExchange exchange) {
        String token = getCookieData(exchange.getRequestHeaders().getFirst("Cookie"), "session");
        return get(token);
    }
}
