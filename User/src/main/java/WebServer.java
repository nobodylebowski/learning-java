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
                redirect(exchange, "/");
                return;
            }
            try {
                StringBuilder html = new StringBuilder();
                html.append("<h1>Chat</h1>");
                html.append("<p>Logged in as: ").append(escape(user.getName())).append("</p>");
                html.append("<h2>Users</h2><ul>");
                for(User u : userService.findAllExcept(user.getId())) {
                    html.append("<li><a href='/chat?with=").append(u.getId()).append("'>");
                    html.append(u.getName()).append("</a></li>");
                }
                html.append("</ul>");

                Integer withId = readWith(exchange.getRequestURI().getQuery());
                if (withId != null) {
                    User other = userService.findById(withId);
                    if (other == null) {
                        html.append("<p>User not found</p>");
                    } else {
                        html.append("<h2>Conversation with ").append(escape(other.getName())).append("</h2>");
                        html.append("<div id='messages'>");
                        for(Message m : messageService.getConversation(user, other)) {
                            html.append("<p><b>").append(escape(m.getSender().getName()));
                            html.append(":</b> ").append(escape(m.getContent())).append("</p>");
                        }
                        html.append("</div>");

                        html.append("<form id='sendForm' method='post' action='/send'>")
                                .append("<input type='hidden' name ='to' value='").append(other.getId()).append("'>")
                                .append("<input type='text' id='content' name='content' required autofocus autocomplete='off'>")
                                .append("<button>Send</button>")
                                .append("</form>");

                        html.append("<script>")
                                .append("const box = document.getElementById('messages');")
                                .append("const ws = new WebSocket('ws://localhost:8887');")
                                .append("ws.onopen = () => console.log('WebSocket connected');")
                                .append("const me = ").append(user.getId()).append(";")
                                .append("const other = ").append(other.getId()).append(";")
                                .append("ws.onmessage = (e) => {")
                                .append("  const m = JSON.parse(e.data);")
                                .append("  const inThisChat = (m.from === me && m.to === other) || (m.from === other && m.to === me);")
                                .append("  if (!inThisChat) return;")
                                .append("  const p = document.createElement('p');")
                                .append("  const b = document.createElement('b');")
                                .append("  b.textContent = m.name + ': ';")
                                .append("  p.appendChild(b);")
                                .append("  p.appendChild(document.createTextNode(m.content));")
                                .append("  box.appendChild(p);")
                                .append("  p.scrollIntoView();")
                                .append("};")
                                .append("const form = document.getElementById('sendForm');")
                                .append("const input = document.getElementById('content');")
                                .append("form.addEventListener('submit', async (ev) => {")
                                .append("  ev.preventDefault();")
                                .append("  const text = input.value;")
                                .append("  if (text.trim() === '') return;")
                                .append("  const body = new URLSearchParams(new FormData(form));")
                                .append("  try {")
                                .append("    const res = await fetch('/send', { method: 'POST', body: body, headers: { 'X-Requested-With': 'fetch' } });")
                                .append("    if (res.ok)  { input.value = ''; input.focus(); }")
                                .append("    else { alert('Message not sent'); }")
                                .append("  } catch (err) { alert('Network Error'); }")
                                .append("});")
                                .append("box.lastElementChild && box.lastElementChild.scrollIntoView();")
                                .append("</script>");

                    }
                }
                html.append("<form method='post' action='/logout'><button>Logout</button></form>");
                send(exchange, 200, html.toString());
            } catch (RuntimeException e) {
                e.printStackTrace();
                send(exchange, 500, "<p>Server error, please try again later</p>");
            }
        });

        server.createContext("/send", exchange -> {
            User me = currentUser(exchange, session);
            if (me == null) { redirect(exchange, "/"); return; }
            if (!exchange.getRequestMethod().equals("POST")) {
                send(exchange, 405, "<p>Method not allowed</p>");
                return;
            }
            Map<String, String> form = readForm(exchange);
            try {
                int toId = Integer.parseInt(form.get("to"));
                User other = userService.findById(toId);
                if (other == null) throw new IllegalArgumentException("User not found");
                Message saved = messageService.send(me, other, form.get("content"));
                socketServer.pushMessage(saved);

                boolean viaFetch = "fetch".equals(exchange.getResponseHeaders().getFirst("X-Requested-With"));
                if (viaFetch) {
                    exchange.sendResponseHeaders(204, -1);
                    exchange.close();
                }
                else {
                    redirect(exchange, "/chat?with=" + toId);
                }
            } catch (IllegalArgumentException e) {
                send(exchange, 400, "<p>Error: " + escape(e.getMessage() + "<a href='/chat'>Back</a></p>"));
            } catch (RuntimeException e) {
                e.printStackTrace();
                send(exchange, 500, "<p>Server error, please try again later</p>");
            }
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
        socketServer.start();
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

    private static void redirect(HttpExchange exchange, String location) throws IOException{
        exchange.getResponseHeaders().add("Location", location);
        exchange.sendResponseHeaders(303, -1);
        exchange.close();
    }

}
