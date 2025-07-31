package play.model;

/**
 * Modello dati per esercizi di tipo "Completa Codice".
 * Estende MacroExercise aggiungendo la gestione del codice completo corretto
 * che serve come riferimento per la validazione delle risposte.
 */
public class CompletaCodiceExercise extends MacroExercise {
    private String codiceCompleto; // Il codice completo corretto

    public CompletaCodiceExercise(String title, String description, String example, String username) {
        super(title, description, example, username);
    }

    public CompletaCodiceExercise(String title, String description, String example, String username, String codiceCompleto) {
        super(title, description, example, username);
        this.codiceCompleto = codiceCompleto;
    }

    public String getCodiceCompleto() {
        return codiceCompleto;
    }

    public void setCodiceCompleto(String codiceCompleto) {
        this.codiceCompleto = codiceCompleto;
    }
}