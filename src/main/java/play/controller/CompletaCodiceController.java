package play.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import play.model.Exercise;
import play.model.ExerciseModel;
import play.model.SessionManager;
import play.model.Timer;

public class CompletaCodiceController {

    protected int currentExerciseIndex = 0;
    protected final ExerciseModel exerciseModel = new ExerciseModel();
    protected List<Exercise> currentExercises;
    protected static String currentDifficulty = "principiante";
    protected String username;
    protected String[] userAnswers;
    
    // Timer field
    private Timer timer;

    @FXML
    protected TextFlow exerciseQuestion;
    @FXML
    protected TextFlow exerciseCode;
    @FXML
    protected TextField codeInput;
    @FXML
    protected Button finishExerciseButton;
    @FXML
    protected Label timerLabel;

    public CompletaCodiceController() {

    }

    @FXML
    public void initialize() {
        // Initialize timer label
        if (timerLabel == null) {
            timerLabel = new Label("Tempo: 00:00");
        }
    }

    public void initData(String username) {
        this.username = username;

        // Determina la difficoltà corretta basata sui progressi dell'utente
        currentDifficulty = DifficultyManager.getCurrentDifficulty("CompletaCodice");
        System.out.println("Caricando CompletaCodice livello: " + currentDifficulty);

        loadExercisesByDifficulty(currentDifficulty);
        loadExercise(currentExerciseIndex);
        
        // Start timer when exercise begins
        timer = new Timer(timerLabel);
        timer.startTimer();
    }

    protected void loadExercisesByDifficulty(String difficulty) {
        currentExercises = exerciseModel.getCompletaCodiceExercisesByDifficulty(difficulty);
        userAnswers = new String[currentExercises.size()];
        for (int i = 0; i < userAnswers.length; i++) {
            userAnswers[i] = "";
        }
    }

    protected void loadExercise(int index) {
        if (index < 0 || index >= currentExercises.size()) return;
        
        Exercise exercise = currentExercises.get(index);
        
        exerciseQuestion.getChildren().clear();
        exerciseQuestion.getChildren().add(new Text(exercise.getQuestion()));
        
        exerciseCode.getChildren().clear();
        exerciseCode.getChildren().add(new Text(exercise.getCode()));
        
        // Ripristina la risposta dell'utente se presente
        codeInput.setText(userAnswers[index]);
        
        // Mostra il pulsante "Concludi Esercizio" solo se è l'ultimo esercizio
        finishExerciseButton.setVisible(index == currentExercises.size() - 1);
    }

    protected void saveCurrentAnswer() {
        if (currentExerciseIndex < 0 || currentExerciseIndex >= userAnswers.length) return;
        userAnswers[currentExerciseIndex] = codeInput.getText();
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

            // IMPORTANTE: Ottieni il controller della Home e aggiorna i progressi
            HomeController controller = loader.getController();
            String username = SessionManager.getUsername();
            if (username != null && !username.trim().isEmpty()) {
                controller.setWelcomeMessage(username);
            }

            Stage stage = (Stage) exerciseQuestion.getScene().getWindow();
            stage.setScene(homeScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void registerOutcome(boolean success) {
        // Stop the timer when exercise is completed
        long timeInSeconds = 0;
        if (timer != null) {
            timer.stopTimer();
            timeInSeconds = timer.getElapsedTimeInSeconds();
        }
        
        JSONObject newOutcome = new JSONObject();
        newOutcome.put("risultato", success ? "success" : "failed");
        newOutcome.put("tempo", timeInSeconds); // Add time to the outcome
    
        String exerciseName = "CompletaCodice";
        JSONObject savesData;
        File file = new File("saves.json");
    
        if (file.exists()) {
            try {
                String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                content = content.trim();
                
                if (content.isEmpty() || content.equals("{}")) {
                    savesData = new JSONObject();
                } else {
                    savesData = new JSONObject(content);
                }
            } catch (IOException | org.json.JSONException e) {
                System.err.println("Errore nella lettura di saves.json, creazione nuovo file: " + e.getMessage());
                savesData = new JSONObject();
            }
        } else {
            savesData = new JSONObject();
        }
    
        JSONObject userSavesObj;
        if (savesData.has(username)) {
            userSavesObj = savesData.getJSONObject(username);
        } else {
            userSavesObj = new JSONObject();
        }
    
        JSONObject exerciseSavesObj;
        if (userSavesObj.has(exerciseName)) {
            exerciseSavesObj = userSavesObj.getJSONObject(exerciseName);
        } else {
            exerciseSavesObj = new JSONObject();
        }
    
        // Sovrascrive il risultato per il grado di difficoltà corrente
        exerciseSavesObj.put(currentDifficulty, newOutcome);
        userSavesObj.put(exerciseName, exerciseSavesObj);
        savesData.put(username, userSavesObj);
    
        try (FileWriter writer = new FileWriter(file)) {
            // Scrivi il JSON con indentazione per renderlo più leggibile
            writer.write(savesData.toString(2));
        } catch (IOException e) {
            System.err.println("Errore nella scrittura del file saves.json: " + e.getMessage());
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
        Alert alert = new Alert(AlertType.INFORMATION, 
                              "Hai interrotto l'esercizio, stai per tornare alla Home", 
                              ButtonType.OK);
        alert.showAndWait();
        redirectToHome();
    }

    @FXML
    protected void handleFinishExercise() {
        saveCurrentAnswer();
        
        if (currentExercises.isEmpty()) {
            redirectToHome();
            return;
        }
        
        boolean allCorrect = true;
        for (int i = 0; i < currentExercises.size(); i++) {
            Exercise exercise = currentExercises.get(i);
            String userAnswer = userAnswers[i].trim();
            String correctAnswer = exercise.getAnswers()[0].trim();
            
            if (!userAnswer.equals(correctAnswer)) {
                allCorrect = false;
                break;
            }
        }
        
        if (allCorrect) {
            registerOutcome(true);
            unlockNextDifficulty();
            Alert alert = new Alert(AlertType.INFORMATION, "Complimenti, hai superato il livello", ButtonType.OK);
            alert.showAndWait();
            redirectToHome();
        } else {
            registerOutcome(false);
            Alert alert = new Alert(AlertType.INFORMATION, "Hai fallito l'esercizio, stai per tornare alla Home", ButtonType.OK);
            alert.showAndWait();
            redirectToHome();
        }
    }
}