package play.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import play.model.Classifiche;
import play.model.SessionManager;

import java.io.IOException;

/**
 * Controller che si occupa di gestire la schermata delle classifiche.
 * 
 * Questa classe si occupa di popolare e visualizzare le classifiche degli utenti 
 * in base a:
 * - Numero di esercizi completati;
 * - Tempo totale impiegato a svolgere esercizi.
 */
public class ClassificheController {
    // Riferimenti alle ListView definite in FXML
    @FXML private ListView<String> successList;
    @FXML private ListView<String> timeList;

    @FXML
    public void initialize() {
        // Rende le liste non selezionabili e non focalizzabili (solo visualizzazione)
        successList.setMouseTransparent(true);
        successList.setFocusTraversable(false);
        timeList.setMouseTransparent(true);
        timeList.setFocusTraversable(false);

        // Popola la classifica dei successi
        Classifiche.getSuccessRanking().forEach(item ->
            successList.getItems().add(item.username + " - " + item.successCount + " set di esercizi completati")
        );

        // Popola la classifica dei tempi, formattando il tempo in minuti:secondi
        Classifiche.getTimeRanking().forEach(item -> {
            long totalSeconds = item.totalTime;
            long minutes = totalSeconds / 60;
            long seconds = totalSeconds % 60;
            String formattedTime = String.format("%d:%02d", minutes, seconds);
            timeList.getItems().add(item.username + " - " + formattedTime);
        });
    }

    // Gestisce il ritorno alla Home quando si preme il pulsante "Torna alla Home"
    @FXML
    protected void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Home.fxml"));
            Parent root = loader.load();

            HomeController controller = loader.getController();
            String username = SessionManager.getUsername();
            if (username != null && !username.trim().isEmpty()) {
                controller.setWelcomeMessage(username);
            }

            Stage stage = (Stage) successList.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
