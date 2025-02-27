package play.controller;

import java.io.IOException;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import play.model.UserDataManager;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label statusLabel;

    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField newUsernameField;
    @FXML
    private PasswordField newPasswordField;

    @FXML
    private GridPane loginForm; // Form di login
    @FXML
    private GridPane createUserForm; // Form di creazione nuovo utente

    @FXML
    public void handleLogin() {
        String user = usernameField.getText();
        String pass = passwordField.getText();
        if (UserDataManager.checkUser(user, pass)) {
            try {
                Stage stage = (Stage) usernameField.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Home.fxml"));
                Scene scene = new Scene(loader.load());
        
                HomeController homeController = loader.getController();
                homeController.setWelcomeMessage(UserDataManager.getFirstName(user));
        
                stage.setScene(scene);
                stage.setTitle("Home - Play Esercizi");
            } catch (IOException e) {
                e.printStackTrace();
                showStatusMessage("Errore nel caricamento della schermata iniziale.", "red");
            }
        } else {
            showStatusMessage("Credenziali errate.", "red");
        }
    }

    @FXML
    public void handleCreateUser() {
        // Nascondi il form di login
        loginForm.setVisible(false);

        // Mostra il form di creazione del nuovo utente
        createUserForm.setVisible(true);
    }

    @FXML
    public void handleSaveUser() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String username = newUsernameField.getText();
        String password = newPasswordField.getText();

        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            showStatusMessage("Compila tutti i campi.", "red");
            return;
        }

        // Salva il nuovo utente, ad esempio chiamando un metodo di UserDataManager
        if (UserDataManager.saveNewUser(firstName, lastName, username, password)) {
            showStatusMessage("Nuovo utente creato con successo!", "green");
            // Puoi anche tornare alla schermata di login se vuoi
            createUserForm.setVisible(false); // Nascondi il form di creazione
            loginForm.setVisible(true); // Mostra di nuovo il form di login
        } else {
            showStatusMessage("Errore durante la creazione del nuovo utente.", "red");
        }
    }

    @FXML
    public void handleCancelCreateUser() {
        // Nascondi il form di creazione e mostra il form di login
        createUserForm.setVisible(false);
        loginForm.setVisible(true); // Mostra di nuovo il form di login
    }

    private void showStatusMessage(String message, String color) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: " + color + "; -fx-background-color: white; -fx-padding: 10px; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-border-color: black; -fx-border-width: 1px;");
        statusLabel.setVisible(true);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> statusLabel.setVisible(false)));
        timeline.setCycleCount(1);
        timeline.play();
    }
}