import model.*;
import service.CustomerService;
import service.ReservationService;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * This class populates test data for Customer, Room, and Reservation models in services collections
 */
public class TestData {

    private static final ReservationService reservationServiceInstance = ReservationService.getInstance();
    private static final CustomerService customerServiceInstance = CustomerService.getInstance();
    public static void populateTestData(){

        // add test rooms
        IRoom room1 = new Room("100", 234.89, RoomType.DOUBLE);
        IRoom room2 = new FreeRoom("101", RoomType.SINGLE);
        IRoom room3 = new Room("102", 123.68, RoomType.SINGLE);
        IRoom room4 = new FreeRoom("103", RoomType.DOUBLE);

        reservationServiceInstance.addARoom(room1);
        reservationServiceInstance.addARoom(room2);
        reservationServiceInstance.addARoom(room3);
        reservationServiceInstance.addARoom(room4);


        // add test customers
        Customer customer1 = new Customer("John.Deff@gmail.com", "John", "Deff");
        Customer customer2 = new Customer("GeorgeJohnson@gmail.com", "George", "Johnson");
        Customer customer3 = new Customer("Daniel.Greg@hotmail.com", "Daniel", "Greg");
        Customer customer4 = new Customer("Brett_123@outlook.com", "Brett", "Livi");

        customerServiceInstance.addACustomer(customer1);
        customerServiceInstance.addACustomer(customer2);
        customerServiceInstance.addACustomer(customer3);
        customerServiceInstance.addACustomer(customer4);


        // add test reservations
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Reservation reservation1 = null;
        Reservation reservation2 = null;
        Reservation reservation3 = null;
        Reservation reservation4 = null;

        try{
            reservation1 = new Reservation(customer1, room1, format.parse("2024-10-01"), format.parse("2024-10-05"));
            reservation2 = new Reservation(customer2, room3, format.parse("2024-10-25"), format.parse("2024-10-28"));
            reservation3 = new Reservation(customer3, room2, format.parse("2024-11-10"), format.parse("2024-11-12"));
            reservation4 = new Reservation(customer4, room1, format.parse("2024-10-06"), format.parse("2024-10-08"));

        }
        catch (ParseException e){
            System.out.println("Invalid date format. Please use YYYY-MM-dd.");
        }

        reservationServiceInstance.addAReservation(reservation1);
        reservationServiceInstance.addAReservation(reservation2);
        reservationServiceInstance.addAReservation(reservation3);
        reservationServiceInstance.addAReservation(reservation4);
    }
}
