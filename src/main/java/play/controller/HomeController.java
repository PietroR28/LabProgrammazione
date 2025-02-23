package play.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import play.model.Exercise;

public class HomeController {
    @FXML private Label welcomeLabel;

    @FXML
    private Button exercise1Button;

    @FXML
    private Button exercise2Button;

    @FXML
    private Button exercise3Button;

    @FXML
    private Button exercise4Button;

    public void setWelcomeMessage(String username) {
        welcomeLabel.setText("Ciao, " + username + "!");
    }

    private void loadExercise(Exercise exercise) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Exercise.fxml"));
            Scene scene = new Scene(loader.load());
            
            ExerciseController controller = loader.getController();
            controller.initExercise(exercise);
            
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleExercise1() {
        Exercise exercise = new Exercise(
            "Esercizio 1 - Calcolo Media",
            "Scrivi una funzione che calcoli la media di un array di numeri.",
            "public class Solution {\n    public static double calcolaMedia(int[] numeri) {\n        // Scrivi qui il tuo codice\n    }\n}"
        );
        loadExercise(exercise);
    }

    @FXML
    public void handleExercise2() {
        Exercise exercise = new Exercise(
            "Esercizio 2 - Palindromo",
            "Scrivi una funzione che verifichi se una stringa è palindroma.",
            "public class Solution {\n    public static boolean isPalindromo(String testo) {\n        // Scrivi qui il tuo codice\n    }\n}"
        );
        loadExercise(exercise);
    }

    @FXML
    public void handleExercise3() {
        // Logica per gestire l'esercizio 3
        System.out.println("Esercizio 3 selezionato");
    }

    @FXML
    public void handleExercise4() {
        // Logica per gestire l'esercizio 4
        System.out.println("Esercizio 4 selezionato");
    }
}