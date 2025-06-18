package play.controller;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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

            Stage stage = (Stage) exerciseExample.getScene().getWindow();
            stage.setScene(homeScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Home.fxml"));
            Scene scene = new Scene(loader.load());

            // IMPORTANTE: Ottieni il controller della Home e aggiorna i progressi
            HomeController homeController = loader.getController();
            String username = SessionManager.getUsername();
            if (username != null && !username.trim().isEmpty()) {
                homeController.setWelcomeMessage(username);
            }

            Stage stage = (Stage) exerciseExample.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}