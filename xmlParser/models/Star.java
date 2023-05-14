package models;

public class Star {
    private String name; // name in table (can't be null)
    private int birthYear; // birthYear in table (can be null)

    public void Star(String name, int birthYear) {
        this.name = name;
        this.birthYear = birthYear;
    }

    public String getName() {
        return name;
    }

    public int getBirthYear() {
        return birthYear;
    }
}
