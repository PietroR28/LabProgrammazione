package play.controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class OrdinaCodiceExerciseController extends MacroExerciseController {
    private String username;

    public void initUsername(String username) {
        this.username = username;
    }

    @FXML
    public void handleExerciseRedirect(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/OCExercise.fxml"));
            Parent root = loader.load();
            OrdinaCodiceController controller = loader.getController();
            controller.initData(username);
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}