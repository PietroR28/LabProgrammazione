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
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import play.model.Exercise;
import play.model.SessionManager;
import play.model.Timer;

/**
 * Classe base astratta per tutti i controller di esercizi principali.
 * Implementa il Template Method Pattern per fornire struttura comune
 * e permettere specializzazione nelle sottoclassi.
 */
public abstract class BaseExerciseController {
    
    // Campi comuni a tutti i tipi di esercizi
    protected int currentExerciseIndex = 0;
    protected List<Exercise> currentExercises;
    protected static String currentDifficulty = "principiante";
    protected String username;
    protected Timer timer;
    
    @FXML
    protected Label timerLabel;

    // Template Method - definisce la struttura generale dell'inizializzazione
    public final void initData(String username) {
        this.username = username;
        
        // Determina la difficoltà corretta basata sui progressi dell'utente
        currentDifficulty = DifficultyManager.getCurrentDifficulty(getExerciseType());
        System.out.println("Caricando " + getExerciseType() + " livello: " + currentDifficulty);
        
        loadExercisesByDifficulty(currentDifficulty);
        
        // Inizializza le strutture dati specifiche del tipo di esercizio
        initializeExerciseSpecificData();
        
        loadExercise(currentExerciseIndex);
        
        // Avvia il timer
        startTimer();
    }
    
    // Template Method - definisce la struttura generale per concludere un esercizio
    public final void finishExercise() {
        saveCurrentAnswer();
        
        if (currentExercises == null || currentExercises.isEmpty()) {
            redirectToHome();
            return;
        }
        
        boolean allCorrect = checkAllAnswers();
        
        if (allCorrect) {
            registerOutcome(true);
            unlockNextDifficulty();
            showSuccessMessage();
            redirectToHome();
        } else {
            registerOutcome(false);
            showFailureMessage();
            redirectToHome();
        }
    }
    
    // Metodi astratti che devono essere implementati dalle sottoclassi
    protected abstract String getExerciseType();
    protected abstract void loadExercisesByDifficulty(String difficulty);
    protected abstract void initializeExerciseSpecificData();
    protected abstract void loadExercise(int index);
    protected abstract void saveCurrentAnswer();
    protected abstract boolean checkAllAnswers();
    
    // Metodi con implementazione di default che possono essere override
    protected void startTimer() {
        if (timerLabel != null) {
            timer = new Timer(timerLabel);
            timer.startTimer();
        }
    }
    
    protected void showSuccessMessage() {
        Alert alert = new Alert(AlertType.INFORMATION, "Complimenti, hai superato il livello", ButtonType.OK);
        alert.showAndWait();
    }
    
    protected void showFailureMessage() {
        Alert alert = new Alert(AlertType.INFORMATION, "Hai fallito l'esercizio, stai per tornare alla Home", ButtonType.OK);
        alert.showAndWait();
    }
    
    // Metodi comuni con implementazione concreta
    protected final void unlockNextDifficulty() {
        if ("principiante".equals(currentDifficulty)) {
            currentDifficulty = "intermedio";
        } else if ("intermedio".equals(currentDifficulty)) {
            currentDifficulty = "esperto";
        }
        
        loadExercisesByDifficulty(currentDifficulty);
        currentExerciseIndex = 0;
        loadExercise(currentExerciseIndex);
    }
    
    protected final void redirectToHome() {
        try {
            URL fxmlLocation = getClass().getResource("/fxml/Home.fxml");
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent homeRoot = loader.load();
            Scene homeScene = new Scene(homeRoot);

            // Ottieni il controller della Home e aggiorna i progressi
            HomeController controller = loader.getController();
            String username = SessionManager.getUsername();
            if (username != null && !username.trim().isEmpty()) {
                controller.setWelcomeMessage(username);
            }

            Stage stage = getStage();
            stage.setScene(homeScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    protected final void registerOutcome(boolean success) {
        // Stop del timer
        long timeInSeconds = 0;
        if (timer != null) {
            timer.stopTimer();
            timeInSeconds = timer.getElapsedTimeInSeconds();
        }
        
        // Controllo username
        if (username == null || username.trim().isEmpty()) {
            username = SessionManager.getUsername();
            if (username == null || username.trim().isEmpty()) {
                username = "user";
            }
        }

        JSONObject newOutcome = new JSONObject();
        newOutcome.put("risultato", success ? "success" : "failed");
        newOutcome.put("tempo", timeInSeconds);

        String exerciseName = getExerciseType();
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
            writer.write(savesData.toString(2));
        } catch (IOException e) {
            System.err.println("Errore nella scrittura del file saves.json: " + e.getMessage());
        }
    }
    
    // Metodi di navigazione comuni
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
        finishExercise();
    }
    
    // Metodo helper per ottenere lo Stage corrente
    protected abstract Stage getStage();
}
