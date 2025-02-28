package play.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import play.model.MacroExercise;

public class MacroExerciseController {
    @FXML private Label exerciseTitle;
    @FXML private Label exerciseDescription;
    @FXML private TextFlow exerciseExample;
    
    private MacroExercise currentExercise;

    public void initExercise(MacroExercise exercise) {
        this.currentExercise = exercise;
        exerciseTitle.setText(exercise.getTitle());
        exerciseDescription.setText(exercise.getDescription());
        
        // Clear existing children
        exerciseExample.getChildren().clear();
        
        // Add formatted text using Text and Label
        String exampleText = exercise.getExample();
        String[] parts = exampleText.split("<span style='color:red; text-decoration: underline;'>|</span>");
        
        for (int i = 0; i < parts.length; i++) {
            if (i % 2 == 0) {
                // Normal text
                exerciseExample.getChildren().add(new Text(parts[i]));
            } else {
                // Formatted text
                Label redUnderlineText = new Label(parts[i]);
                redUnderlineText.setStyle("-fx-text-fill: red; -fx-underline: true;");
                exerciseExample.getChildren().add(redUnderlineText);
            }
        }
    }

    @FXML
    public void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Home.fxml"));
            Scene scene = new Scene(loader.load());
    
            HomeController homeController = loader.getController();
            String username = currentExercise.getUsername();
            homeController.setWelcomeMessage(username);
    
            Stage stage = (Stage) exerciseExample.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}