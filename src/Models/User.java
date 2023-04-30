package Models;

/**
 * This User class only has the username field in this example.
 * You can add more attributes such as the user's shopping cart items.
 */
public class User {
    private final int id;
    private final String firstName;
    private final String lastName;

    private final String ccid;
    private final String address;
    private final String username; // username = email

    public User(int id, String firstName, String lastName, String ccid, String address, String username) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.ccid = ccid;
        this.address = address;
        this.username = username;
    }

}