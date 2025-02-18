package play.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class HomeController {

    @FXML
    private Label welcomeLabel;

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

    @FXML
    public void handleExercise1() {
        // Logica per gestire l'esercizio 1
        System.out.println("Esercizio 1 selezionato");
    }

    @FXML
    public void handleExercise2() {
        // Logica per gestire l'esercizio 2
        System.out.println("Esercizio 2 selezionato");
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