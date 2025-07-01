package play.model;

public class Exercise implements IExercise {
    private String question;
    private String code;
    private String[] answers;
    private int correctAnswerIndex;
    private String difficulty;

    public Exercise(String question, String code, String[] answers, int correctAnswerIndex, String difficulty) {
        this.question = question;
        this.code = code;
        this.answers = answers;
        this.correctAnswerIndex = correctAnswerIndex;
        this.difficulty = difficulty;
    }

    @Override
    public String getTitle() {
        return question; // Per Exercise, il titolo è la domanda
    }

    @Override
    public String getDescription() {
        return question; // Per Exercise, la descrizione è la domanda
    }

    public String getQuestion() {
        return question;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String[] getAnswers() {
        return answers;
    }

    @Override
    public int getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }

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