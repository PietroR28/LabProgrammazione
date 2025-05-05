package play.model;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class ExerciseModel {
    private List<Exercise> exercises;
    private List<Exercise> completaCodiceExercises;
    private static final String EXERCISES_FILE_PATH = "exercises.json";

    public ExerciseModel() {
        exercises = new ArrayList<>();
        completaCodiceExercises = new ArrayList<>();
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

    public List<Exercise> getExercisesByDifficulty(String difficulty) {
        List<Exercise> filteredExercises = new ArrayList<>();
        for (Exercise exercise : exercises) {
            if (exercise.getDifficulty().equals(difficulty)) {
                filteredExercises.add(exercise);
            }
        }
        return filteredExercises;
    }
    
    public List<Exercise> getCompletaCodiceExercisesByDifficulty(String difficulty) {
        List<Exercise> filteredExercises = new ArrayList<>();
        for (Exercise exercise : completaCodiceExercises) {
            if (exercise.getDifficulty().equals(difficulty)) {
                filteredExercises.add(exercise);
            }
        }
        return filteredExercises;
    }

    public Exercise getExercise(int index) {
        if (index >= 0 && index < exercises.size()) {
            return exercises.get(index);
        }
        return null;
    }

    public int getTotalExercises() {
        return exercises.size();
    }
}