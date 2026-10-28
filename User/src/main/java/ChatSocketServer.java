import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class ChatSocketServer extends WebSocketServer {
    private final SessionManager sessions;
    private final Map<Integer, Set<WebSocket>> connections = new ConcurrentHashMap<>();

    public ChatSocketServer(int port, SessionManager sessions) {
        super(new InetSocketAddress(port));
        this.sessions = sessions;
    }

    @Override
    public void onStart(){
        System.out.println("WebSocket server on ws://localhost:" + getPort());
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        String cookieHeader = handshake.getFieldValue("Cookie");
        String token = SessionManager.getCookieData(cookieHeader, "session");


        User user = sessions.get(token);
        if (token == null) {
            conn.close(1008, "Not logged in");
            return;
        }

        String origin = handshake.getFieldValue("Origin");
        if(!"http://localhost:8081".equals(origin)) {
            conn.close(1008, "Bad origin");
            return;
        }
        conn.setAttachment(user);
        connections.computeIfAbsent(user.getId(), k -> new CopyOnWriteArraySet<>()).add(conn);
        System.out.println(user.getName() + " connected");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {

    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        User user = conn.getAttachment();
        if (user != null) {
            Set<WebSocket> set = connections.get(user.getId());
            if(set != null) {
                set.remove(conn);
            }
            System.out.println(user.getName() + " disconnected");
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    public void pushMessage(Message m) {
        String json = "{\"from\":" + m.getSender().getId()
                + ",\"to\":"  + m.getReceiver().getId()
                + ",\"name\":\"" + jsonEscape(m.getSender().getName()) + "\""
                + ",\"content\":\"" + jsonEscape(m.getContent()) + "\"}";
        sendToUser(m.getSender().getId(), json);
        sendToUser(m.getReceiver().getId(), json);
    }

    private void sendToUser(int userId, String json) {
        Set<WebSocket> set = connections.get(userId);
        if (set == null) return;
        for (WebSocket conn : set) {
            if (conn.isOpen()) {
                conn.send(json);
            }
        }
    }

    private static String jsonEscape(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
                }
            }
        }
        return sb.toString();
    }
}
