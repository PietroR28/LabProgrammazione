package play.model;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class OrdinaCodiceExerciseModel {
    private List<Exercise> ordinaCodiceExercises;
    private static final String EXERCISES_FILE_PATH = "exercises.json";

    public OrdinaCodiceExerciseModel() {
        ordinaCodiceExercises = new ArrayList<>();
        loadExercises();
    }

    private void loadExercises() {
        try {
            File exerciseFile = new File(EXERCISES_FILE_PATH);

            if (!exerciseFile.exists()) {
                System.out.println("File exercises.json non trovato nel percorso: " + EXERCISES_FILE_PATH);
                exerciseFile = new File("LabProgrammazione/exercises.json");
                if (!exerciseFile.exists()) {
                    System.out.println("File exercises.json non trovato nemmeno nel percorso: LabProgrammazione/exercises.json");
                    return;
                }
            }

            try (FileReader reader = new FileReader(exerciseFile)) {
                JSONArray jsonArray = new JSONArray(new JSONTokener(reader));
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String macroexercise = jsonObject.optString("macroexercise", "");

                    if ("OrdinaCodice".equals(macroexercise)) {
                        loadOrdinaCodiceExercise(jsonObject);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Errore durante la lettura del file exercises.json: " + e.getMessage());
        }
    }

    private void loadOrdinaCodiceExercise(JSONObject jsonObject) {
        String question = jsonObject.optString("question", "");

        // Gestisci il codice come array di stringhe o come stringa singola
        String codeString = "";
        JSONArray codeArray = jsonObject.optJSONArray("code");
        if (codeArray != null) {
            StringBuilder codeBuilder = new StringBuilder();
            for (int j = 0; j < codeArray.length(); j++) {
                codeBuilder.append(codeArray.getString(j));
                if (j < codeArray.length() - 1) {
                    codeBuilder.append("\n");
                }
            }
            codeString = codeBuilder.toString();
        } else {
            // Se il codice è fornito come stringa singola
            codeString = jsonObject.optString("code", "");
        }

        // Ottieni l'array dell'ordine corretto
        JSONArray correctOrderArray = jsonObject.optJSONArray("correctOrder");
        int[] correctOrder = new int[correctOrderArray.length()];
        for (int j = 0; j < correctOrderArray.length(); j++) {
            correctOrder[j] = correctOrderArray.getInt(j);
        }

        String difficulty = jsonObject.optString("difficulty", "principiante");

        // Crea un nuovo esercizio con le informazioni estratte
        OrdinaCodiceExerciseItem exercise = new OrdinaCodiceExerciseItem(
                question, codeString, correctOrder, difficulty);

        ordinaCodiceExercises.add(exercise);
    }

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