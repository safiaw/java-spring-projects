package api;

import model.Customer;
import model.IRoom;
import model.Reservation;
import service.CustomerService;
import service.ReservationService;

import java.util.ArrayList;
import java.util.Date;

/**
 * This is a HotelResource API class that takes all UI actions and their inputs and pass it to underlying services class
 */
public class HotelResource {

    // provide a static reference to HotelResource class - ensures the class has only one instance (Singleton pattern)
    private static HotelResource instance;
    private static ReservationService reservationServiceInstance;
    private static CustomerService customerServiceInstance;

    /**
     * This implements Singleton pattern for HotelResource API class
     */
    private HotelResource(){
        reservationServiceInstance = ReservationService.getInstance();
        customerServiceInstance = CustomerService.getInstance();
    }
    public static HotelResource getInstance(){
        if (instance == null){
            instance = new HotelResource();
        }
        return instance;
    }

    /**
     * This method call getCustomer() method of customerService
     * @param email - customer email address in string
     * @return - return customer object
     */
    public Customer getCustomer(String email){
        return customerServiceInstance.getCustomer(email);
    }

    /**
     * This method calls addCustomer() method of customerService
     * @param email - customer email address in string
     * @param firstName - customer first name in string
     * @param lastName - customer last name in string
     */
    public void createACustomer(String email, String firstName, String lastName){
           boolean isAdded = customerServiceInstance.addCustomer(email, firstName, lastName);
           if (isAdded){
               System.out.println("Successfully created a new customer.");
           }
           else {
               System.out.println("Failed to create a new customer.");
           }

    }

    /**
     * This method calls getARoom() method of reservationService
     * @param roomId - room number in string
     * @return - return IRoom reference - a room object
     */
    public IRoom getRoom(String roomId){
           return reservationServiceInstance.getARoom(roomId);
    }

    /**
     * This method calls bookARoom() method of reservationService
     * @param customerEmail - customer email in string
     * @param room - IRoom reference to room object
     * @param checkInDate - checkInDate in Date type
     * @param checkOutDate - checkOutDate in Date type
     * @return - return a Reservation object reference
     */
    public Reservation bookARoom(String customerEmail, IRoom room, Date checkInDate, Date checkOutDate){
           Customer customer = getCustomer(customerEmail);
           return reservationServiceInstance.reserveARoom(customer, room, checkInDate, checkOutDate);
    }

    /**
     * This method calls getCustomer() and getCustomerReservation() method of the two services
     * @param customerEmail - customer email in string
     * @return - return Reservation object list of the customer
     */
    public ArrayList<Reservation> getCustomersReservation(String customerEmail){
           Customer customer = getCustomer(customerEmail);
           return reservationServiceInstance.getCustomersReservation(customer);
    }

    /**
     * This method calls findRooms() method of reservationService
     * @param checkInDate - checkInDate in Date type
     * @param checkOutDate - checkOutDate in Date type
     * @param searchRooms - searchRoom in string
     * @return - return a list of IRoom references to room objects
     */
    public ArrayList<IRoom> findARoom(Date checkInDate, Date checkOutDate, String searchRooms){
           return reservationServiceInstance.findRooms(checkInDate, checkOutDate, searchRooms);
    }
}
