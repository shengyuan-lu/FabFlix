package helpers;

import java.util.Objects;

public class StarPair {

    // Pair attributes
    public String name;
    public Integer birthYear;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StarPair pair = (StarPair) o;
        return Objects.equals(name, pair.name) && Objects.equals(birthYear, pair.birthYear);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, birthYear);
    }

    // Constructor to initialize pair
    public StarPair(String name, Integer birthYear)
    {
        // This keyword refers to current instance
        this.name = name;
        this.birthYear = birthYear;
    }
}