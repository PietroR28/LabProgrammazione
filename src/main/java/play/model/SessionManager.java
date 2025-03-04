package play.model;

public class SessionManager {
    private static String username = "";

    public static void setUsername(String username) {
        SessionManager.username = username;
    }

    public static String getUsername() {
        return username;
    }
}