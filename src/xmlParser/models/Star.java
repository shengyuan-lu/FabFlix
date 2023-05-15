package xmlParser.models;

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

    public String getDetails() {
        StringBuilder sb = new StringBuilder();

        sb.append("Star ID: " + this.id + "\n");
        sb.append("Star Name: " + this.name + "\n");
        sb.append("Star Birth Year: " + this.birthYear + "\n");

        return sb.toString();
    }

    public String getCSVLine() {

        if (this.birthYear == null) {
            return String.format("%s,%s,null\n", this.id, this.name);
        } else {
            return String.format("%s,%s,%d\n", this.id, this.name, this.birthYear);
        }

    }
}
