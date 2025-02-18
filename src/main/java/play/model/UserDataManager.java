package play.model;

import java.util.HashMap;
import java.util.Map;

public class UserDataManager {

    private static final Map<String, String> fakeUsers = new HashMap<>();
    static {
        fakeUsers.put("admin", "1234");
        fakeUsers.put("user", "pass");
    }

    public static boolean checkUser(String username, String password) {
        return fakeUsers.containsKey(username) && fakeUsers.get(username).equals(password);
    }
}