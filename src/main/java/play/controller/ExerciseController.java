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
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import play.model.Exercise;
import play.model.ExerciseModel;
import play.model.SessionManager;
import play.model.Timer;

public class ExerciseController {

    protected int currentExerciseIndex = 0;
    protected final ExerciseModel exerciseModel = new ExerciseModel();
    protected boolean[] exerciseResults;
    protected List<Exercise> currentExercises;
    // Modifica: campo statico per mantenere il livello aggiornato tra le istanze
    protected static String currentDifficulty = "principiante";
    protected String username; // campo per lo username

    // Aggiungiamo un array per salvare l'indice della risposta scelta per ogni domanda (inizialmente -1)
    protected int[] userAnswers;
    
    // Timer fields
    private Timer timer;

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
    @FXML
    protected Label timerLabel;

    public ExerciseController() {

    }    @FXML
    public void initialize() {
        ToggleGroup group = new ToggleGroup();
        answerA.setToggleGroup(group);
        answerB.setToggleGroup(group);
        answerC.setToggleGroup(group);
        
        // Initialize timer label
        if (timerLabel == null) {
            timerLabel = new Label("Tempo: 00:00");
        }
    }    public void initData(String username) {
        this.username = username;

        // Determina la difficoltà corretta basata sui progressi dell'utente
        currentDifficulty = DifficultyManager.getCurrentDifficulty("TrovaErrore");
        System.out.println("Caricando TrovaErrore livello: " + currentDifficulty);

        loadExercisesByDifficulty(currentDifficulty);
        loadExercise(currentExerciseIndex);
        
        // Start timer when exercise begins
        timer = new Timer(timerLabel);
        timer.startTimer();
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
        // Controlla se la lista è vuota o l'indice è fuori range
        if (currentExercises == null || currentExercises.isEmpty()) {
            System.out.println("Nessun esercizio disponibile per la difficoltà: " + currentDifficulty);
            // Se non ci sono esercizi, avvisa l'utente e torna alla Home
            Alert alert = new Alert(AlertType.INFORMATION, 
                "Non ci sono esercizi disponibili per il livello " + currentDifficulty + ". Tornerai alla Home.", 
                ButtonType.OK);
            alert.showAndWait();
            redirectToHome();
            return;
        }
        
        if (index < 0 || index >= currentExercises.size()) {
            System.out.println("Indice esercizio fuori range: " + index);
            return;
        }
        
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
        if ("principiante".equals(currentDifficulty)) {
            currentDifficulty = "intermedio";
        } else if ("intermedio".equals(currentDifficulty)) {
            currentDifficulty = "esperto";
        }
        // Carica i nuovi esercizi per la nuova difficoltà
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
    }    protected void registerOutcome(boolean success) {
        // Stop the timer when exercise is completed
        long timeInSeconds = 0;
        if (timer != null) {
            timer.stopTimer();
            timeInSeconds = timer.getElapsedTimeInSeconds();
        }
        
        // Dati riguardanti il risultato dell'esercizio
        JSONObject newOutcome = new JSONObject();
        newOutcome.put("risultato", success ? "success" : "failed");
        newOutcome.put("tempo", timeInSeconds);

        String exerciseName = "TrovaErrore";
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
            registerOutcome(true);
            // Aggiorna la difficoltà prima del redirect alla Home
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