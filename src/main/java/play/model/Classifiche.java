package play.model;

import org.json.JSONObject;
import java.io.File;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Classe di utilità per la gestione e il calcolo delle classifiche degli utenti.
 *
 * Questa classe fornisce:
 * - Strutture dati per rappresentare le informazioni di classifica di ciascun utente;
 * - Metodi statici per caricare i dati salvati da file json e calcolare le classifiche
 *   in base al numero di esercizi completati con successo e al tempo totale impiegato
 * (entrambe ordinate in modo decrescente).;
 *
 * I dati vengono letti dal file "saves.json" e sono utilizzati per popolare le schermate di classifica
 * dell'applicazione.
 */

public class Classifiche {
    /**
     * Classe interna che rappresenta un elemento della classifica.
     * Contiene username, numero di successi e tempo totale.
     */
    public static class ClassificaItem {
        public final String username;
        public final int successCount;
        public final long totalTime;
        public ClassificaItem(String u, int s, long t) {
            username = u;
            successCount = s;
            totalTime = t;
        }
    }

    // Oggetto JSON che contiene tutti i dati salvati
    private static final JSONObject SAVES = loadSaves();

    /**
     * Carica i dati dal file "saves.json" e li restituisce come JSONObject.
     * Se il file non esiste o si verifica un errore, restituisce un oggetto vuoto.
     */
    private static JSONObject loadSaves() {
        try {
            File f = new File("saves.json");
            if (!f.exists()) return new JSONObject();
            byte[] bytes = Files.readAllBytes(f.toPath());
            String text = new String(bytes, StandardCharsets.UTF_8).trim();
            return text.isEmpty() ? new JSONObject() : new JSONObject(text);
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    /**
     * Restituisce la classifica degli utenti ordinata per numero di esercizi completati con successo (decrescente).
     * Per ogni utente, conta il numero di livelli completati con risultato "success".
     */
    public static List<ClassificaItem> getSuccessRanking() {
        List<ClassificaItem> list = new ArrayList<>();
        for (String user : SAVES.keySet()) {
            JSONObject userObj = SAVES.getJSONObject(user);
            int cnt = 0;
            for (String ex : userObj.keySet()) {
                JSONObject levels = userObj.getJSONObject(ex);
                for (String lvl : levels.keySet()) {
                    if ("success".equals(levels.getJSONObject(lvl).optString("risultato")))
                        cnt++;
                }
            }
            list.add(new ClassificaItem(user, cnt, 0));
        }
        // Ordina la lista in ordine decrescente di successi
        list.sort((a, b) -> Integer.compare(b.successCount, a.successCount));
        return list;
    }

    /**
     * Restituisce la classifica degli utenti ordinata per tempo totale impiegato (decrescente).
     * Per ogni utente, somma il tempo impiegato in tutti i livelli.
     */
    public static List<ClassificaItem> getTimeRanking() {
        List<ClassificaItem> list = new ArrayList<>();
        for (String user : SAVES.keySet()) {
            JSONObject userObj = SAVES.getJSONObject(user);
            long sum = 0;
            for (String ex : userObj.keySet()) {
                JSONObject levels = userObj.getJSONObject(ex);
                for (String lvl : levels.keySet()) {
                    sum += levels.getJSONObject(lvl).optLong("tempo", 0);
                }
            }
            list.add(new ClassificaItem(user, 0, sum));
        }
        // Ordina la lista in ordine decrescente di tempo totale
        list.sort((a, b) -> Long.compare(b.totalTime, a.totalTime));
        return list;
    }
}