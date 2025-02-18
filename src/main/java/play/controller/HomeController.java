package play.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class HomeController {

    @FXML
    private Button exercise1Button;

    @FXML
    private Button exercise2Button;

    @FXML
    private Button logoutButton;

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

}