package play.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import play.model.UserDataManager;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    @FXML
    public void handleLogin() {
        String user = usernameField.getText();
        String pass = passwordField.getText();
        if (UserDataManager.checkUser(user, pass)) {
            statusLabel.setText("Login Success!");
            // Passa alla schermata iniziale
            try {
                Stage stage = (Stage) usernameField.getScene().getWindow();
                Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/fxml/Home.fxml")));
                stage.setScene(scene);
                stage.setTitle("Home - Play Esercizi");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            statusLabel.setText("Credenziali errate.");
        }
    }
}