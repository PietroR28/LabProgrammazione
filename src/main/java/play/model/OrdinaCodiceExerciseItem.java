package play.model;

/**
 * Rappresenta un singolo esercizio di ordinamento codice.
 * Estende Exercise ma aggiunge la proprietà correctOrder specifica per questo tipo.
 */
public class OrdinaCodiceExerciseItem extends Exercise {
    // Array che contiene l'ordine corretto delle righe di codice
    private int[] correctOrder;

    /**
     * Costruttore per un esercizio di ordinamento codice
     * @param question La domanda/istruzioni dell'esercizio
     * @param code Il codice da ordinare (come stringa con \n tra le righe)
     * @param correctOrder Array con l'ordine corretto (es: [0,2,1,3,4])
     * @param difficulty Livello di difficoltà (principiante/intermedio/esperto)
     */
    public OrdinaCodiceExerciseItem(String question, String code, int[] correctOrder, String difficulty) {
        // Chiama il costruttore padre con array vuoto per answers e 0 per correctAnswerIndex
        // perché negli esercizi di ordinamento non ci sono "risposte multiple" ma solo l'ordine
        super(question, code, new String[0], 0, difficulty);
        this.correctOrder = correctOrder;
    }

    /**
     * Override del metodo della classe padre per restituire l'ordine corretto.
     * Questo è il metodo cruciale che il controller usa per verificare se l'utente
     * ha ordinato correttamente le righe di codice.
     */
    @Override
    public int[] getCorrectOrder() {
        return correctOrder;
    }
}