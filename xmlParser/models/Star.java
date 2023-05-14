package models;

public class Star {
    private String id; // id in table (can't be null)
    private String name; // name in table (can't be null)
    private int birthYear; // birthYear in table (can be null)

    public Star(String id, String name, int birthYear) {
        this.id = id;
        this.name = name;
        this.birthYear = birthYear;
    }

    public Star() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int year) {
        this.birthYear = year;
    }
}
