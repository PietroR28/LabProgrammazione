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

/**
 * Controller per gli esercizi di ordinamento codice.
 * Permette all'utente di trascinare righe di codice per ordinarle correttamente.
 */
public class OrdinaCodiceController extends BaseExerciseController {

    private final OrdinaCodiceExerciseModel exerciseModel = new OrdinaCodiceExerciseModel();

    // Lista che tiene traccia dell'ordine corrente creato dall'utente (molto importante!)
    private List<Integer> currentUserOrder = new ArrayList<>();

    @FXML
    private VBox codeContainer;

    @FXML
    protected javafx.scene.control.Label exerciseQuestion;
    @FXML
    protected javafx.scene.control.Button finishExerciseButton;
    @FXML
    protected javafx.scene.control.Label resultLabel;

    // La lista visibile dove l'utente trascina le righe di codice
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
        // Carica tutti gli esercizi di ordinamento per la difficoltà scelta
        List<Exercise> exercises = exerciseModel.getOrdinaCodiceExercisesByDifficulty(difficulty);
        currentExercises = new ArrayList<>(exercises);
    }

    @Override
    protected void initializeExerciseSpecificData() {
        // Crea la lista dove l'utente trascinerà le righe di codice
        setupCodeListView();
    }

    @Override
    protected boolean checkAllAnswers() {
        // Verifica se l'utente ha ordinato correttamente le righe
        return checkCurrentExerciseResult();
    }

    @Override
    protected void saveCurrentAnswer() {
        // L'ordinamento è già salvato automaticamente in currentUserOrder
    }

    @Override
    protected javafx.stage.Stage getStage() {
        return (javafx.stage.Stage) codeContainer.getScene().getWindow();
    }

    @Override
    protected void loadExercise(int index) {
        if (index < 0 || index >= currentExercises.size()) return;

        Exercise exercise = currentExercises.get(index);
        exerciseQuestion.setText(exercise.getQuestion());

        // Dividi il codice in singole righe
        String[] codeLines = exercise.getCode().split("\n");

        // Crea un ordine iniziale mescolato per confondere l'utente
        List<Integer> initialOrder = new ArrayList<>();
        for (int i = 0; i < codeLines.length; i++) {
            initialOrder.add(i);
        }
        Collections.shuffle(initialOrder); // Mescola le righe!

        // Salva l'ordine mescolato come punto di partenza dell'utente
        currentUserOrder.clear();
        for (Integer i : initialOrder) {
            currentUserOrder.add(i);
        }

        // Mostra le righe di codice nell'ordine mescolato
        ObservableList<String> items = FXCollections.observableArrayList();
        for (int i : initialOrder) {
            items.add(codeLines[i]);
        }
        codeListView.setItems(items);

        finishExerciseButton.setVisible(index == currentExercises.size() - 1);
        resultLabel.setVisible(false);
    }

    /**
     * Crea la lista dove l'utente può trascinare le righe di codice per riordinarle
     */
    private void setupCodeListView() {
        codeListView = new ListView<>();
        codeListView.getStyleClass().add("code-area");
        codeListView.setStyle("-fx-font-family: 'Consolas', 'Monaco', monospace; -fx-font-size: 14px;");
        codeListView.setPrefHeight(300);

        codeContainer.getChildren().add(codeListView);

        // Configura ogni riga per essere trascinabile
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
                        // Colori alternati per le righe (zebra striping)
                        setBackground(new Background(new BackgroundFill(
                                getIndex() % 2 == 0 ? Color.web("#f8f9fa") : Color.web("#e9ecef"),
                                CornerRadii.EMPTY,
                                Insets.EMPTY)));
                    }
                }
            };

            // DRAG AND DROP - Quando l'utente inizia a trascinare una riga
            cell.setOnDragDetected(event -> {
                if (!cell.isEmpty()) {
                    Dragboard db = cell.startDragAndDrop(TransferMode.MOVE);
                    ClipboardContent content = new ClipboardContent();
                    content.putString(String.valueOf(cell.getIndex())); // Salva quale riga sta trascinando
                    db.setContent(content);
                    event.consume();
                }
            });

            // Permette di rilasciare su questa cella
            cell.setOnDragOver(event -> {
                if (event.getGestureSource() != cell && event.getDragboard().hasString()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                event.consume();
            });

            // Evidenzia la cella quando ci passi sopra trascinando
            cell.setOnDragEntered(event -> {
                if (event.getGestureSource() != cell && event.getDragboard().hasString()) {
                    cell.setBackground(new Background(new BackgroundFill(
                            Color.web("#cce5ff"), CornerRadii.EMPTY, Insets.EMPTY)));
                }
                event.consume();
            });

            // Rimuove l'evidenziazione quando esci dalla cella
            cell.setOnDragExited(event -> {
                cell.setBackground(new Background(new BackgroundFill(
                        cell.getIndex() % 2 == 0 ? Color.web("#f8f9fa") : Color.web("#e9ecef"),
                        CornerRadii.EMPTY,
                        Insets.EMPTY)));
                event.consume();
            });

            // PARTE CRUCIALE - Quando l'utente rilascia una riga trascinata
            cell.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasString()) {
                    int draggedIdx = Integer.parseInt(db.getString()); // Quale riga era trascinata
                    int targetIdx = cell.getIndex(); // Dove è stata rilasciata

                    if (draggedIdx != targetIdx) {
                        // Sposta la riga nella vista
                        ObservableList<String> items = codeListView.getItems();
                        String draggedItem = items.get(draggedIdx);
                        items.remove(draggedIdx);
                        items.add(targetIdx, draggedItem);

                        // IMPORTANTISSIMO: Aggiorna anche l'ordine dell'utente!
                        // Senza questo, il controllo finale fallirebbe sempre
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

    /**
     * Verifica se l'utente ha ordinato correttamente le righe di codice.
     * Confronta l'ordine corrente dell'utente con quello corretto dal JSON.
     */
    private boolean checkCurrentExerciseResult() {
        if (currentExerciseIndex < 0 || currentExerciseIndex >= currentExercises.size()) {
            return false;
        }

        Exercise exercise = currentExercises.get(currentExerciseIndex);
        int[] correctOrder = exercise.getCorrectOrder(); // Ordine corretto dal JSON

        // Verifica che abbiano la stessa lunghezza
        if (correctOrder.length != currentUserOrder.size()) {
            System.out.println("DEBUG: Lunghezze diverse - Corretto: " + correctOrder.length + ", Utente: " + currentUserOrder.size());
            return false;
        }

        // Controlla ogni posizione: deve essere identica
        for (int i = 0; i < correctOrder.length; i++) {
            if (correctOrder[i] != currentUserOrder.get(i)) {
                System.out.println("DEBUG: Differenza alla posizione " + i + " - Corretto: " + correctOrder[i] + ", Utente: " + currentUserOrder.get(i));
                return false;
            }
        }

        System.out.println("DEBUG: Ordine corretto! Utente: " + currentUserOrder + ", Corretto: " + java.util.Arrays.toString(correctOrder));
        return true;
    }

    // Pulsanti per spostare su/giù (alternativa al drag&drop)
    @FXML
    protected void handleMoveUp() {
        int selectedIndex = codeListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex > 0) {
            // Sposta nella vista
            ObservableList<String> items = codeListView.getItems();
            String selectedItem = items.get(selectedIndex);
            items.remove(selectedIndex);
            items.add(selectedIndex - 1, selectedItem);

            // Sposta anche nell'ordine dell'utente
            int movedItem = currentUserOrder.get(selectedIndex);
            currentUserOrder.remove(selectedIndex);
            currentUserOrder.add(selectedIndex - 1, movedItem);

            codeListView.getSelectionModel().select(selectedIndex - 1);
        }
    }

    @FXML
    protected void handleMoveDown() {
        int selectedIndex = codeListView.getSelectionModel().getSelectedIndex();
        ObservableList<String> items = codeListView.getItems();
        if (selectedIndex >= 0 && selectedIndex < items.size() - 1) {
            // Sposta nella vista
            String selectedItem = items.get(selectedIndex);
            items.remove(selectedIndex);
            items.add(selectedIndex + 1, selectedItem);

            // Sposta anche nell'ordine dell'utente
            int movedItem = currentUserOrder.get(selectedIndex);
            currentUserOrder.remove(selectedIndex);
            currentUserOrder.add(selectedIndex + 1, movedItem);

            codeListView.getSelectionModel().select(selectedIndex + 1);
        }
    }
}