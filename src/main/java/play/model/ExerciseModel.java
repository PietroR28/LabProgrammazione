package play.model;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class ExerciseModel {
    private List<Exercise> exercises;

    public ExerciseModel() {
        exercises = new ArrayList<>();
        loadExercises();
    }

    private void loadExercises() {
        try (FileReader reader = new FileReader("exercises.json")) {
            JSONArray jsonArray = new JSONArray(new JSONTokener(reader));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                // Modifica qui: usiamo "question" invece di "text"
                String question = jsonObject.optString("question", "Default question");
                String code = jsonObject.optString("code", "Default code");
                JSONArray answersArray = jsonObject.optJSONArray("answers");
                String[] answers = new String[3];
                if (answersArray != null) {
                    for (int j = 0; j < answersArray.length(); j++) {
                        answers[j] = answersArray.optString(j, "Default answer");
                    }
                } else {
                    answers[0] = "Default answer A";
                    answers[1] = "Default answer B";
                    answers[2] = "Default answer C";
                }
                int correctAnswerIndex = jsonObject.optInt("correctAnswerIndex", 0);
                exercises.add(new Exercise(question, code, answers, correctAnswerIndex));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Exercise getExercise(int index) {
        return exercises.get(index);
    }

    public int getTotalExercises() {
        return exercises.size();
    }
}