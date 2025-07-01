package play.model;

/**
 * Interfaccia comune per tutti i tipi di esercizi.
 * Unifica le gerarchie Exercise e MacroExercise fornendo
 * un contratto comune per le operazioni base.
 */
public interface IExercise {
    
    /**
     * Restituisce il titolo/domanda dell'esercizio
     */
    String getTitle();
    
    /**
     * Restituisce la domanda dell'esercizio
     */
    default String getQuestion() {
        return getTitle(); // Default: la domanda è il titolo
    }
    
    /**
     * Restituisce la descrizione dell'esercizio
     */
    String getDescription();
    
    /**
     * Restituisce il livello di difficoltà
     */
    String getDifficulty();
    
    /**
     * Restituisce il codice associato all'esercizio (se presente)
     */
    default String getCode() {
        return "";
    }
    
    /**
     * Restituisce le possibili risposte (per esercizi a scelta multipla)
     */
    default String[] getAnswers() {
        return new String[0];
    }
    
    /**
     * Restituisce l'indice della risposta corretta (per esercizi a scelta multipla)
     */
    default int getCorrectAnswerIndex() {
        return -1;
    }
    
    /**
     * Restituisce l'ordine corretto (per esercizi di ordinamento)
     */
    default int[] getCorrectOrder() {
        return new int[0];
    }
}
