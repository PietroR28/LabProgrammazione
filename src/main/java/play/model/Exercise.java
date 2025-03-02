package play.model;

public class Exercise {
    private String question;
    private String code;
    private String[] answers;
    private int correctAnswerIndex;

    public Exercise(String question, String code, String[] answers, int correctAnswerIndex) {
        this.question = question;
        this.code = code;
        this.answers = answers;
        this.correctAnswerIndex = correctAnswerIndex;
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
}