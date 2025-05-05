package play.model;

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