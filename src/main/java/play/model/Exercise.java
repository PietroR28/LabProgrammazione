package play.model;

public class Exercise {
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

    public String getQuestion() {
        return question;
    }

    public String getCode() {
        return code;
    }

    public String[] getAnswers() {
        return answers;
    }

    public int getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }

    public String getDifficulty() {
        return difficulty;
    }

    // Metodo per gli esercizi OrdinaCodice, restituisce un array vuoto per default
    // Verrà sovrascritto nelle classi figlie
    public int[] getCorrectOrder() {
        return new int[0];
    }
}