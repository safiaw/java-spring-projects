package service;

import model.Customer;

import java.util.*;
import java.util.regex.Pattern;

/**
 * This class is a CustomerService API which interacts with Resource classes and model classes
 * This is a stateful API implementation that maintains data structure for storing models
 */
public class CustomerService {
    private static final Map<String, Customer> customerAccounts = new HashMap<>();
    private static CustomerService instance;

    /**
     * This ensures only one instance of CustomerService is available
     */
    private CustomerService(){
    }

    public static CustomerService getInstance(){
        if (instance == null) {
            instance = new CustomerService();
        }
        return instance;
    }

    /**
     * This method add a new customer in customer map
     * @param customer - customer object
     */
    public void addACustomer(Customer customer){
        customerAccounts.put(customer.getEmail(), customer);
    }

    /**
     * This method creates a new customer account by taking all user inputs
     * @param email - email address in string
     * @param firstName - first name in string
     * @param lastName - lastname in string
     * @return - return a new customer object
     */
    public boolean addCustomer(String email, String firstName, String lastName){
          if (customerAccounts.containsKey(email)){
              System.out.println("Customer already exist.");
              return false;
          }

          Customer newCustomer = new Customer(email, firstName, lastName);
          addACustomer(newCustomer);
          System.out.println("Successfully created a new customer account: " +newCustomer);
          return true;
    }

    /**
     * This method gets customer object from the customer map given a customer email
     * @param email - customer email address in string
     * @return - return customer object
     */
    public Customer getCustomer(String email){
       return customerAccounts.get(email);
    }

    /**
     * This method get all customers in customer mao
     * @return - return a list of all customer objects
     */
    public ArrayList<Customer> getAllCustomers(){
        return new ArrayList<Customer>(customerAccounts.values());

    }


}
