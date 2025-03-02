package play.model;

public class Exercise {
    private String question;
    private String code;
    private String[] answers;
    private int correctAnswerIndex;
    private String difficulty; // Aggiungi questo campo

    public Exercise(String question, String code, String[] answers, int correctAnswerIndex, String difficulty) {
        this.question = question;
        this.code = code;
        this.answers = answers;
        this.correctAnswerIndex = correctAnswerIndex;
        this.difficulty = difficulty; // Inizializza questo campo
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
        return difficulty; // Aggiungi questo getter
    }
}