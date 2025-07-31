package play.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import play.model.Exercise;
import play.model.ExerciseModel;

/**
 * Controller principale per gli esercizi "Completa Codice".
 * Gestisce l'interfaccia di esecuzione: mostra domande/codice, raccoglie risposte utente,
 * gestisce la navigazione tra esercizi e verifica le risposte alla fine.
 */
public class CompletaCodiceController extends BaseExerciseController {

    private final ExerciseModel exerciseModel = new ExerciseModel();
    private String[] userAnswers;
    
    @FXML
    private TextFlow exerciseQuestion;
    @FXML
    private TextFlow exerciseCode;
    @FXML
    private TextField codeInput;
    @FXML
    private Button finishExerciseButton;

    @FXML
    public void initialize() {
        // Inizializzazione specifica se necessaria
    }

    @Override
    protected String getExerciseType() {
        return "CompletaCodice";
    }

    @Override
    protected void loadExercisesByDifficulty(String difficulty) {
        currentExercises = exerciseModel.getCompletaCodiceExercisesByDifficulty(difficulty);
    }

    @Override
    protected void initializeExerciseSpecificData() {
        userAnswers = new String[currentExercises.size()];
        for (int i = 0; i < userAnswers.length; i++) {
            userAnswers[i] = "";
        }
    }

    @Override
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

    @Override
    protected void saveCurrentAnswer() {
        if (currentExerciseIndex < 0 || currentExerciseIndex >= userAnswers.length) return;
        userAnswers[currentExerciseIndex] = codeInput.getText();
    }

    @Override
    protected boolean checkAllAnswers() {
        for (int i = 0; i < currentExercises.size(); i++) {
            Exercise exercise = currentExercises.get(i);
            String userAnswer = userAnswers[i].trim();
            String correctAnswer = exercise.getAnswers()[0].trim();
            
            if (!userAnswer.equals(correctAnswer)) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected Stage getStage() {
        return (Stage) exerciseQuestion.getScene().getWindow();
    }
}

