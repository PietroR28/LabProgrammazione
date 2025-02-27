package play.controller;

import java.util.HashMap;
import java.util.Map;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import play.model.CompletaCodiceExercise;
import play.model.MacroExercise;
import play.model.OrdinaCodiceExercise;
import play.model.TrovaErroreExercise;
import play.model.User;

public class HomeController {
    @FXML private Label welcomeLabel;

    private static Map<String, User> users = new HashMap<>();

    @FXML
    public void initialize() {
    }

    public static String getFirstName(String username) {
        User user = users.get(username);
        return user != null ? user.getFirstName() : null;
    }

    public void setWelcomeMessage(String firstName) {
        welcomeLabel.setText("Ciao " + firstName + "!");
    }

    private void loadExercise(MacroExercise exercise) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MacroExercise.fxml"));
            Scene scene = new Scene(loader.load());
            
            MacroExerciseController controller = loader.getController();
            controller.initExercise(exercise);
            
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleExercise1() {
        MacroExercise exercise = new TrovaErroreExercise(
            "Esercizio 1 - Trova l'errore",
            "Trova l'errore nel seguente codice",
            "DA MODIFICARE"
        );
        loadExercise(exercise);
    }

    @FXML
    public void handleExercise2() {
        MacroExercise exercise = new OrdinaCodiceExercise(
            "Esercizio 2 - Ordina il codice",
            "Ordina il codice seguente in modo che sia corretto e funzionante",
            "DA MODIFICARE"
        );
        loadExercise(exercise);
    }

    @FXML
    public void handleExercise3() {
        MacroExercise exercise = new CompletaCodiceExercise(
            "Esercizio 3 - Completa il codice",
            "Completa il codice seguente in modo che sia corretto e funzionante",
            "DA MODIFICARE"
        );
        loadExercise(exercise);
    }

}