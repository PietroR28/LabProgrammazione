/**
 * Classe astratta che rappresenta un esercizio macro generico.
 *
 * Questa classe fornisce:
 * - Campi comuni a tutti gli esercizi macro: titolo, descrizione, esempio e username;
 * - Un costruttore che inizializza tutti i campi;
 * - Metodi getter per accedere alle proprietà dell'esercizio.
 *
 * Le sottoclassi devono implementare l'interfaccia IExercise e possono estendere
 * o personalizzare il comportamento di MacroExercise.
 */
package play.model;

public abstract class MacroExercise implements IExercise {
    private String title; //Titolo dell'esercizio
    private String description; //Descrizione dell'esercizio
    private String example; //Esempio di codice o testo associato all'esercizio
    private String username; //Username dell'utente associato all'esercizio

    public MacroExercise(String title, String description, String example, String username) {
        this.title = title;
        this.description = description;
        this.example = example;
        this.username = username;
    }

    //Restituisce il titolo dell'esercizio.
    @Override
    public String getTitle() { 
        return title; 
    }
    
    //Restituisce la descrizione dell'esercizio.
    @Override
    public String getDescription() { 
        return description; 
    }
    
    /**
     * Restituisce la difficoltà dell'esercizio.
     * Di default è "principiante", ma può essere ridefinito nelle sottoclassi.
     */
    @Override
    public String getDifficulty() {
        return "principiante";
    }
    
    //Restituisce l'esempio associato all'esercizio.
    public String getExample() { 
        return example; 
    }
    
    //Restituisce lo username dell'utente associato all'esercizio.
    public String getUsername() { 
        return username; 
    }
}