package play.model;

/**
 * Classe che rappresenta un esercizio macro di tipo "Trova l'errore".
 *
 * Questa classe estende MacroExercise e permette di creare oggetti che
 * rappresentano la schermata introduttiva per esercizi in cui l'utente deve
 * individuare errori nel codice.
 *
 * Il costruttore accetta titolo, descrizione, esempio e username, e li passa
 * al costruttore della superclasse MacroExercise.
 */

public class TrovaErroreExercise extends MacroExercise {
    public TrovaErroreExercise(String title, String description, String example, String username) {
        super(title, description, example, username);
    }
}