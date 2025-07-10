package play.model;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

/**
 * Classe che gestisce un timer per le sessioni di esercizio.
 *
 * Questa classe fornisce:
 * - Avvio e arresto di un timer
 * - Calcolo del tempo trascorso in secondi dall'avvio;
 * - Visualizzazione del tempo trascorso in formato mm:ss;
 * - Metodi per ottenere il tempo trascorso e per aggiornare la visualizzazione.
 *
 * Utilizza Timeline di JavaFX per aggiornare periodicamente la Label associata.
 */
public class Timer {
    private long startTime; // Tempo di inizio in millisecondi
    private long elapsedTime; // Tempo trascorso in secondi
    private Timeline timeline;// Timeline JavaFX per aggiornare il timer ogni secondo
    private Label timerLabel; // Label su cui viene visualizzato il timer
    
    //Costruttore che inizializza il timer e associa la Label su cui mostrare il tempo.
    public Timer(Label timerLabel) {
        this.timerLabel = timerLabel;
        elapsedTime = 0;
    }
    
    /**
     * Avvia il timer.
     * Imposta il tempo di inizio e crea una Timeline che aggiorna la label ogni secondo.
     */
    public void startTimer() {
        startTime = System.currentTimeMillis();
        
        // Aggiorna il timer ogni secondo
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            long currentTime = System.currentTimeMillis();
            elapsedTime = (currentTime - startTime) / 1000;
            updateTimerLabel();
        }));
        
        timeline.setCycleCount(Animation.INDEFINITE); // Ripeti all'infinito
        timeline.play(); // Avvia la Timeline
        
        updateTimerLabel(); // Aggiorna subito la label
    }
    
    /**
     * Ferma il timer.
     * Se la Timeline esiste, la interrompe.
     */
    public void stopTimer() {
        if (timeline != null) {
            timeline.stop();
        }
    }
    
    //Restituisce il tempo trascorso in secondi.
    public long getElapsedTimeInSeconds() {
        return elapsedTime;
    }
    
    //Aggiorna la label del timer con il tempo formattato (mm:ss).
    private void updateTimerLabel() {
        long minutes = elapsedTime / 60;
        long seconds = elapsedTime % 60;
        timerLabel.setText(String.format("Tempo: %02d:%02d", minutes, seconds));
    }
}