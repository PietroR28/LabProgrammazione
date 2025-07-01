package play.model;

public abstract class MacroExercise implements IExercise {
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

    @Override
    public String getTitle() { 
        return title; 
    }
    
    @Override
    public String getDescription() { 
        return description; 
    }
    
    @Override
    public String getDifficulty() {
        return "principiante"; // Default, può essere override nelle sottoclassi
    }
    
    public String getExample() { 
        return example; 
    }
    
    public String getUsername() { 
        return username; 
    }
}