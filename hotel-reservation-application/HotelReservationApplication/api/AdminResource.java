package api;

import model.Customer;
import model.IRoom;
import model.RoomType;
import service.CustomerService;
import service.ReservationService;

import java.util.ArrayList;

/**
 * This is an AdminResource API class that takes UI inputs and redirect to its next service layer class
 */
public class AdminResource {
    // a static reference to the class - only one instance
    private static AdminResource instance;
    private static CustomerService customerServiceInstance;
    private static ReservationService reservationServiceInstance;

    /**
     * private constructor for creating a single instance of AdminResource API - Singleton pattern
     */
    private AdminResource(){
        customerServiceInstance = CustomerService.getInstance();
        reservationServiceInstance = ReservationService.getInstance();
    }

    public static AdminResource getInstance(){
        if (instance == null){
            instance = new AdminResource();
        }
        return instance;
    }


    /**
     * This method calls getCustomer() method of customerService
     * @param customerEmail - customer email in string
     * @return - return a customer object
     */
    public Customer getCustomer(String customerEmail){
         return customerServiceInstance.getCustomer(customerEmail);
    }

    /**
     * This method calls addRoom() method of reservationService by passing all the user input parameters
     * @param roomNumber - room number in string
     * @param roomPrice - room price in double
     * @param roomType - room type in enum
     */
    public void addRoom(String roomNumber, double roomPrice, RoomType roomType){
            reservationServiceInstance.addRoom(roomNumber, roomPrice, roomType);

    }

    /**
     * This method calls getAllRooms() method of reservationService
     * @return - return all rooms in reservationService collection
     */
    public ArrayList<IRoom> getAllRooms(){
         return reservationServiceInstance.getAllRooms();
    }

    /**
     * This method calls getAllCustomers() method of customerService
     * @return - return a list of all customers
     */
    public ArrayList<Customer> getAllCustomers(){
         return customerServiceInstance.getAllCustomers();
    }

    /**
     * This method calls printAllReservation() method of reservationService
     */
    public void displayAllReservations(){
         reservationServiceInstance.printAllReservation();
    }


}
