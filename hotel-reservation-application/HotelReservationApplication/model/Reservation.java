package model;

import java.util.Date;

/**
 * This is a Reservation model class that defines reservation attributes and its behaviour
 */
public class Reservation {
    private final Customer customer;
    private final IRoom room;
    private final Date checkInDate;
    private final Date checkOutDate;

    public Reservation(Customer customer, IRoom room, Date checkIn, Date checkOut){
        this.customer = customer;
        this.room = room;
        this.checkInDate = checkIn;
        this.checkOutDate = checkOut;
    }

    /**
     * This method checks for conflicting reservations
     * @param checkIn - checkIn in Date type
     * @param checkOut - checkOut in Date type
     * @return - return true or false when the given date range conflict or not with other reservations
     */
    public boolean conflicts(Date checkIn, Date checkOut){
        return !(checkInDate.after(checkOut)) && !(checkOutDate.before(checkIn));
    }

    /* getters */
    public Customer getCustomer() {
        return customer;
    }

    public IRoom getRoom() {
        return room;
    }

    public Date getCheckInDate() {
        return checkInDate;
    }

    public Date getCheckOutDate() {
        return checkOutDate;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "customer=" + customer +
                ", room=" + room +
                ", checkInDate=" + checkInDate +
                ", checkOutDate=" + checkOutDate +
                '}';
    }
}
