public class Config {
    public static final int HTTP_PORT =
            Integer.parseInt(System.getenv().getOrDefault("HTTP_PORT", "8081"));
    public static final int WS_PORT = 8887;
    public static final String ALLOWED_ORIGIN = "http://localhost:" + HTTP_PORT;
}
