package model;

/**
 * This is a Room model class that implements IRoom interface and define its attributes
 */
public class Room implements IRoom{
    private String roomNumber;
    private double roomPrice;
    private RoomType roomType;

    public Room(String roomNumber, Double roomPrice, RoomType roomType){
         this.roomNumber = roomNumber;
         this.roomPrice = roomPrice;
         this.roomType = roomType;
    }

    /* Getters and Setters */

    @Override
    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    @Override
    public double getRoomPrice() {
        return roomPrice;
    }

    public void setRoomPrice(Double roomPrice) {
        this.roomPrice = roomPrice;
    }

    @Override
    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    @Override
    public boolean isFree() {
        return false;
    }


    @Override
    public String toString() {
        return "Room{" +
                "roomNumber='" + roomNumber + '\'' +
                ", roomPrice=$" + roomPrice +
                ", roomType=" + roomType +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true;}
        if (!( obj instanceof Room)) { return false;}
        Room room = (Room) obj;
        return this.roomNumber.equals(room.roomNumber);
    }

    @Override
    public int hashCode() {
        return this.roomNumber.hashCode();
    }
}
