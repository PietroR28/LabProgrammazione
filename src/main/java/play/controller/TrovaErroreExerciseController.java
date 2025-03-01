package play.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.RadioButton;
import javafx.scene.text.TextFlow;
import java.io.FileWriter;
import java.io.IOException;
import org.json.JSONObject;

public class TrovaErroreExerciseController extends MacroExerciseController {
    private int currentExerciseIndex = 0;
    private final int totalExercises = 9;
    private final String[] difficulties = {"principiante", "principiante", "principiante", "intermedio", "intermedio", "intermedio", "esperto", "esperto", "esperto"};
    private boolean[] exerciseResults = new boolean[totalExercises];

    @FXML
    private TextFlow exerciseQuestion;
    @FXML
    private RadioButton answerA;
    @FXML
    private RadioButton answerB;
    @FXML
    private RadioButton answerC;

    @FXML
    public void handleExerciseRedirect() {
        if (currentExerciseIndex < totalExercises) {
            // Load the exercise based on the current index and difficulty
            loadExercise(currentExerciseIndex, difficulties[currentExerciseIndex]);
        } else {
            // Check if all exercises are completed correctly
            boolean allCorrect = true;
            for (boolean result : exerciseResults) {
                if (!result) {
                    allCorrect = false;
                    break;
                }
            }

            if (allCorrect) {
                // Redirect to home and unlock next difficulty
                unlockNextDifficulty();
                redirectToHome();
            } else {
                // Register failure and show failure message
                registerFailure();
                showFailureMessage();
            }
        }
    }

    private void loadExercise(int index, String difficulty) {
        // Logic to load the exercise based on index and difficulty
        // This could involve setting the question, possible answers, etc.
        System.out.println("Loading exercise " + index + " with difficulty " + difficulty);
        // Example logic to set the exercise question and answers
        // Set the exercise question (HTML content can be set here)
        // exerciseQuestion.getChildren().clear();
        // exerciseQuestion.getChildren().add(new Text("Question " + index + " content here"));
        // Set possible answers and other UI elements
        answerA.setText("Answer A for " + difficulty + " exercise");
        answerB.setText("Answer B for " + difficulty + " exercise");
        answerC.setText("Answer C for " + difficulty + " exercise");
    }

    private void unlockNextDifficulty() {
        // Logic to unlock the next difficulty level
        System.out.println("Unlocking next difficulty level");
        // Example logic to unlock the next difficulty
        // Save the unlocked difficulty to a file or database
    }

    private void redirectToHome() {
        // Logic to redirect the user to the home screen
        System.out.println("Redirecting to home screen");
        // Example logic to redirect to home
        // Stage stage = (Stage) someNode.getScene().getWindow();
        // stage.setScene(homeScene);
    }

    private void registerFailure() {
        JSONObject failureData = new JSONObject();
        failureData.put("user", "currentUser"); // Replace with actual user data
        failureData.put("status", "failed");
        failureData.put("exercisesCompleted", currentExerciseIndex);

        try (FileWriter file = new FileWriter("user_exercise_status.json")) {
            file.write(failureData.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showFailureMessage() {
        Alert alert = new Alert(AlertType.INFORMATION, "Hai fallito il set di esercizi. Riprova!", ButtonType.OK);
        alert.showAndWait();
    }

    @FXML
    private void handlePreviousQuestion() {
        if (currentExerciseIndex > 0) {
            currentExerciseIndex--;
            loadExercise(currentExerciseIndex, difficulties[currentExerciseIndex]);
        }
    }

    @FXML
    private void handleNextQuestion() {
        if (currentExerciseIndex < totalExercises - 1) {
            currentExerciseIndex++;
            loadExercise(currentExerciseIndex, difficulties[currentExerciseIndex]);
        }
    }

    @FXML
    private void handleExitExercise() {
        registerFailure();
        showFailureMessage();
        redirectToHome();
    }
}