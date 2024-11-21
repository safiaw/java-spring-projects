package model;

/**
 * This is an IRoom interface declaring common behaviours of all type of rooms
 */
public interface IRoom {
    public String getRoomNumber();
    public double getRoomPrice();
    public RoomType getRoomType();
    public boolean isFree();
}
