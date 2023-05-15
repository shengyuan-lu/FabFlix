package Models;

public class Customer {
    private final int id;
    private final String firstName;
    private final String lastName;
    private final String ccid;
    private final String address;
    private final String email;

    public Customer(int id, String firstName, String lastName, String ccid, String address, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.ccid = ccid;
        this.address = address;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getCcid() {
        return ccid;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }
}