package play.controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Controller per la gestione della schermata introduttiva dell'esercizio "Trova l'errore".
 *
 * Questa classe estende MacroExerciseController e si occupa di:
 * - Gestire la transizione dalla schermata introduttiva all'esercizio vero e proprio;
 * - Passare lo username al controller dell'esercizio per mantenere il contesto utente
 * e consentire un corretto salvataggio nel file json.
 */

public class TrovaErroreExerciseController extends MacroExerciseController {
    // Campo per memorizzare lo username dell'utente corrente
    private String username;

    // Metodo per inizializzare lo username (viene chiamato da altri controller)
    public void initUsername(String username) {
        this.username = username;
    }

    // Gestisce il click sul pulsante "Vai all'esercizio"
    @FXML
    public void handleExerciseRedirect(ActionEvent event) {
        try {
            // Carica il file FXML dell'esercizio vero e proprio
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TEExercise.fxml"));
            Parent root = loader.load();

            // Ottiene il controller dell'esercizio e passa lo username
            TrovaErroreController controller = loader.getController();
            controller.initData(username);

            // Recupera lo stage corrente e imposta la nuova scena
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}