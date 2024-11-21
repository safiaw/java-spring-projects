import api.AdminResource;
import model.*;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * This class is an AdminMenu UI class that help admin performs a few actions and redirect all these actions to its next admin
 * resource layer
 */
public class AdminMenu {

    private static final AdminResource adminResourceInstance = AdminResource.getInstance();

    public static void adminMenu(){

        while(true){
            System.out.println("Admin Menu:");
            System.out.println("---------------------------------------");
            System.out.println("1. See all customers.");
            System.out.println("2. See all rooms.");
            System.out.println("3. See all reservations.");
            System.out.println("4. Add a room.");
            System.out.println("5. Populate test data");
            System.out.println("6. Back to Main Menu");
            System.out.println("---------------------------------------");
            System.out.println("Please select a number for the menu option");
            int choice;
            try {
                choice = MainMenu.scanner.nextInt();
                MainMenu.scanner.nextLine();
            }
            catch (InputMismatchException e){
                System.out.println("Invalid integer input. Please select a number from the given menu option.");
                MainMenu.scanner.next();
                continue;
            }


            switch(choice){
                case 1:
                    ArrayList<Customer> allCustomers = new ArrayList<Customer>(adminResourceInstance.getAllCustomers());
                    if (!(allCustomers.isEmpty())) {
                        System.out.println("Printing all customers: ");
                        for (Customer customer : allCustomers) {
                            System.out.println(customer);
                        }
                    }
                    else{
                        System.out.println("No customers exists.");
                    }
                    break;
                case 2:
                    ArrayList<IRoom> allRooms = new ArrayList<IRoom>(adminResourceInstance.getAllRooms());
                    if (!(allRooms.isEmpty())) {
                        System.out.println("Printing all rooms: ");
                        for (IRoom room : allRooms) {
                            System.out.println(room);
                        }
                    }
                    else{
                        System.out.println("No rooms exists.");
                    }
                    break;

                case 3:
                    adminResourceInstance.displayAllReservations();
                    break;
                case 4:
                    System.out.println("Enter room number:");
                    String roomNumber = MainMenu.scanner.nextLine();
                    double roomPrice;
                    while(true){
                        try{
                            System.out.println("Enter room price: ");
                            roomPrice = MainMenu.scanner.nextDouble();
                            MainMenu.scanner.nextLine();
                            break;
                        }
                        catch(InputMismatchException e){
                            System.out.println("Invalid room price! Please enter a numeric or a double value.");
                            MainMenu.scanner.next();
                        }
                    }


                    RoomType roomType;
                    while(true){
                        System.out.println("Enter room type: ");
                        for(RoomType type: RoomType.values()){
                            System.out.println(type.ordinal()+1 + ": " + type);
                        }
                        int roomTypeChoice;
                        try {
                            roomTypeChoice = MainMenu.scanner.nextInt();
                            MainMenu.scanner.nextLine();
                        }
                        catch(InputMismatchException e){
                            System.out.println("Invalid integer input for room type choice. Please select a valid integer option.");
                            MainMenu.scanner.next();
                            continue;
                        }

                        if( roomTypeChoice < 1 || roomTypeChoice > RoomType.values().length){
                            System.out.println("Please enter the type from the above given options.");
                        }
                        else {
                            roomType = RoomType.values()[roomTypeChoice-1];
                            break;
                        }
                    }
                    adminResourceInstance.addRoom(roomNumber, roomPrice,roomType);
                    break;

                case 5:
                    System.out.println("Populating test data in Customer, Room, and Reservation models.");
                    TestData.populateTestData();
                    break;
                case 6:
                    System.out.println("Returning to Main Menu.");
                    return;

                default:
                    System.out.println("Please enter a valid choice.");
                    break;

            }
        }

    }
}
