package models;

public class Star {
    private String id; // id in table (can't be null)
    private String name; // name in table (can't be null)
    private Integer birthYear; // birthYear in table (can be null)

    public Star(String id, String name, Integer birthYear) {
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

    public Integer getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(Integer year) {
        this.birthYear = year;
    }

    public boolean validate() {
        return !this.name.isEmpty();
    }

    public String getCSVLine() {
        return String.format("%s,%s,%d\n", this.id, this.name, this.birthYear);
    }
}
