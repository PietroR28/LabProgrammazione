package play.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import play.model.MacroExercise;

public class MacroExerciseController {
    @FXML private Label exerciseTitle;
    @FXML private Label exerciseDescription;
    @FXML private TextArea codeArea;
    @FXML private Label resultLabel;
    
    private MacroExercise currentExercise;

    public void initExercise(MacroExercise exercise) {
        this.currentExercise = exercise;
        exerciseTitle.setText(exercise.getTitle());
        exerciseDescription.setText(exercise.getDescription());
        codeArea.setText(exercise.getStarterCode());
    }

    @FXML
    public void handleVerify() {
        // Qui implementerai la logica di verifica del codice
        String code = codeArea.getText();
        // TODO: Implementare la verifica del codice
        resultLabel.setText("Verifica in corso...");
    }

    @FXML
    public void handleReset() {
        codeArea.setText(currentExercise.getStarterCode());
        resultLabel.setText("");
    }

    @FXML
    public void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Home.fxml"));
            Scene scene = new Scene(loader.load());
    
            HomeController homeController = loader.getController();
            String username = currentExercise.getUsername();
            homeController.setWelcomeMessage(username); // Passa il nome dell'utente
    
            Stage stage = (Stage) codeArea.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}