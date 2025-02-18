package play.model;

import java.util.HashMap;
import java.util.Map;

public class UserDataManager {

    private static Map<String, String> users = new HashMap<>(); // Usa una mappa per immagazzinare nome utente e password

    static {
        // Aggiungi un utente di test
        users.put("user", "pass");
    }

    public static boolean checkUser(String username, String password) {
        return users.containsKey(username) && users.get(username).equals(password);
    }

    public static boolean saveNewUser(String firstName, String lastName, String username, String password) {
        // Verifica che l'username non sia già presente
        if (users.containsKey(username)) {
            return false;
        }

        // Salva il nuovo utente
        users.put(username, password);
        return true;
    }
}
