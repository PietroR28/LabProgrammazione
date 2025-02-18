package play.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
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
            statusLabel.setText("Login Success!");
            // Passa alla HomeScreen o gestisci la navigazione
        } else {
            statusLabel.setText("Credenziali errate.");
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
            statusLabel.setText("Compila tutti i campi.");
            return;
        }

        // Salva il nuovo utente, ad esempio chiamando un metodo di UserDataManager
        if (UserDataManager.saveNewUser(firstName, lastName, username, password)) {
            statusLabel.setText("Nuovo utente creato con successo!");
            // Puoi anche tornare alla schermata di login se vuoi
            createUserForm.setVisible(false); // Nascondi il form di creazione
            loginForm.setVisible(true); // Mostra di nuovo il form di login
        } else {
            statusLabel.setText("Errore durante la creazione del nuovo utente.");
        }
    }

    @FXML
    public void handleCancelCreateUser() {
        // Nascondi il form di creazione e mostra il form di login
        createUserForm.setVisible(false);
        loginForm.setVisible(true); // Mostra di nuovo il form di login
    }
}
