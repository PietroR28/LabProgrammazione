package play.model;

public abstract class MacroExercise {
    private String title;
    private String description;
    private String starterCode;
    private String username;

    public MacroExercise(String title, String description, String starterCode, String username) {
        this.title = title;
        this.description = description;
        this.starterCode = starterCode;
        this.username = username;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getStarterCode() { return starterCode; }
    public String getUsername() { return username; }
}