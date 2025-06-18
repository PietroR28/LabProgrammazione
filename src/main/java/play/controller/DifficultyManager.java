package play.controller;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.json.JSONObject;
import play.model.SessionManager;

/**
 * Utility class per gestire correttamente la progressione delle difficoltà
 */
public class DifficultyManager {

    /**
     * Determina quale dovrebbe essere la difficoltà corrente per un esercizio
     * basandosi sui progressi dell'utente
     * @param exerciseName Nome dell'esercizio (TrovaErrore, OrdinaCodice, CompletaCodice)
     * @return La difficoltà che l'utente dovrebbe affrontare ("principiante", "intermedio", "esperto")
     */
    public static String getCurrentDifficulty(String exerciseName) {
        String username = SessionManager.getUsername();
        if (username == null || username.trim().isEmpty()) {
            return "principiante"; // Default per utenti non loggati
        }

        JSONObject savesData = loadSavesData();
        if (savesData == null || !savesData.has(username)) {
            return "principiante"; // Nessun progresso salvato
        }

        JSONObject userSaves = savesData.getJSONObject(username);
        if (!userSaves.has(exerciseName)) {
            return "principiante"; // Nessun progresso per questo esercizio
        }

        JSONObject exerciseData = userSaves.getJSONObject(exerciseName);

        // Controlla i livelli in ordine per trovare il prossimo da completare
        if (!isLevelCompleted(exerciseData, "principiante")) {
            return "principiante";
        } else if (!isLevelCompleted(exerciseData, "intermedio")) {
            return "intermedio";
        } else if (!isLevelCompleted(exerciseData, "esperto")) {
            return "esperto";
        } else {
            // Tutti i livelli completati, torna al principiante per rigiocarlo
            return "principiante";
        }
    }

    /**
     * Controlla se un livello specifico è stato completato con successo
     */
    private static boolean isLevelCompleted(JSONObject exerciseData, String difficulty) {
        if (!exerciseData.has(difficulty)) {
            return false;
        }

        JSONObject difficultyData = exerciseData.getJSONObject(difficulty);
        return difficultyData.has("risultato") &&
                "success".equals(difficultyData.getString("risultato"));
    }

    /**
     * Ottiene il prossimo livello di difficoltà dopo aver completato il livello corrente
     */
    public static String getNextDifficulty(String currentDifficulty) {
        switch (currentDifficulty) {
            case "principiante":
                return "intermedio";
            case "intermedio":
                return "esperto";
            case "esperto":
                return "esperto"; // Rimane al livello esperto
            default:
                return "principiante";
        }
    }

    /**
     * Verifica se l'utente può accedere a un determinato livello di difficoltà
     */
    public static boolean canAccessDifficulty(String exerciseName, String targetDifficulty) {
        String currentDifficulty = getCurrentDifficulty(exerciseName);

        // L'utente può sempre accedere al livello corrente o a livelli precedenti
        String[] difficultyOrder = {"principiante", "intermedio", "esperto"};
        int currentIndex = getDifficultyIndex(currentDifficulty);
        int targetIndex = getDifficultyIndex(targetDifficulty);

        return targetIndex <= currentIndex;
    }

    /**
     * Ottiene l'indice numerico di una difficoltà
     */
    private static int getDifficultyIndex(String difficulty) {
        switch (difficulty) {
            case "principiante": return 0;
            case "intermedio": return 1;
            case "esperto": return 2;
            default: return 0;
        }
    }

    /**
     * Carica i dati dal file saves.json
     */
    private static JSONObject loadSavesData() {
        File file = new File("saves.json");
        if (!file.exists()) {
            return null;
        }

        try {
            String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            if (content.trim().isEmpty()) {
                return null;
            }
            return new JSONObject(content);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Metodo per debug: stampa lo stato attuale di tutti gli esercizi
     */
    public static void printUserProgress() {
        String username = SessionManager.getUsername();
        System.out.println("=== STATO PROGRESSI PER: " + username + " ===");

        String[] exercises = {"TrovaErrore", "OrdinaCodice", "CompletaCodice"};
        for (String exercise : exercises) {
            String currentDiff = getCurrentDifficulty(exercise);
            System.out.println(exercise + " -> Prossimo livello: " + currentDiff);
        }
        System.out.println("===========================================");
    }
}