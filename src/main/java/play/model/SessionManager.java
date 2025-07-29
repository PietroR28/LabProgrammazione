
/**
 * Classe di utilità per la gestione della sessione utente.
 *
 * Questa classe si occupa di impostare, memorizzare e recuperare 
 * lo username dell'utente attualmente loggato.
 */
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