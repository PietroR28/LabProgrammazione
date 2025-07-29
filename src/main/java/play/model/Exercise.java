
/**
 * Classe che rappresenta un esercizio a risposta multipla.
 *
 * Questa classe implementa l'interfaccia IExercise e fornisce:
 * - La domanda dell'esercizio;
 * - Il codice da analizzare o completare;
 * - Le possibili risposte;
 * - L'indice della risposta corretta;
 * - Il livello di difficoltà;
 * - Un metodo virtuale per l'ordine corretto (per esercizi OrdinaCodice).
 */
package play.model;

public class Exercise implements IExercise {
    private String question;

    private String code;

    private String[] answers;

    private int correctAnswerIndex;

    private String difficulty;


    /**
     * Costruttore della classe Exercise.
     * @param question La domanda dell'esercizio
     * @param code Il codice associato all'esercizio
     * @param answers Le possibili risposte
     * @param correctAnswerIndex L'indice della risposta corretta
     * @param difficulty Il livello di difficoltà
     */
    public Exercise(String question, String code, String[] answers, int correctAnswerIndex, String difficulty) {
        this.question = question;
        this.code = code;
        this.answers = answers;
        this.correctAnswerIndex = correctAnswerIndex;
        this.difficulty = difficulty;
    }


    /**
     * Restituisce il titolo dell'esercizio.
     * Per Exercise, coincide con la domanda.
     */
    @Override
    public String getTitle() {
        return question;
    }


    /**
     * Restituisce la descrizione dell'esercizio.
     * Per Exercise, coincide con la domanda.
     */
    @Override
    public String getDescription() {
        return question;
    }


    /**
     * Restituisce la domanda dell'esercizio.
     * @return La domanda
     */
    public String getQuestion() {
        return question;
    }


    /**
     * Restituisce il codice associato all'esercizio.
     * @return Il codice sorgente
     */
    @Override
    public String getCode() {
        return code;
    }


    /**
     * Restituisce l'array delle possibili risposte.
     * @return Array di risposte
     */
    @Override
    public String[] getAnswers() {
        return answers;
    }


    /**
     * Restituisce l'indice della risposta corretta.
     * @return Indice della risposta corretta
     */
    @Override
    public int getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }


    /**
     * Restituisce il livello di difficoltà dell'esercizio.
     * @return Difficoltà
     */
    @Override
    public String getDifficulty() {
        return difficulty;
    }

    // Metodo virtuale per gli esercizi OrdinaCodice
    // Implementazione di default, può essere sovrascritto nelle classi figlie
    @Override
    public int[] getCorrectOrder() {
        return new int[0];
    }
}