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

/**
 * Controller per la schermata di login dell'applicazione.
 *
 * Questa classe si occupa di:
 * - Gestire l'autenticazione dell'utente;
 * - Gestire la creazione di un nuovo utente;
 * - Mostrare messaggi di stato e feedback;
 * - Gestire la navigazione tra il form di login e quello di registrazione.
 */

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

    /** Form di login (GridPane) */
    @FXML
    private GridPane loginForm;

    /** Form di creazione nuovo utente (GridPane) */
    @FXML
    private GridPane createUserForm;


    /**
     * Gestisce il login dell'utente.
     * Verifica le credenziali e, se corrette, carica la schermata Home.
     * In caso di errore mostra un messaggio di stato.
     */
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
                homeController.setWelcomeMessage(UserDataManager.getUsername(user));
    
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


    /**
     * Gestisce il click sul pulsante "Crea nuovo utente".
     * Nasconde il form di login e mostra il form di registrazione.
     */
    @FXML
    public void handleCreateUser() {
        // Nascondi il form di login
        loginForm.setVisible(false);

        // Mostra il form di creazione del nuovo utente
        createUserForm.setVisible(true);
    }


    /**
     * Gestisce il salvataggio di un nuovo utente.
     * Valida i campi, salva l'utente tramite UserDataManager e mostra messaggi di feedback.
     */
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

        if (UserDataManager.saveNewUser(firstName, lastName, username, password)) {
            showStatusMessage("Nuovo utente creato con successo!", "green");

            createUserForm.setVisible(false);
            loginForm.setVisible(true);
        } else {
            showStatusMessage("Errore durante la creazione del nuovo utente.", "red");
        }
    }


    /**
     * Gestisce il click sul pulsante "Annulla" nella schermata di registrazione.
     * Torna al form di login nascondendo quello di creazione utente.
     */
    @FXML
    public void handleCancelCreateUser() {
        createUserForm.setVisible(false);
        loginForm.setVisible(true);
    }


    /**
     * Mostra un messaggio di stato temporaneo nella label di stato.
     * Il messaggio scompare automaticamente dopo alcuni secondi.
     * @param message Il testo da mostrare
     * @param color Il colore del testo (es: "red", "green")
     */
    private void showStatusMessage(String message, String color) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: " + color + "; -fx-background-color: white; -fx-padding: 10px; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-border-color: black; -fx-border-width: 1px;");
        statusLabel.setVisible(true);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> statusLabel.setVisible(false)));
        timeline.setCycleCount(1);
        timeline.play();
    }
}