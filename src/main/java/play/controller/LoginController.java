package play.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import play.model.UserDataManager;

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
            // Passa alla HomeScreen o gestisci la navigazione
        } else {
            statusLabel.setText("Credenziali errate.");
        }
    }
}