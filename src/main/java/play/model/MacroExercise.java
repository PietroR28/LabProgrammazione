package play.model;

public abstract class MacroExercise {
    private String title;
    private String description;
    private String starterCode;

    public MacroExercise(String title, String description, String starterCode) {
        this.title = title;
        this.description = description;
        this.starterCode = starterCode;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getStarterCode() { return starterCode; }
}