package play.model;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Modello che si occupa di caricare gli esercizi di ordinamento codice dal file JSON.
 * Legge exercises.json e crea oggetti Exercise per il tipo "OrdinaCodice".
 */
public class OrdinaCodiceExerciseModel {
    private List<Exercise> ordinaCodiceExercises;
    private static final String EXERCISES_FILE_PATH = "exercises.json";

    public OrdinaCodiceExerciseModel() {
        ordinaCodiceExercises = new ArrayList<>();
        loadExercises(); // Carica subito gli esercizi dal JSON
    }

    /**
     * Carica tutti gli esercizi dal file JSON, filtrando solo quelli di tipo "OrdinaCodice"
     */
    private void loadExercises() {
        try {
            // Cerca il file JSON in due possibili percorsi
            File exerciseFile = new File(EXERCISES_FILE_PATH);

            if (!exerciseFile.exists()) {
                System.out.println("File exercises.json non trovato nel percorso: " + EXERCISES_FILE_PATH);
                exerciseFile = new File("LabProgrammazione/exercises.json");
                if (!exerciseFile.exists()) {
                    System.out.println("File exercises.json non trovato nemmeno nel percorso: LabProgrammazione/exercises.json");
                    return;
                }
            }

            // Legge il file JSON e lo converte in array
            try (FileReader reader = new FileReader(exerciseFile)) {
                JSONArray jsonArray = new JSONArray(new JSONTokener(reader));

                // Scorre tutti gli esercizi nel JSON
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String macroexercise = jsonObject.optString("macroexercise", "");

                    // Prende solo quelli del nostro tipo
                    if ("OrdinaCodice".equals(macroexercise)) {
                        loadOrdinaCodiceExercise(jsonObject);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Errore durante la lettura del file exercises.json: " + e.getMessage());
        }
    }

    /**
     * Converte un singolo esercizio JSON in un oggetto OrdinaCodiceExerciseItem
     */
    private void loadOrdinaCodiceExercise(JSONObject jsonObject) {
        String question = jsonObject.optString("question", "");

        // Il codice può essere sia array di stringhe che stringa singola
        String codeString = "";
        JSONArray codeArray = jsonObject.optJSONArray("code");
        if (codeArray != null) {
            // Se è un array, unisce tutte le righe con \n
            StringBuilder codeBuilder = new StringBuilder();
            for (int j = 0; j < codeArray.length(); j++) {
                codeBuilder.append(codeArray.getString(j));
                if (j < codeArray.length() - 1) {
                    codeBuilder.append("\n");
                }
            }
            codeString = codeBuilder.toString();
        } else {
            // Se è una stringa singola, la usa così com'è
            codeString = jsonObject.optString("code", "");
        }

        // PARTE CRUCIALE: Converte l'array JSON correctOrder in array Java
        JSONArray correctOrderArray = jsonObject.optJSONArray("correctOrder");
        int[] correctOrder = new int[correctOrderArray.length()];
        for (int j = 0; j < correctOrderArray.length(); j++) {
            correctOrder[j] = correctOrderArray.getInt(j);
        }

        String difficulty = jsonObject.optString("difficulty", "principiante");

        // Crea l'esercizio e lo aggiunge alla lista
        OrdinaCodiceExerciseItem exercise = new OrdinaCodiceExerciseItem(
                question, codeString, correctOrder, difficulty);

        ordinaCodiceExercises.add(exercise);
    }

    /**
     * Restituisce solo gli esercizi della difficoltà richiesta
     */
    public List<Exercise> getOrdinaCodiceExercisesByDifficulty(String difficulty) {
        List<Exercise> filteredExercises = new ArrayList<>();
        for (Exercise exercise : ordinaCodiceExercises) {
            if (exercise.getDifficulty().equals(difficulty)) {
                filteredExercises.add(exercise);
            }
        }
        return filteredExercises;
    }
}