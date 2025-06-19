package play.controller;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import play.model.CompletaCodiceExercise;
import play.model.MacroExercise;
import play.model.OrdinaCodiceExercise;
import play.model.SessionManager;
import play.model.TrovaErroreExercise;
import play.model.User;

public class HomeController {
    @FXML private Label welcomeLabel;
    @FXML private ProgressBar progressBar1; // Progress bar per "Trova l'errore"
    @FXML private ProgressBar progressBar2; // Progress bar per "Ordina il codice"
    @FXML private ProgressBar progressBar3; // Progress bar per "Completa il codice"
    @FXML private Label statusLabel1; // Label per mostrare il livello raggiunto
    @FXML private Label statusLabel2;
    @FXML private Label statusLabel3;

    private static Map<String, User> users = new HashMap<>();

    /**
     * Classe per contenere informazioni sul progresso di un esercizio
     */
    private static class ExerciseProgress {
        double progressValue;
        String statusText;
        String statusEmoji;
        int completedLevels;

        ExerciseProgress(double progressValue, String statusText, String statusEmoji, int completedLevels) {
            this.progressValue = progressValue;
            this.statusText = statusText;
            this.statusEmoji = statusEmoji;
            this.completedLevels = completedLevels;
        }
    }

    @FXML
    public void initialize() {
        // Inizializza le progress bar con valore 0
        if (progressBar1 != null) progressBar1.setProgress(0.0);
        if (progressBar2 != null) progressBar2.setProgress(0.0);
        if (progressBar3 != null) progressBar3.setProgress(0.0);

        // Inizializza le label di stato
        if (statusLabel1 != null) statusLabel1.setText("Non iniziato");
        if (statusLabel2 != null) statusLabel2.setText("Non iniziato");
        if (statusLabel3 != null) statusLabel3.setText("Non iniziato");
    }

    public void setWelcomeMessage(String username) {
        welcomeLabel.setText("Ciao " + username + "!");
        // Salva lo username in SessionManager per renderlo persistente
        SessionManager.setUsername(username);

        // Carica e aggiorna i progressi degli esercizi
        updateProgressBars(username);
    }

    /**
     * Metodo pubblico per aggiornare l'interfaccia quando si torna alla home
     * Questo metodo dovrebbe essere chiamato dagli altri controller quando tornano alla home
     */
    public void refreshProgressData() {
        String username = SessionManager.getUsername();
        if (username != null && !username.trim().isEmpty()) {
            updateProgressBars(username);
        }
    }

    /**
     * Calcola e aggiorna le barre di progresso per tutti gli esercizi
     */
    private void updateProgressBars(String username) {
        if (username == null || username.trim().isEmpty()) {
            return;
        }

        // Carica i dati di salvataggio
        JSONObject savesData = loadSavesData();
        if (savesData == null || !savesData.has(username)) {
            return;
        }

        JSONObject userSaves = savesData.getJSONObject(username);

        // Calcola il progresso per ogni esercizio
        ExerciseProgress progress1 = calculateExerciseProgress(userSaves, "TrovaErrore");
        ExerciseProgress progress2 = calculateExerciseProgress(userSaves, "OrdinaCodice");
        ExerciseProgress progress3 = calculateExerciseProgress(userSaves, "CompletaCodice");

        // Aggiorna le progress bar e le label di stato
        updateExerciseUI(progressBar1, statusLabel1, progress1);
        updateExerciseUI(progressBar2, statusLabel2, progress2);
        updateExerciseUI(progressBar3, statusLabel3, progress3);
    }

    /**
     * Aggiorna l'interfaccia per un singolo esercizio
     */
    private void updateExerciseUI(ProgressBar progressBar, Label statusLabel, ExerciseProgress progress) {
        if (progressBar != null) {
            progressBar.setProgress(progress.progressValue);
        }

        if (statusLabel != null) {
            statusLabel.setText(progress.statusText);

            // Cambia il colore del testo in base al progresso
            String style = "-fx-font-weight: bold; ";
            switch (progress.completedLevels) {
                case 0:
                    style += "-fx-text-fill: #6c757d;"; // Grigio
                    break;
                case 1:
                    style += "-fx-text-fill: #fd7e14;"; // Arancione
                    break;
                case 2:
                    style += "-fx-text-fill: #ffc107;"; // Giallo oro
                    break;
                case 3:
                    style += "-fx-text-fill: #28a745;"; // Verde
                    break;
                default:
                    style += "-fx-text-fill: #6c757d;";
            }
            statusLabel.setStyle(style);
        }
    }

    /**
     * Carica i dati dal file saves.json
     */
    private JSONObject loadSavesData() {
        File file = new File("saves.json");
        if (file.exists()) {
            try {
                String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                // Rimuovi eventuali caratteri di controllo o spazi extra
                content = content.trim();
                
                // Se il file è vuoto o contiene solo "{}", restituisci un oggetto vuoto
                if (content.isEmpty() || content.equals("{}")) {
                    return new JSONObject();
                }
                
                return new JSONObject(content);
            } catch (IOException e) {
                System.err.println("Errore nella lettura del file saves.json: " + e.getMessage());
                return new JSONObject();
            } catch (org.json.JSONException e) {
                System.err.println("Errore nel parsing JSON di saves.json: " + e.getMessage());
                System.err.println("Creazione di un nuovo file saves.json...");
                
                // Crea un backup del file corrotto
                try {
                    Files.move(file.toPath(), new File("saves_backup.json").toPath());
                    System.err.println("File corrotto salvato come saves_backup.json");
                } catch (IOException backupError) {
                    System.err.println("Impossibile creare backup: " + backupError.getMessage());
                }
                
                return new JSONObject();
            }
        } else {
            return new JSONObject();
        }
    }

    /**
     * Calcola il progresso per un specifico esercizio con informazioni dettagliate.
     * Ora gestisce correttamente la progressione dei livelli.
     * @param userSaves I dati di salvataggio dell'utente
     * @param exerciseName Il nome dell'esercizio
     * @return Un oggetto ExerciseProgress con tutte le informazioni
     */
    private ExerciseProgress calculateExerciseProgress(JSONObject userSaves, String exerciseName) {
        if (!userSaves.has(exerciseName)) {
            return new ExerciseProgress(0.0, "Non iniziato", "", 0);
        }

        JSONObject exerciseData = userSaves.getJSONObject(exerciseName);

        // I livelli di difficoltà disponibili in ordine
        String[] difficulties = {"principiante", "intermedio", "esperto"};
        int completedLevels = 0;
        String highestLevelCompleted = "";

        // Conta i livelli completati in ordine sequenziale
        for (String difficulty : difficulties) {
            if (exerciseData.has(difficulty)) {
                JSONObject difficultyData = exerciseData.getJSONObject(difficulty);
                if (difficultyData.has("risultato") &&
                        "success".equals(difficultyData.getString("risultato"))) {
                    completedLevels++;
                    highestLevelCompleted = difficulty;
                } else {
                    // Se un livello non è completato, fermiamo il conteggio
                    // perché i livelli devono essere completati in ordine
                    break;
                }
            } else {
                // Se un livello non esiste nei dati, fermiamo il conteggio
                break;
            }
        }

        // Calcola il progresso come percentuale dei livelli completati
        double progressValue = (double) completedLevels / difficulties.length;

        // Determina il testo di stato
        String statusText;

        switch (completedLevels) {
            case 0:
                statusText = "Non iniziato";
                break;
            case 1:
                statusText = "Livello Principiante completato";
                break;
            case 2:
                statusText = "Livello Intermedio completato";
                break;
            case 3:
                statusText = "Tutti i livelli completati!";
                break;
            default:
                statusText = "Stato sconosciuto";
        }

        // Debug output
        System.out.println("DEBUG - " + exerciseName + ": " + completedLevels + " livelli completati. Ultimo: " + highestLevelCompleted);

        return new ExerciseProgress(progressValue, statusText, "", completedLevels);
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
            } else if (controller instanceof OrdinaCodiceExerciseController) {
                ((OrdinaCodiceExerciseController) controller).initUsername(exercise.getUsername());
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
                "<style>" +
                        ".code-preview {" +
                        "  font-family: 'Consolas', 'Monaco', monospace;" +
                        "  font-size: 14px;" +
                        "  background-color: #f8f9fa;" +
                        "  border: 1px solid #e9ecef;" +
                        "  border-radius: 5px;" +
                        "  padding: 10px;" +
                        "  white-space: pre;" +
                        "  display: block;" +
                        "}" +
                        "</style>" +
                        "<div class='code-preview'>" +
                        "public class HelloWorld {\n" +
                        "    System.out.println(\"Hello World!\");\n" +
                        "    public static void main(String[] args) {\n" +
                        "}\n" +
                        "}\n" +
                        "</div>" +
                        "<p>Ordina le righe di codice in modo corretto utilizzando drag & drop o i pulsanti di movimento.</p>",
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

    @FXML
    public void handleClassifiche() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Classifiche.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}