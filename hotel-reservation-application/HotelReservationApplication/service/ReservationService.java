package service;

import model.*;

import java.util.*;

/**
 * This class is a ReservationService API class that performs all business logic needed for booking a room and finding all rooms
 * available for booking
 */
public class ReservationService {

    private static final ArrayList<Reservation> reservations = new ArrayList<>();
    private static final Map<String, IRoom> rooms = new HashMap<>();
    private static ReservationService instance;
    private static final CustomerService customerServiceInstance = CustomerService.getInstance();

    /**
     * This ensures a single instance of ReservationService exists
     */
    private ReservationService() {}

    public static ReservationService getInstance(){
        if (instance == null){
            instance = new ReservationService();
        }
        return instance;
    }

    /**
     * This method add a newly created room in rooms collection
     * @param room - room reference
     */
    public void addARoom(IRoom room){
        rooms.put(room.getRoomNumber(), room);
    }

    /**
     * This method add a new reservation object to reservations collections
     * @param reservation - reservation reference
     */
    public void addAReservation(Reservation reservation){
        reservations.add(reservation);
    }

    /**
     * This method add a new room given all room attributes
     * @param roomNumber - room number in string
     * @param roomPrice - room price in double
     * @param roomType - room type in enum
     */
    public void addRoom(String roomNumber, double roomPrice, RoomType roomType){
        IRoom room;
        double epsilon = 1e-10;
        if(Math.abs(roomPrice) < epsilon){
            room = new FreeRoom(roomNumber,roomType);
        }
        else{
            room = new Room(roomNumber, roomPrice, roomType);
        }
        addARoom(room);
        System.out.println("Successfully added a new room: "+room);
    }

    /**
     * This method pull room object given a room id or number which is unique
     * @param roomId - room number in string
     * @return - return a IRoom reference to room object
     */
    public IRoom getARoom(String roomId){
        return rooms.get(roomId);
    }

    /**
     * This method get all rooms from the room collections
     * @return - return a list of all rooms references
     */
    public ArrayList<IRoom> getAllRooms(){
          ArrayList<IRoom> allRooms = new ArrayList<>(rooms.values());
          return allRooms;
    }

    /**
     * This method reserve a room for user by first checking availability of rooms
     * @param customer - customer object
     * @param room - room object
     * @param checkInDate - checkInDate in Date type
     * @param checkOutDate - checkOutDate in Date type
     * @return - return a reservation reference
     */
    public Reservation reserveARoom(Customer customer, IRoom room, Date checkInDate, Date checkOutDate){

        // check customer is valid and exists
        Set<Customer> allCustomers = new HashSet<>(customerServiceInstance.getAllCustomers());

        if (!(allCustomers.contains(customer))){
            System.out.println("Customer does not exist. Please create your account from the Main Menu options.");
            return null;
        }

        // check room is valid and exists
        Set<IRoom> allRooms = new HashSet<>(rooms.values());
        if (!(allRooms.contains(room))){
            System.out.println("Room does not exist. Please add a room from the Admin Menu options.");
            return null;
        }
        // check the existing room is available in the given date range
        for(Reservation reservation: reservations){
            if(reservation.getRoom().equals(room) && reservation.conflicts(checkInDate, checkInDate)){
                System.out.println("Room is already booked for these dates.");
                return null;
            }
        }
        // reserve the room
        Reservation newReservation = new Reservation(customer, room, checkInDate, checkOutDate);
        reservations.add(newReservation);
        System.out.println("Room booked successfully.");
        return newReservation;
    }

    /**
     * This method search for available free rooms, paid rooms , or all rooms given a date range
     * @param checkIn - checkIn in Date type
     * @param checkOut - checkOut in Date type
     * @param freeRoom - freeRoom string can have three values - free, paid, all
     * @return - return a list of available rooms
     */
    public ArrayList<IRoom> findRooms(Date checkIn, Date checkOut, String freeRoom){
        ArrayList<IRoom> availableRooms = new ArrayList<>();
        ArrayList<IRoom> roomsList = new ArrayList<>(rooms.values());
        ArrayList<IRoom> freeRoomsList = new ArrayList<>();
        ArrayList<IRoom> paidRoomsList = new ArrayList<>();

        for(IRoom room: roomsList){
            if(room.isFree()){
                freeRoomsList.add(room);
            }
            else{
                paidRoomsList.add(room);
            }
        }
        if (freeRoom.equals("free")){
            roomsList = freeRoomsList;
            System.out.println("Searching for free rooms and are available.");
        }
        else if(freeRoom.equals("paid")){
            roomsList = paidRoomsList;
            System.out.println("Searching for paid rooms and are available.");
        }
        else {
            System.out.println("Searching for all rooms and are available.");
        }

        for(IRoom room: roomsList){
            boolean isBooked = false;
            for(Reservation reservation: reservations){
                if(reservation.getRoom().equals(room) && reservation.conflicts(checkIn, checkOut)){
                    isBooked = true;
                    break;
                }
            }
            if (!isBooked){
                availableRooms.add(room);
            }
        }
        return availableRooms;
    }


    /**
     * This method get a list of all reservations made by a particular customer
     * @param customer - customer object
     * @return - return a list of all Reservations references
     */
    public ArrayList<Reservation> getCustomersReservation(Customer customer){
        ArrayList<Reservation> customerReservation = new ArrayList<>();
        for(Reservation reservation: reservations){
            if (reservation.getCustomer().equals(customer)){
                customerReservation.add(reservation);
            }
        }
        return customerReservation;
    }

    /**
     * This method prints all reservations
     */
    public void printAllReservation(){

        for(Reservation reservation: reservations){
            System.out.println(reservation);
        }

    }


}
