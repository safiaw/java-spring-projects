import api.HotelResource;
import model.Customer;
import model.IRoom;
import model.Reservation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * This class is MainMenu UI class that displays all the actions user can perform in the hotel application and redirect user actions
 * to the next resource layer
 */
public class MainMenu {
    private static final HotelResource hotelResource = HotelResource.getInstance();
    public static final Scanner scanner = new Scanner(System.in);
    public static void mainMenu() {
        while (true) {
            System.out.println("Welcome to the Hotel Reservation Application");
            System.out.println("------------------------------------------------");
            System.out.println("1. Find and reserve a room");
            System.out.println("2. See my reservations");
            System.out.println("3. Create an account");
            System.out.println("4. Admin");
            System.out.println("5. Exit");
            System.out.println("------------------------------------------------");
            System.out.println("Please select a number for the menu option");

            try {

                    int choice = scanner.nextInt();
                    scanner.nextLine();
                    switch (choice) {
                        case 1:

                            // ask user for check in and check out dates

                            Date checkInDate = null;
                            Date checkOutDate = null;
                            do {
                                System.out.println("Enter the checkIn date in format YYYY-MM-DD eg., 2024-10-01 (must be later than the current date): ");
                                String checkInDateString = scanner.nextLine();
                                checkInDate = checkDateFormat(checkInDateString);
                            } while (checkInDate == null || !checkInDate.after(new Date()));

                            do {
                                System.out.println("Enter the checkOut date in format YYYY-MM-DD eg., 2024-10-01 (must be later than the checkInDate): ");
                                String checkOutDateString = scanner.nextLine();
                                checkOutDate = checkDateFormat(checkOutDateString);
                            } while (checkOutDate == null || !checkOutDate.after(checkInDate));

                            // get room type user is searching for

                            String searchRoomType = getSearchRoomType();

                            // find all available rooms based on the search type

                            ArrayList<IRoom> foundRooms = hotelResource.findARoom(checkInDate, checkOutDate, searchRoomType);

                            // if no rooms are available in the given date range then ask for next days of availability

                            if (foundRooms.isEmpty()) {
                                System.out.println("No available rooms in the given date range.");
                                int afterDays = getAfterDays();
                                checkInDate = MainMenu.getNextDate(checkInDate, afterDays);
                                checkOutDate = MainMenu.getNextDate(checkOutDate, afterDays);
                                foundRooms = hotelResource.findARoom(checkInDate, checkOutDate, searchRoomType);

                            }
                            if (foundRooms.isEmpty()) {
                                System.out.println("No rooms are available.");
                                continue;
                            }
                            // display all available rooms of user's room search type choice
                            int roomChoice = getRoomChoice(foundRooms);

                            // display user's selected room
                            System.out.println("Selected room is: " + foundRooms.get(roomChoice - 1));


                            // book the selected room
                            while (true)
                            {
                                System.out.println("Do you want to book this room. Enter y or n");
                                String userInput = scanner.nextLine().toLowerCase();
                                if (userInput.equals("y"))
                                {
                                    Reservation reservation = bookSelectedRoom(foundRooms.get(roomChoice - 1), checkInDate, checkOutDate);
                                    if (reservation != null){
                                        System.out.println("Successfully reserved a room.");
                                    }
                                    else {
                                        System.out.println("Failed to reserve a room.");
                                    }
                                    break;
                                }
                                else if(userInput.equals("n"))
                                {
                                    System.out.println("Returning to Main Menu");
                                    break;
                                }
                                else
                                {
                                    System.out.println("Please enter Y(yes) or N(no).");
                                }
                            }
                            break;
                        case 2:
                            System.out.println("Enter email: ");
                            String customerEmail = scanner.nextLine();
                            ArrayList<Reservation> customerReservations = hotelResource.getCustomersReservation(customerEmail);
                            if (customerReservations.isEmpty()) {
                                System.out.println("You have no reservations.");
                            } else {
                                System.out.println("Your reservations are:");
                                for (Reservation reservation : customerReservations) {
                                    System.out.println(reservation);
                                }
                            }
                            break;
                        case 3:
                            createANewAccount();
                            break;
                        case 4:
                            AdminMenu.adminMenu();
                            break;
                        case 5:
                            System.out.println("Exiting the system.");
                            scanner.close();
                            return;
                        default:
                            System.out.println("Please enter a valid choice.");
                            break;
                    }
                }
            catch(InputMismatchException e)
            {
                System.out.println("Invalid input choice! Please enter a valid option."+e.getMessage());
                scanner.next();
            }

            }

    }

    /**
     * This method is used by MainMenu UI class in case 1 where user want to find and reserve a room in a given date range
     * This method converts user entered checkIn and checkOut date to future recommended checkIn and checkOut dates after adding a given
     * number of days
     * @param date - checkIn or checkOut date
     * @param afterDays - add number of days to checkIn or checkOut date
     * @return - return updated date
     */
    public static Date getNextDate(Date date, int afterDays){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH,afterDays);
        return calendar.getTime();

    }

    /**
     * This method validates user entered email by matching it with a pre-defined email regex pattern
     * @param email - email address in string
     * @return - return true or false stating valid or invalid email address
     */
    public static boolean isValidEmail(String email){
        // valid email address -  user.name@domain.abc
        // invalid email address - user.name@domain.co.as.xy
        String emailRegEx = "^[a-zA-Z0-9_+*-]+(?:\\.[a-zA-Z0-9_+*-]+)*@[a-zA-Z0-9_+*-]+\\.[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegEx);
        return pattern.matcher(email).matches();
    }

    public static void createANewAccount(){

        boolean isValid;
        String email;
        do{
            System.out.println("Enter email:");
            email = scanner.nextLine();
            isValid = isValidEmail(email);
            if(!isValid){ System.out.println("Invalid email! Please enter a valid email address eg., user@domain.com");}
        }while(!isValid);

        System.out.println("Enter first name: ");
        String firstName = scanner.nextLine();
        System.out.println("Enter last name: ");
        String lastName = scanner.nextLine();
        hotelResource.createACustomer(email, firstName, lastName);

    }

    public static Date checkDateFormat(String inputDate){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        Date formattedDate = null;
        try {
            formattedDate = dateFormat.parse(inputDate);
        }
        catch (ParseException e){
            System.out.println("Invalid date format! Please enter in YYYY-MM-DD format. eg., 2024-12-01");
            return null;
        }
        return formattedDate;
    }

    public static String getSearchRoomType()
    {

        String searchRoom;
        while (true) {
            System.out.println("Do you want to search for: ");
            System.out.println("1. Free rooms.");
            System.out.println("2. Paid rooms.");
            System.out.println("3. All rooms.");
            System.out.println("Please select a number for the rooms options.");
            int searchType;
            try {
                searchType = scanner.nextInt();
                scanner.nextLine();
            }
            catch (InputMismatchException e)
            {
                System.out.println("Room search input is not a valid integer option.");
                scanner.next();
                continue;
            }
            if (searchType == 1)
            {
                searchRoom = "free";
                break;
            }
            else if (searchType == 2)
            {
                searchRoom = "paid";
                break;
            }
            else if (searchType == 3)
            {
                searchRoom = "all";
                break;
            }
            else
            {
                System.out.println("Please select correct search option from 1-3.");
            }

        }
        return searchRoom;
    }

    public static int getRoomChoice(ArrayList<IRoom> foundRooms)
    {

        int roomChoice;
        while (true)
        {
            System.out.println("Available rooms are: ");
            for (int i = 0; i < foundRooms.size(); i++) {
                System.out.println(i + 1 + ": " + foundRooms.get(i));
            }
            int n = foundRooms.size();
            System.out.println("Select the room options from 1 - " +n);
            try {
                roomChoice = scanner.nextInt();
                scanner.nextLine();
            }
            catch(InputMismatchException e)
            {
                System.out.println("Please enter a valid integer input from the given option.");
                scanner.next();
                continue;
            }
            if (roomChoice < 1 || roomChoice > foundRooms.size()) {
                System.out.println("Please select room from the given options.");
            }
            else
            {
                break;
            }
        }
        return roomChoice;
    }

    public static Reservation bookSelectedRoom(IRoom room, Date checkInDate, Date checkOutDate)
    {

        boolean isValid = false;
        String customerEmail = "";
        while (!isValid)
        {
            System.out.println("Enter email: ");
            customerEmail = scanner.nextLine();
            isValid = isValidEmail(customerEmail);
            if (!isValid)
            {
                System.out.println("Invalid email! Please enter a valid email address. user@domain.com");
            }
        }

        Customer customer = hotelResource.getCustomer(customerEmail);
        if (customer == null)
        {
            System.out.println("Customer does not exist. Please create an account.");
            createANewAccount();
        }
        Reservation reservation = hotelResource.bookARoom(customerEmail, room, checkInDate, checkOutDate);
        return reservation;
    }

    public static int getAfterDays(){


        int afterDays;
        while(true){
            System.out.println("Enter how many days after do you want to recommend rooms.");
            try{
                afterDays = scanner.nextInt();
                scanner.nextLine();
                break;
            }
            catch(InputMismatchException e){
                System.out.println("Please enter a valid integer option between (1-30) days");
                scanner.next();
            }
        }

        return afterDays;
    }
}


