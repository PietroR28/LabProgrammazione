package play.controller;

import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONObject;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.RadioButton;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import play.model.Exercise;
import play.model.ExerciseModel;

public class ExerciseController {

    protected int currentExerciseIndex = 0;
    protected final ExerciseModel exerciseModel = new ExerciseModel();
    protected boolean[] exerciseResults;

    @FXML
    protected TextFlow exerciseQuestion;
    @FXML
    protected TextFlow exerciseCode;
    @FXML
    protected RadioButton answerA;
    @FXML
    protected RadioButton answerB;
    @FXML
    protected RadioButton answerC;

    public ExerciseController() {
        // Nota: Non chiamare loadExercise qui perché gli elementi FXML non sono ancora iniettati.
        exerciseResults = new boolean[exerciseModel.getTotalExercises()];
    }

    // Questo metodo viene chiamato subito dopo l'iniezione degli elementi FXML
    @FXML
    public void initialize() {
        loadExercise(currentExerciseIndex);
    }

    protected void loadExercise(int index) {
        Exercise exercise = exerciseModel.getExercise(index);
        System.out.println("Loading exercise " + index);
        // Set the exercise question, code and answers
        exerciseQuestion.getChildren().clear();
        exerciseQuestion.getChildren().add(new Text(exercise.getQuestion()));
        exerciseCode.getChildren().clear();
        exerciseCode.getChildren().add(new Text(exercise.getCode()));
        answerA.setText(exercise.getAnswers()[0]);
        answerB.setText(exercise.getAnswers()[1]);
        answerC.setText(exercise.getAnswers()[2]);
    }

    protected void unlockNextDifficulty() {
        // Logic to unlock the next difficulty level
        System.out.println("Unlocking next difficulty level");
        // Esempio: salva la difficoltà sbloccata su file o database
    }

    protected void redirectToHome() {
        // Logic to redirect the user to the home screen
        System.out.println("Redirecting to home screen");
        // Esempio: Stage stage = (Stage) someNode.getScene().getWindow();
        // stage.setScene(homeScene);
    }

    protected void registerFailure() {
        JSONObject failureData = new JSONObject();
        failureData.put("user", "currentUser"); // Sostituire con i dati reali dell'utente
        failureData.put("status", "failed");
        failureData.put("exercisesCompleted", currentExerciseIndex);

        try (FileWriter file = new FileWriter("user_exercise_status.json")) {
            file.write(failureData.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void showFailureMessage() {
        Alert alert = new Alert(AlertType.INFORMATION, "Hai fallito il set di esercizi. Riprova!", ButtonType.OK);
        alert.showAndWait();
    }

    @FXML
    protected void handlePreviousQuestion() {
        if (currentExerciseIndex > 0) {
            currentExerciseIndex--;
            loadExercise(currentExerciseIndex);
        }
    }

    @FXML
    protected void handleNextQuestion() {
        if (currentExerciseIndex < exerciseModel.getTotalExercises() - 1) {
            currentExerciseIndex++;
            loadExercise(currentExerciseIndex);
        }
    }

    @FXML
    protected void handleExitExercise() {
        registerFailure();
        showFailureMessage();
        redirectToHome();
    }
}