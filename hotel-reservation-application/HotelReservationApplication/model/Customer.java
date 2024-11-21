package model;

import java.util.regex.Pattern;

/**
 * This is a Customer model class that defines customer attributes and their behaviour
 */
public class Customer {
    private final String firstName;
    private final String lastName;
    private final String email;

    public Customer(String email, String firstName, String lastName){
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }



    /* getters */
    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true;}
        if (!( obj instanceof Customer)) { return false;}
        Customer customer = (Customer) obj;
        return this.email.equals(customer.email);
    }

    @Override
    public int hashCode() {
        return email.hashCode();
    }

    @Override
    public String toString() {
        return "Customer{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
