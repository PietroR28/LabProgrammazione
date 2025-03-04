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
    protected String username; // campo per lo username

    // Aggiungiamo un array per salvare l'indice della risposta scelta per ogni domanda (inizialmente -1)
    protected int[] userAnswers;

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
    protected Button finishExerciseButton;

    public ExerciseController() {
        loadExercisesByDifficulty(currentDifficulty);
    }

    // Metodo per impostare lo username
    public void setUsername(String username) {
        this.username = username;
    }

    @FXML
    public void initialize() {
        loadExercise(currentExerciseIndex);
    }

    protected void loadExercisesByDifficulty(String difficulty) {
        currentExercises = exerciseModel.getExercisesByDifficulty(difficulty);
        // Inizializziamo l'array con -1 per indicare che nessuna risposta è stata scelta
        userAnswers = new int[currentExercises.size()];
        for (int i = 0; i < userAnswers.length; i++) {
            userAnswers[i] = -1;
        }
    }

    protected void loadExercise(int index) {
        Exercise exercise = currentExercises.get(index);
        System.out.println("Loading exercise " + index);
        // Setta la domanda, il codice e le risposte
        exerciseQuestion.getChildren().clear();
        exerciseQuestion.getChildren().add(new Text(exercise.getQuestion()));
        exerciseCode.getChildren().clear();
        exerciseCode.getChildren().add(new Text(exercise.getCode()));
        answerA.setText(exercise.getAnswers()[0]);
        answerB.setText(exercise.getAnswers()[1]);
        answerC.setText(exercise.getAnswers()[2]);

        // Deseleziona i RadioButton
        answerA.setSelected(false);
        answerB.setSelected(false);
        answerC.setSelected(false);

        // Se l'utente aveva già selezionato una risposta, la ripristina
        if (userAnswers[index] != -1) {
            switch (userAnswers[index]) {
                case 0:
                    answerA.setSelected(true);
                    break;
                case 1:
                    answerB.setSelected(true);
                    break;
                case 2:
                    answerC.setSelected(true);
                    break;
            }
        }

        // Mostra il pulsante "Concludi Esercizio" solo se è l'ultimo esercizio
        if (index == currentExercises.size() - 1) {
            finishExerciseButton.setVisible(true);
        } else {
            finishExerciseButton.setVisible(false);
        }
    }

    // Metodo per salvare la risposta attuale
    protected void saveCurrentAnswer() {
        int selectedAnswerIndex = -1;
        if (answerA.isSelected()) {
            selectedAnswerIndex = 0;
        } else if (answerB.isSelected()) {
            selectedAnswerIndex = 1;
        } else if (answerC.isSelected()) {
            selectedAnswerIndex = 2;
        }
        userAnswers[currentExerciseIndex] = selectedAnswerIndex;
    }

    protected void unlockNextDifficulty() {
        registerOutcome(true);
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

            Stage stage = (Stage) exerciseQuestion.getScene().getWindow();
            stage.setScene(homeScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Metodo per registrare il risultato (success o failed) in saves.json
    protected void registerOutcome(boolean success) {
        JSONObject outcomeData = new JSONObject();
        outcomeData.put("username", username != null ? username : "defaultUser");
        outcomeData.put("nomeEsercizio", "TrovaErrore");
        outcomeData.put("gradoDifficolta", currentDifficulty);
        outcomeData.put("risultato", success ? "success" : "failed");

        try (FileWriter file = new FileWriter("saves.json")) {
            file.write(outcomeData.toString(4)); // Pretty printing con indentazione di 4 spazi
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void handlePreviousQuestion() {
        saveCurrentAnswer();
        if (currentExerciseIndex > 0) {
            currentExerciseIndex--;
            loadExercise(currentExerciseIndex);
        }
    }

    @FXML
    protected void handleNextQuestion() {
        saveCurrentAnswer();
        if (currentExerciseIndex < currentExercises.size() - 1) {
            currentExerciseIndex++;
            loadExercise(currentExerciseIndex);
        }
    }

    @FXML
    protected void handleExitExercise() {
        saveCurrentAnswer();
        registerOutcome(false);
        Alert alert = new Alert(AlertType.INFORMATION, "Hai fallito l'esercizio, stai per tornare alla Home", ButtonType.OK);
        alert.showAndWait();
        redirectToHome();
    }

    @FXML
    protected void handleFinishExercise() {
        // Salva la risposta corrente prima di controllare il risultato
        saveCurrentAnswer();
        boolean allCorrect = true;
        for (int i = 0; i < currentExercises.size(); i++) {
            Exercise exercise = currentExercises.get(i);
            if (userAnswers[i] != exercise.getCorrectAnswerIndex()) {
                allCorrect = false;
                break;
            }
        }
        if (allCorrect) {
            unlockNextDifficulty();
        } else {
            registerOutcome(false);
            Alert alert = new Alert(AlertType.INFORMATION, "Hai fallito l'esercizio, stai per tornare alla Home", ButtonType.OK);
            alert.showAndWait();
            redirectToHome();
        }
    }
}