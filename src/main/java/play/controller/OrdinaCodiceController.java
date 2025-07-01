package play.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
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
import play.model.Exercise;
import play.model.OrdinaCodiceExerciseModel;

public class OrdinaCodiceController extends BaseExerciseController {

    private final OrdinaCodiceExerciseModel exerciseModel = new OrdinaCodiceExerciseModel();
    
    // Lista che memorizza l'ordinamento corrente creato dall'utente
    private List<Integer> currentUserOrder = new ArrayList<>();

    @FXML
    private VBox codeContainer;
    
    // Aggiunti per compatibilità con BaseExerciseController
    @FXML
    protected javafx.scene.control.Label exerciseQuestion;
    @FXML
    protected javafx.scene.control.Button finishExerciseButton;
    @FXML
    protected javafx.scene.control.Label resultLabel;
    
    private ListView<String> codeListView;

    public OrdinaCodiceController() {
        super();
    }

    @Override
    protected String getExerciseType() {
        return "OrdinaCodice";
    }

    @Override
    protected void loadExercisesByDifficulty(String difficulty) {
        List<Exercise> exercises = exerciseModel.getOrdinaCodiceExercisesByDifficulty(difficulty);
        currentExercises = new ArrayList<>(exercises);
    }

    @Override
    protected void initializeExerciseSpecificData() {
        setupCodeListView();
    }

    @Override
    protected boolean checkAllAnswers() {
        // Verifica i risultati di tutti gli esercizi
        for (int i = 0; i < currentExercises.size(); i++) {
            if (!checkSingleExerciseResult(i)) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void saveCurrentAnswer() {
        // L'ordinamento corrente è già salvato in currentUserOrder
        // Non c'è bisogno di fare nulla di specifico qui per OrdinaCodice
    }

    @Override
    protected javafx.stage.Stage getStage() {
        return (javafx.stage.Stage) codeContainer.getScene().getWindow();
    }

    @Override
    protected void loadExercise(int index) {
        if (index < 0 || index >= currentExercises.size()) return;

        Exercise exercise = currentExercises.get(index); // Cast necessario per accedere ai metodi specifici

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

    private void setupCodeListView() {
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

    private boolean checkSingleExerciseResult(int exerciseIndex) {
        Exercise exercise = currentExercises.get(exerciseIndex);
        int[] correctOrder = exercise.getCorrectOrder();

        // Salva l'esercizio corrente e carica quello da verificare
        int savedIndex = currentExerciseIndex;
        currentExerciseIndex = exerciseIndex;
        loadExercise(exerciseIndex);

        // Confronta l'ordine dell'utente con l'ordine corretto
        boolean isCorrect = true;
        if (correctOrder.length != currentUserOrder.size()) {
            isCorrect = false;
        } else {
            for (int i = 0; i < correctOrder.length; i++) {
                if (correctOrder[i] != currentUserOrder.get(i)) {
                    isCorrect = false;
                    break;
                }
            }
        }

        // Ripristina l'esercizio corrente
        currentExerciseIndex = savedIndex;
        return isCorrect;
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
}