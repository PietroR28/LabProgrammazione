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
import play.model.SessionManager;
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
        // Salva lo username in SessionManager per renderlo persistente
        SessionManager.setUsername(username);
    }
    
    private void loadExercise(MacroExercise exercise) {
        try {
            FXMLLoader loader;
            if (exercise instanceof TrovaErroreExercise) {
                loader = new FXMLLoader(getClass().getResource("/fxml/TrovaErroreExercise.fxml"));
            } else if (exercise instanceof OrdinaCodiceExercise) {
                loader = new FXMLLoader(getClass().getResource("/fxml/OrdinaCodiceExercise.fxml"));
            } else if (exercise instanceof CompletaCodiceExercise) {
                loader = new FXMLLoader(getClass().getResource("/fxml/CompletaCodiceExercise.fxml"));
            } else {
                throw new IllegalArgumentException("Tipo di esercizio non supportato");
            }
            
            Scene scene = new Scene(loader.load());
            
            MacroExerciseController controller = loader.getController();
            controller.initExercise(exercise);
            
            // Passa lo username al controller specifico
            if (controller instanceof TrovaErroreExerciseController) {
                ((TrovaErroreExerciseController) controller).initUsername(exercise.getUsername());
            } else if (controller instanceof CompletaCodiceExerciseController) {
                ((CompletaCodiceExerciseController) controller).initUsername(exercise.getUsername());
            }
            
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleExercise1() {
        // Recupera lo username dal SessionManager
        String username = SessionManager.getUsername();
        MacroExercise exercise = new TrovaErroreExercise(
            "Esercizio 1 - Trova l'errore",
            "Trova l'errore nel seguente codice",
            "<style>" +
            ".center {" +
            "  display: flex;" +
            "  justify-content: center;" +
            "}" +
            "</style>" +
            "<pre><code>public class Main {\n" +
            "  public static void main(String[] args) {\n" +
            "    System.out.<span style='color:red; text-decoration: underline;'>printtext</span>(\"Hello World\");\n" +
            "  }\n" +
            "}</code></pre><br>" +
            "Invece che <span style='color:red; text-decoration: underline;'>printtext</span> ci va:<br>" +
            "<div class='center'>" +
            "<input type='radio' id='a' disabled><label for='a'>printline</label><br>" +
            "<input type='radio' id='b' disabled><label for='b'>println</label><br>" +
            "<input type='radio' id='c' disabled><label for='c'>echo()</label>" +
            "</div>",
            username
        );
        loadExercise(exercise);
    }
    
    @FXML
    public void handleExercise2() {
        String username = SessionManager.getUsername();
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
        String username = SessionManager.getUsername();
        MacroExercise exercise = new CompletaCodiceExercise(
            "Esercizio 3 - Completa il codice",
            "Completa il codice inserendo le parti mancanti per farlo funzionare correttamente",
            "<style>" +
            ".center {" +
            "  display: flex;" +
            "  justify-content: center;" +
            "}" +
            "</style>" +
            "<pre><code>public class Calcolatrice {\n" +
            "  public static void main(String[] args) {\n" +
            "    int a = 5;\n" +
            "    int b = 3;\n" +
            "    // inserisci qui il codice per calcolare e stampare la somma\n" +
            "    // ______\n" +
            "  }\n" +
            "}</code></pre>",
            username
        );
        loadExercise(exercise);
    }

    @FXML
    public void handleLogout() {
        // Reset dello username in SessionManager
        SessionManager.setUsername("");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}