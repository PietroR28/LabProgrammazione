package play.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import play.model.Exercise;
import play.model.OrdinaCodiceExerciseModel;
import play.model.SessionManager;
import play.model.Timer;

public class OrdinaCodiceController {

    protected int currentExerciseIndex = 0;
    protected final OrdinaCodiceExerciseModel exerciseModel = new OrdinaCodiceExerciseModel();
    protected List<Exercise> currentExercises;
    protected static String currentDifficulty = "principiante";
    protected String username;
    protected int selectedLineIndex = -1;
    
    // Timer field
    private Timer timer;

    // Lista che memorizza l'ordinamento corrente creato dall'utente
    protected List<Integer> currentUserOrder = new ArrayList<>();

    @FXML
    protected Label exerciseQuestion;
    @FXML
    protected VBox codeContainer;
    @FXML
    protected Button finishExerciseButton;
    @FXML
    protected Label resultLabel;
    @FXML
    protected Label timerLabel;

    protected ListView<String> codeListView;

    public OrdinaCodiceController() {

    }

    @FXML
    public void initialize() {
        // Initialize timer label
        if (timerLabel == null) {
            timerLabel = new Label("Tempo: 00:00");
            codeContainer.getChildren().add(0, timerLabel); // Add timer at top of container
        }
        
        // Inizializza la ListView per il codice
        codeListView = new ListView<>();
        codeListView.getStyleClass().add("code-area");
        codeListView.setStyle("-fx-font-family: 'Consolas', 'Monaco', monospace; -fx-font-size: 14px;");
        codeListView.setPrefHeight(300);

        // Aggiungi la ListView al container
        codeContainer.getChildren().add(codeListView);

        // Configura la visualizzazione delle celle
        codeListView.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setBackground(null);
                    } else {
                        setText(item);
                        setBackground(new Background(new BackgroundFill(
                                getIndex() % 2 == 0 ? Color.web("#f8f9fa") : Color.web("#e9ecef"),
                                CornerRadii.EMPTY,
                                Insets.EMPTY)));
                    }
                }
            };

            // Configura drag and drop
            cell.setOnDragDetected(event -> {
                if (!cell.isEmpty()) {
                    Dragboard db = cell.startDragAndDrop(TransferMode.MOVE);
                    ClipboardContent content = new ClipboardContent();
                    content.putString(String.valueOf(cell.getIndex()));
                    db.setContent(content);
                    selectedLineIndex = cell.getIndex();
                    event.consume();
                }
            });

            cell.setOnDragOver(event -> {
                if (event.getGestureSource() != cell && event.getDragboard().hasString()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                event.consume();
            });

            cell.setOnDragEntered(event -> {
                if (event.getGestureSource() != cell && event.getDragboard().hasString()) {
                    cell.setBackground(new Background(new BackgroundFill(
                            Color.web("#cce5ff"), CornerRadii.EMPTY, Insets.EMPTY)));
                }
                event.consume();
            });

            cell.setOnDragExited(event -> {
                cell.setBackground(new Background(new BackgroundFill(
                        cell.getIndex() % 2 == 0 ? Color.web("#f8f9fa") : Color.web("#e9ecef"),
                        CornerRadii.EMPTY,
                        Insets.EMPTY)));
                event.consume();
            });

            cell.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasString()) {
                    int draggedIdx = Integer.parseInt(db.getString());
                    int targetIdx = cell.getIndex();

                    if (draggedIdx != targetIdx) {
                        ObservableList<String> items = codeListView.getItems();
                        String draggedItem = items.get(draggedIdx);
                        items.remove(draggedIdx);
                        items.add(targetIdx, draggedItem);

                        // Aggiorna l'ordinamento dell'utente
                        int movedItem = currentUserOrder.get(draggedIdx);
                        currentUserOrder.remove(draggedIdx);
                        currentUserOrder.add(targetIdx, movedItem);
                    }
                    success = true;
                }
                event.setDropCompleted(success);
                event.consume();
            });

            return cell;
        });
    }

    public void initData(String username) {
        this.username = username;
        // Se username è null, prova a recuperarlo dal SessionManager
        if (this.username == null || this.username.trim().isEmpty()) {
            this.username = SessionManager.getUsername();
        }
        if (this.username == null || this.username.trim().isEmpty()) {
            this.username = "user"; // Default
        }

        // Determina la difficoltà corretta basata sui progressi dell'utente
        currentDifficulty = DifficultyManager.getCurrentDifficulty("OrdinaCodice");
        System.out.println("Caricando OrdinaCodice livello: " + currentDifficulty);

        loadExercisesByDifficulty(currentDifficulty);
        loadExercise(currentExerciseIndex);
        
        // Start timer when exercise begins
        timer = new Timer(timerLabel);
        timer.startTimer();
    }

    protected void loadExercisesByDifficulty(String difficulty) {
        currentExercises = exerciseModel.getOrdinaCodiceExercisesByDifficulty(difficulty);
    }

    protected void loadExercise(int index) {
        if (index < 0 || index >= currentExercises.size()) return;

        Exercise exercise = currentExercises.get(index);

        // Imposta la domanda
        exerciseQuestion.setText(exercise.getQuestion());

        // Ottieni il codice e l'ordine corretto
        String[] codeLines = exercise.getCode().split("\n");

        // Crea un array con le righe in ordine casuale per la presentazione
        List<Integer> initialOrder = new ArrayList<>();
        for (int i = 0; i < codeLines.length; i++) {
            initialOrder.add(i);
        }
        // Mescola l'ordine
        Collections.shuffle(initialOrder);

        // Memorizza l'ordine attuale dell'utente
        currentUserOrder.clear();
        for (Integer i : initialOrder) {
            currentUserOrder.add(i);
        }

        // Popola la ListView con le righe di codice nell'ordine mescolato
        ObservableList<String> items = FXCollections.observableArrayList();
        for (int i : initialOrder) {
            items.add(codeLines[i]);
        }
        codeListView.setItems(items);

        // Mostra il pulsante "Concludi Esercizio" solo se è l'ultimo esercizio
        finishExerciseButton.setVisible(index == currentExercises.size() - 1);

        // Nascondi il label del risultato quando si carica un nuovo esercizio
        resultLabel.setVisible(false);
    }

    @FXML
    protected void handleMoveUp() {
        int selectedIndex = codeListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex > 0) {
            ObservableList<String> items = codeListView.getItems();
            String selectedItem = items.get(selectedIndex);
            items.remove(selectedIndex);
            items.add(selectedIndex - 1, selectedItem);

            // Aggiorna l'ordine dell'utente
            int movedItem = currentUserOrder.get(selectedIndex);
            currentUserOrder.remove(selectedIndex);
            currentUserOrder.add(selectedIndex - 1, movedItem);

            // Seleziona di nuovo la riga spostata
            codeListView.getSelectionModel().select(selectedIndex - 1);
        }
    }

    @FXML
    protected void handleMoveDown() {
        int selectedIndex = codeListView.getSelectionModel().getSelectedIndex();
        ObservableList<String> items = codeListView.getItems();
        if (selectedIndex >= 0 && selectedIndex < items.size() - 1) {
            String selectedItem = items.get(selectedIndex);
            items.remove(selectedIndex);
            items.add(selectedIndex + 1, selectedItem);

            // Aggiorna l'ordine dell'utente
            int movedItem = currentUserOrder.get(selectedIndex);
            currentUserOrder.remove(selectedIndex);
            currentUserOrder.add(selectedIndex + 1, movedItem);

            // Seleziona di nuovo la riga spostata
            codeListView.getSelectionModel().select(selectedIndex + 1);
        }
    }

    protected boolean checkExerciseResult() {
        Exercise exercise = currentExercises.get(currentExerciseIndex);
        int[] correctOrder = exercise.getCorrectOrder();

        // Confronta l'ordine dell'utente con l'ordine corretto
        boolean isCorrect = true;
        if (correctOrder.length != currentUserOrder.size()) {
            return false;
        }

        for (int i = 0; i < correctOrder.length; i++) {
            if (correctOrder[i] != currentUserOrder.get(i)) {
                isCorrect = false;
                break;
            }
        }

        return isCorrect;
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
        
        // Controllo aggiuntivo per evitare NullPointerException
        if (username == null || username.trim().isEmpty()) {
            username = SessionManager.getUsername();
            if (username == null || username.trim().isEmpty()) {
                username = "user"; // Default fallback
            }
        }

        JSONObject newOutcome = new JSONObject();
        newOutcome.put("risultato", success ? "success" : "failed");
        newOutcome.put("tempo", timeInSeconds); // Add time to the outcome

        String exerciseName = "OrdinaCodice";
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
        registerOutcome(false);
        Alert alert = new Alert(AlertType.INFORMATION,
                "Hai interrotto l'esercizio, stai per tornare alla Home",
                ButtonType.OK);
        alert.showAndWait();
        redirectToHome();
    }

    @FXML
    protected void handleFinishExercise() {
        boolean allCorrect = true;

        // Se non ci sono esercizi, torna alla Home
        if (currentExercises == null || currentExercises.isEmpty()) {
            redirectToHome();
            return;
        }

        // Verifica i risultati di tutti gli esercizi
        for (int i = 0; i < currentExercises.size(); i++) {
            currentExerciseIndex = i;
            loadExercise(i); // Ricarica l'esercizio per ottenere l'ordine corretto
            if (!checkExerciseResult()) {
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