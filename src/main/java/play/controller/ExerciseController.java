package play.controller;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.json.JSONObject;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.RadioButton;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import play.model.Exercise;
import play.model.ExerciseModel;

public class ExerciseController {

    protected int currentExerciseIndex = 0;
    protected final ExerciseModel exerciseModel = new ExerciseModel();
    protected boolean[] exerciseResults;
    protected List<Exercise> currentExercises;
    protected String currentDifficulty = "principiante";
    protected String username; // Aggiungi un campo per lo username

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
    @FXML
    protected Button finishExerciseButton; // Aggiungi il riferimento al pulsante

    public ExerciseController() {
        // Nota: Non chiamare loadExercise qui perché gli elementi FXML non sono ancora iniettati.
        loadExercisesByDifficulty(currentDifficulty);
    }

    // Metodo per impostare lo username
    public void setUsername(String username) {
        this.username = username;
    }

    // Questo metodo viene chiamato subito dopo l'iniezione degli elementi FXML
    @FXML
    public void initialize() {
        loadExercise(currentExerciseIndex);
    }

    protected void loadExercisesByDifficulty(String difficulty) {
        currentExercises = exerciseModel.getExercisesByDifficulty(difficulty);
        exerciseResults = new boolean[currentExercises.size()];
    }

    protected void loadExercise(int index) {
        Exercise exercise = currentExercises.get(index);
        System.out.println("Loading exercise " + index);
        // Set the exercise question, code and answers
        exerciseQuestion.getChildren().clear();
        exerciseQuestion.getChildren().add(new Text(exercise.getQuestion()));
        exerciseCode.getChildren().clear();
        exerciseCode.getChildren().add(new Text(exercise.getCode()));
        answerA.setText(exercise.getAnswers()[0]);
        answerB.setText(exercise.getAnswers()[1]);
        answerC.setText(exercise.getAnswers()[2]);

        // Deselect all RadioButtons
        answerA.setSelected(false);
        answerB.setSelected(false);
        answerC.setSelected(false);

        // Mostra il pulsante "Concludi Esercizio" solo se è l'ultimo esercizio
        if (index == currentExercises.size() - 1) {
            finishExerciseButton.setVisible(true);
        } else {
            finishExerciseButton.setVisible(false);
        }
    }

    protected void unlockNextDifficulty() {
        if ("principiante".equals(currentDifficulty)) {
            currentDifficulty = "intermedio";
        } else if ("intermedio".equals(currentDifficulty)) {
            currentDifficulty = "esperto";
        }
        loadExercisesByDifficulty(currentDifficulty);
        currentExerciseIndex = 0;
        loadExercise(currentExerciseIndex);
    }

    protected void redirectToHome() {
        try {
            URL fxmlLocation = getClass().getResource("/fxml/Home.fxml");
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent homeRoot = loader.load();
            Scene homeScene = new Scene(homeRoot);

            // Ottieni lo stage corrente
            Stage stage = (Stage) exerciseQuestion.getScene().getWindow();
            stage.setScene(homeScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void registerFailure() {
        JSONObject failureData = new JSONObject();
        failureData.put("user", username != null ? username : "defaultUser");
        failureData.put("status", "failed");
        failureData.put("exercisesCompleted", currentExerciseIndex);

        try (FileWriter file = new FileWriter("saves.json")) {
            file.write(failureData.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void showFailureMessage() {
        Alert alert = new Alert(AlertType.INFORMATION, "Hai fallito l'esercizio, stai per tornare alla Home", ButtonType.OK);
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
        if (currentExerciseIndex < currentExercises.size() - 1) {
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

    @FXML
    protected void handleFinishExercise() {
        boolean allCorrect = true;
        for (int i = 0; i < currentExercises.size(); i++) {
            Exercise exercise = currentExercises.get(i);
            int selectedAnswerIndex = -1;
            if (answerA.isSelected()) {
                selectedAnswerIndex = 0;
            } else if (answerB.isSelected()) {
                selectedAnswerIndex = 1;
            } else if (answerC.isSelected()) {
                selectedAnswerIndex = 2;
            }
            if (selectedAnswerIndex != exercise.getCorrectAnswerIndex()) {
                allCorrect = false;
                break;
            }
        }
        if (allCorrect) {
            unlockNextDifficulty();
        } else {
            registerFailure();
            showFailureMessage();
            redirectToHome();
        }
    }
}