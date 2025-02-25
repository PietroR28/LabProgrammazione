package play.model;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class UserDataManager {

    private static final String FILE_PATH = "users.json";
    private static Map<String, User> users = new HashMap<>(); // Usa una mappa per immagazzinare nome utente e oggetto User

    static {
        // Carica gli utenti dal file JSON
        loadUsers();
    }

    public static boolean checkUser(String username, String password) {
        return users.containsKey(username) && users.get(username).getPassword().equals(password);
    }

    public static boolean saveNewUser(String firstName, String lastName, String username, String password) {
        // Verifica che l'username non sia già presente
        if (users.containsKey(username)) {
            return false;
        }

        // Crea un nuovo oggetto User e salvalo
        User newUser = new User(firstName, lastName, username, password);
        users.put(username, newUser);
        saveUsers();
        return true;
    }

    private static void saveUsers() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(users, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadUsers() {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(FILE_PATH)) {
            Type type = new TypeToken<Map<String, User>>() {}.getType();
            users = gson.fromJson(reader, type);
            if (users == null) {
                users = new HashMap<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}