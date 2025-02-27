package play.model;

public abstract class MacroExercise {
    private String title;
    private String description;
    private String example;
    private String username;

    public MacroExercise(String title, String description, String example, String username) {
        this.title = title;
        this.description = description;
        this.example = example;
        this.username = username;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getExample() { return example; }
    public String getUsername() { return username; }
}