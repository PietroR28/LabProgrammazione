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

    public void setWelcomeMessage(String username) {
        welcomeLabel.setText("Ciao " + username + "!");
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
        String username = welcomeLabel.getText().replace("Ciao ", "").replace("!", ""); // Estrai lo username dal messaggio di benvenuto
        MacroExercise exercise = new TrovaErroreExercise(
            "Esercizio 1 - Trova l'errore",
            "Trova l'errore nel seguente codice",
            "public class Main {\n" +
            "  public static void main(String[] args) {\n" +
            "    System.out.<span style='color:red; text-decoration: underline;'>printtext</span>(\"Hello World\");\n" +
            "  }\n" +
            "}\n\n" +
            "Invece che <span style='color:red; text-decoration: underline;'>printtext</span> ci va:\n" +
            "a) printline\n" +
            "b) println\n" +
            "c) echo()",
            username
        );
        loadExercise(exercise);
    }
    
    @FXML
    public void handleExercise2() {
        String username = welcomeLabel.getText().replace("Ciao ", "").replace("!", ""); // Estrai lo username dal messaggio di benvenuto
        MacroExercise exercise = new OrdinaCodiceExercise(
            "Esercizio 2 - Ordina il codice",
            "Ordina il codice seguente in modo che sia corretto e funzionante",
            "DA MODIFICARE",
            username
        );
        loadExercise(exercise);
    }
    
    @FXML
    public void handleExercise3() {
        String username = welcomeLabel.getText().replace("Ciao ", "").replace("!", ""); // Estrai lo username dal messaggio di benvenuto
        MacroExercise exercise = new CompletaCodiceExercise(
            "Esercizio 3 - Completa il codice",
            "Completa il codice seguente in modo che sia corretto e funzionante",
            "DA MODIFICARE",
            username
        );
        loadExercise(exercise);
    }
}