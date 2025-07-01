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

public class TrovaErroreController extends BaseExerciseController {

    private final ExerciseModel exerciseModel = new ExerciseModel();
    
    // Aggiungiamo un array per salvare l'indice della risposta scelta per ogni domanda (inizialmente -1)
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

    @FXML
    public void initialize() {
        ToggleGroup group = new ToggleGroup();
        answerA.setToggleGroup(group);
        answerB.setToggleGroup(group);
        answerC.setToggleGroup(group);
    }

    // Implementazione dei metodi astratti richiesti da BaseExerciseController
    @Override
    protected String getExerciseType() {
        return "TrovaErrore";
    }

    @Override
    protected void loadExercisesByDifficulty(String difficulty) {
        currentExercises = exerciseModel.getExercisesByDifficulty(difficulty);
    }

    @Override
    protected void initializeExerciseSpecificData() {
        // Inizializziamo l'array con -1 per indicare che nessuna risposta è stata scelta
        if (currentExercises != null) {
            userAnswers = new int[currentExercises.size()];
            for (int i = 0; i < userAnswers.length; i++) {
                userAnswers[i] = -1;
            }
        }
    }

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

    @Override
    protected Stage getStage() {
        return (Stage) exerciseQuestion.getScene().getWindow();
    }
}