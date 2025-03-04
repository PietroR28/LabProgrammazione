package play.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import play.model.MacroExercise;
import play.model.SessionManager;

public class MacroExerciseController {
    @FXML 
    private Label exerciseTitle;
    @FXML 
    private Label exerciseDescription;
    @FXML 
    private WebView exerciseExample;
    
    private MacroExercise currentExercise;

    public void initExercise(MacroExercise exercise) {
        this.currentExercise = exercise;
        exerciseTitle.setText(exercise.getTitle());
        exerciseDescription.setText(exercise.getDescription());
        
        // Set HTML content in WebView
        String exampleText = exercise.getExample();
        exerciseExample.getEngine().loadContent("<html><body>" + exampleText + "</body></html>");
    }

    @FXML
    public void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Home.fxml"));
            Scene scene = new Scene(loader.load());
    
            HomeController homeController = loader.getController();
            // Recupera lo username dal SessionManager
            String username = SessionManager.getUsername();
            homeController.setWelcomeMessage(username);
    
            Stage stage = (Stage) exerciseExample.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}