import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.util.Map;

public class ChatHandlers {
    private final UserService userService;
    private final MessageService messageService;
    private final SessionManager sessions;
    private final ChatSocketServer socketServer;

    public ChatHandlers (UserService userService, MessageService messageService,
                         SessionManager sessions, ChatSocketServer socketServer) {
        this.userService = userService;
        this.messageService = messageService;
        this.sessions = sessions;
        this.socketServer = socketServer;
    }

    public void register(HttpServer server) {
        server.createContext("/chat", this::handleChat);
        server.createContext("/send", this::handleSend);
    }

    private void handleChat(HttpExchange exchange) throws IOException {
        User user = sessions.fromRequest(exchange);
        if (user == null) {
            Http.redirect(exchange, "/");
            return;
        }
        try {
            StringBuilder html = new StringBuilder();
            html.append("<h1>Chat</h1>");
            html.append("<p>Logged in as: ").append(Http.escape(user.getName())).append("</p>");
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
                    html.append("<h2>Conversation with ").append(Http.escape(other.getName())).append("</h2>");
                    html.append("<div id='messages' data-me='").append(user.getId())
                            .append("' data-other='").append(other.getId()).append("'/>");
                    for(Message m : messageService.getConversation(user, other)) {
                        html.append("<p><b>").append(Http.escape(m.getSender().getName()));
                        html.append(":</b> ").append(Http.escape(m.getContent())).append("</p>");
                    }
                    html.append("</div>");

                    html.append("<form id='sendForm' method='post' action='/send'>")
                            .append("<input type='hidden' name ='to' value='").append(other.getId()).append("'>")
                            .append("<input type='text' id='content' name='content' required autofocus autocomplete='off'>")
                            .append("<button>Send</button>")
                            .append("</form>");

                    html.append("<script src='chat.js' defer></script>");
                }
            }
            html.append("<form method='post' action='/logout'><button>Logout</button></form>");
            Http.send(exchange, 200, html.toString());
        } catch (RuntimeException e) {
            e.printStackTrace();
            Http.send(exchange, 500, "<p>Server error, please try again later</p>");
        }
    }

    private void handleSend(HttpExchange exchange) throws IOException {
        User me = sessions.fromRequest(exchange);
        if (me == null) { Http.redirect(exchange, "/"); return; }
        if (!exchange.getRequestMethod().equals("POST")) {
            Http.send(exchange, 405, "<p>Method not allowed</p>");
            return;
        }
        Map<String, String> form = Http.readForm(exchange);
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
                Http.redirect(exchange, "/chat?with=" + toId);
            }
        } catch (IllegalArgumentException e) {
            Http.send(exchange, 400, "<p>Error: " + Http.escape(e.getMessage() + "<a href='/chat'>Back</a></p>"));
        } catch (RuntimeException e) {
            e.printStackTrace();
            Http.send(exchange, 500, "<p>Server error, please try again later</p>");
        }
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
