package play.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import play.model.Exercise;
import play.model.ExerciseModel;

/**
 * Controller che si occupa della gestione degli esercizi di tipo "Trova l'errore".
 *
 * Questa classe estende la classe astratta BaseExerciseController e implementa
 * la logica necessaria per:
 * - Caricare e visualizzare domande e risposte degli esercizi;
 * - Gestire la selezione delle risposte da parte dell'utente tramite i RadioButton;
 * - Salvare le risposte date;
 * - Verificare la correttezza delle risposte fornite;
 * - Gestire il ritorno alla schermata principale in caso di errori o fine esercizio.
 *
 * Utilizza ExerciseModel per recuperare gli esercizi in base alla difficoltà selezionata.
 */
public class TrovaErroreController extends BaseExerciseController {

    // Modello per il recupero degli esercizi dal file exercises.json
    private final ExerciseModel exerciseModel = new ExerciseModel();
    
    // Array che salva l'indice della risposta scelta per ogni domanda (-1 = nessuna risposta)
    private int[] userAnswers;

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

    // Inizializzazione del controller, associa i RadioButton allo stesso gruppo
    @FXML
    public void initialize() {
        ToggleGroup group = new ToggleGroup();
        answerA.setToggleGroup(group);
        answerB.setToggleGroup(group);
        answerC.setToggleGroup(group);
    }

    // Restituisce il tipo di esercizio "Trova l'errore"
    @Override
    protected String getExerciseType() {
        return "TrovaErrore";
    }

    // Carica gli esercizi in base alla difficoltà raggiunta
    @Override
    protected void loadExercisesByDifficulty(String difficulty) {
        currentExercises = exerciseModel.getExercisesByDifficulty(difficulty);
    }

    // Inizializza l'array delle risposte utente a -1 (nessuna risposta data)
    @Override
    protected void initializeExerciseSpecificData() {
        if (currentExercises != null) {
            userAnswers = new int[currentExercises.size()];
            for (int i = 0; i < userAnswers.length; i++) {
                userAnswers[i] = -1;
            }
        }
    }

    // Carica la domanda e le risposte dell'esercizio all'indice specificato
    @Override
    protected void loadExercise(int index) {
        // Controlla se la lista è vuota o l'indice è fuori range
        if (currentExercises == null || currentExercises.isEmpty()) {
            System.out.println("Nessun esercizio disponibile per la difficoltà: " + currentDifficulty);
            redirectToHome();
            return;
        }
        
        if (index < 0 || index >= currentExercises.size()) {
            System.out.println("Indice esercizio fuori range: " + index);
            return;
        }
        
        Exercise exercise = currentExercises.get(index);
        System.out.println("Loading exercise " + index);
        
        // Imposta la domanda e il codice nell'interfaccia
        exerciseQuestion.getChildren().clear();
        exerciseQuestion.getChildren().add(new Text(exercise.getQuestion()));
        exerciseCode.getChildren().clear();
        exerciseCode.getChildren().add(new Text(exercise.getCode()));
        answerA.setText(exercise.getAnswers()[0]);
        answerB.setText(exercise.getAnswers()[1]);
        answerC.setText(exercise.getAnswers()[2]);

        // Deseleziona tutti i RadioButton
        answerA.setSelected(false);
        answerB.setSelected(false);
        answerC.setSelected(false);

        // Se l'utente aveva già selezionato una risposta, la ripristina
        if (userAnswers != null && userAnswers[index] != -1) {
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

    // Salva la risposta selezionata dall'utente per la domanda corrente
    @Override
    protected void saveCurrentAnswer() {
        if (userAnswers == null) return;
        
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

    // Verifica se tutte le risposte date sono corrette
    @Override
    protected boolean checkAllAnswers() {
        if (userAnswers == null || currentExercises == null) return false;
        
        for (int i = 0; i < currentExercises.size(); i++) {
            Exercise exercise = currentExercises.get(i);
            if (userAnswers[i] != exercise.getCorrectAnswerIndex()) {
                return false;
            }
        }
        return true;
    }

    // Restituisce lo Stage corrente (finestra attiva)
    @Override
    protected Stage getStage() {
        return (Stage) exerciseQuestion.getScene().getWindow();
    }
}