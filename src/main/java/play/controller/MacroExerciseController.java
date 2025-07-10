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

/**
 * Controller di base per la gestione delle schermate introduttive degli esercizi.
 *
 * Questa classe fornisce:
 * - Metodi per inizializzare e visualizzare titolo, descrizione ed esempio dell'esercizio;
 * - Funzionalità di ritorno alla schermata Home, assicurando che il contesto utente
 *   venga mantenuto correttamente;
 * - Metodi riutilizzabili per la navigazione tra schermate, pensati per essere estesi da controller specifici.
 */

public class MacroExerciseController {
    // Collegamenti agli elementi dell'interfaccia grafica tramite FXML
    @FXML
    private Label exerciseTitle;
    @FXML
    private Label exerciseDescription;
    @FXML
    private WebView exerciseExample;

    // Riferimento all'esercizio macro attualmente visualizzato
    private MacroExercise currentExercise;

    /**
     * Inizializza la schermata con i dati dell'esercizio macro fornito.
     * Imposta titolo, descrizione e esempio (in HTML) nei rispettivi componenti grafici.
     */
    public void initExercise(MacroExercise exercise) {
        this.currentExercise = exercise;
        exerciseTitle.setText(exercise.getTitle());
        exerciseDescription.setText(exercise.getDescription());

        // Imposta il contenuto HTML nell'area WebView per mostrare l'esempio
        String exampleText = exercise.getExample();
        exerciseExample.getEngine().loadContent("<html><body>" + exampleText + "</body></html>");
    }

    // Metodo di utilità per tornare alla schermata Home.
    protected void redirectToHome() {
        try {
            URL fxmlLocation = getClass().getResource("/fxml/Home.fxml");
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent homeRoot = loader.load();
            Scene homeScene = new Scene(homeRoot);

            // Ottiene il controller della Home e aggiorna i progressi dell'utente
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

    // Gestisce il click sul pulsante "Torna alla Home".
    @FXML
    public void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Home.fxml"));
            Scene scene = new Scene(loader.load());

            // Ottiene il controller della Home e aggiorna i progressi dell'utente
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