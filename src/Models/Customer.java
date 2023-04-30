package Models;

/**
 * This User class only has the username field in this example.
 * You can add more attributes such as the user's shopping cart items.
 */
public class Customer {

    private final String customerEmail;
    private final String customerId;

    public Customer(String customerEmail, String customerId) {
        this.customerEmail = customerEmail;
        this.customerId = customerId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

}