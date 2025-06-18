package play.model;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

/**
 * Manages a timer for exercise sessions.
 */
public class Timer {
    private long startTime;
    private long elapsedTime;
    private Timeline timeline;
    private Label timerLabel;
    
    /**
     * Initializes a timer that displays time in the specified label.
     * 
     * @param timerLabel The label to display the timer in
     */
    public Timer(Label timerLabel) {
        this.timerLabel = timerLabel;
        elapsedTime = 0;
    }
    
    /**
     * Starts the timer.
     */
    public void startTimer() {
        startTime = System.currentTimeMillis();
        
        // Update timer every second
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            long currentTime = System.currentTimeMillis();
            elapsedTime = (currentTime - startTime) / 1000;
            updateTimerLabel();
        }));
        
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
        
        updateTimerLabel();
    }
    
    /**
     * Stops the timer.
     */
    public void stopTimer() {
        if (timeline != null) {
            timeline.stop();
        }
    }
    
    /**
     * Returns the elapsed time in seconds.
     * 
     * @return elapsed time in seconds
     */
    public long getElapsedTimeInSeconds() {
        return elapsedTime;
    }
    
    /**
     * Updates the timer label with formatted time.
     */
    private void updateTimerLabel() {
        long minutes = elapsedTime / 60;
        long seconds = elapsedTime % 60;
        timerLabel.setText(String.format("Tempo: %02d:%02d", minutes, seconds));
    }
}