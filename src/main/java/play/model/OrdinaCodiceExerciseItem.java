package play.model;

public class OrdinaCodiceExerciseItem extends Exercise {
    private int[] correctOrder;

    public OrdinaCodiceExerciseItem(String question, String code, int[] correctOrder, String difficulty) {
        // Per compatibilità con la classe Exercise, passiamo un array di risposte vuoto e un indice 0
        super(question, code, new String[0], 0, difficulty);
        this.correctOrder = correctOrder;
    }

    public int[] getCorrectOrder() {
        return correctOrder;
    }
}