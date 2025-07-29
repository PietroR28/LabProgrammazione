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
 * Classe che gestisce il caricamento e la gestione degli esercizi dal file exercises.json.
 *
 * Questa classe si occupa di:
 * - Caricare gli esercizi dal file JSON;
 * - Gestire due liste distinte di esercizi (normali e "Completa il codice");
 * - Fornire metodi per filtrare gli esercizi per difficoltà;
 * - Restituire esercizi specifici o il numero totale di esercizi.
 */

public class ExerciseModel {
    private List<Exercise> exercises;
    private List<Exercise> completaCodiceExercises;

    // Percorso del file JSON degli esercizi
    private static final String EXERCISES_FILE_PATH = "exercises.json";


    /**
     * Costruttore della classe ExerciseModel.
     * Inizializza le liste e carica gli esercizi dal file JSON.
     */
    public ExerciseModel() {
        exercises = new ArrayList<>();
        completaCodiceExercises = new ArrayList<>();
        loadExercises();
    }


    /**
     * Carica gli esercizi dal file JSON e li suddivide nelle rispettive liste.
     * Gestisce sia esercizi "Trova l'errore" che "Completa il codice".
     */
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
                    
                    if ("TrovaErrore".equals(macroexercise)) {
                        loadTrovaErroreExercise(jsonObject);
                    }
                    else if ("CompletaCodice".equals(macroexercise)) {
                        loadCompletaCodiceExercise(jsonObject);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Errore durante la lettura del file exercises.json: " + e.getMessage());
        }
    }
    

    /**
     * Carica un esercizio di tipo "Trova l'errore" da un oggetto JSON e lo aggiunge alla lista.
     * @param jsonObject Oggetto JSON contenente i dati dell'esercizio
     */
    private void loadTrovaErroreExercise(JSONObject jsonObject) {
        String question = jsonObject.optString("question", "");
        String code = jsonObject.optString("code", "");
        JSONArray answersArray = jsonObject.optJSONArray("answers");
        String[] answers = new String[3];
        if (answersArray != null) {
            for (int j = 0; j < Math.min(answersArray.length(), 3); j++) {
                answers[j] = answersArray.optString(j, "");
            }
        }
        int correctAnswerIndex = jsonObject.optInt("correctAnswerIndex", 0);
        String difficulty = jsonObject.optString("difficulty", "principiante");
        exercises.add(new Exercise(question, code, answers, correctAnswerIndex, difficulty));
    }
    

    /**
     * Carica un esercizio di tipo "Completa il codice" da un oggetto JSON e lo aggiunge alla lista.
     * @param jsonObject Oggetto JSON contenente i dati dell'esercizio
     */
    private void loadCompletaCodiceExercise(JSONObject jsonObject) {
        String question = jsonObject.optString("question", "");
        String code = jsonObject.optString("code", "");
        String answer = jsonObject.optString("answer", "");
        String[] answers = new String[1];
        answers[0] = answer;
        int correctAnswerIndex = 0;
        String difficulty = jsonObject.optString("difficulty", "principiante");
        completaCodiceExercises.add(new Exercise(question, code, answers, correctAnswerIndex, difficulty));
    }


    /**
     * Restituisce la lista degli esercizi "Trova l'errore" filtrati per difficoltà.
     * @param difficulty La difficoltà da filtrare
     * @return Lista di esercizi filtrati
     */
    public List<Exercise> getExercisesByDifficulty(String difficulty) {
        List<Exercise> filteredExercises = new ArrayList<>();
        for (Exercise exercise : exercises) {
            if (exercise.getDifficulty().equals(difficulty)) {
                filteredExercises.add(exercise);
            }
        }
        return filteredExercises;
    }
    

    /**
     * Restituisce la lista degli esercizi "Completa il codice" filtrati per difficoltà.
     * @param difficulty La difficoltà da filtrare
     * @return Lista di esercizi filtrati
     */
    public List<Exercise> getCompletaCodiceExercisesByDifficulty(String difficulty) {
        List<Exercise> filteredExercises = new ArrayList<>();
        for (Exercise exercise : completaCodiceExercises) {
            if (exercise.getDifficulty().equals(difficulty)) {
                filteredExercises.add(exercise);
            }
        }
        return filteredExercises;
    }


    /**
     * Restituisce un esercizio dalla lista "Trova l'errore" dato l'indice.
     * @param index L'indice dell'esercizio
     * @return L'esercizio corrispondente o null se l'indice non è valido
     */
    public Exercise getExercise(int index) {
        if (index >= 0 && index < exercises.size()) {
            return exercises.get(index);
        }
        return null;
    }


    /**
     * Restituisce il numero totale di esercizi "Trova l'errore" caricati.
     * @return Numero di esercizi
     */
    public int getTotalExercises() {
        return exercises.size();
    }
}