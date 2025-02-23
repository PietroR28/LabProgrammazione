package play.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class UserDataManager {

    private static final String FILE_PATH = "users.json";
    private static Map<String, String> users = new HashMap<>(); // Usa una mappa per immagazzinare nome utente e password

    static {
        // Carica gli utenti dal file JSON
        loadUsers();
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
        saveUsers();
        return true;
    }

    private static void saveUsers() {
        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(users, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadUsers() {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(FILE_PATH)) {
            Type type = new TypeToken<Map<String, String>>() {}.getType();
            users = gson.fromJson(reader, type);
            if (users == null) {
                users = new HashMap<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}