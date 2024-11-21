package model;

/**
 * This is a FreeRoom concrete class that inherits Room parent class
 */
public class FreeRoom extends Room{

    public FreeRoom(String roomNumber, RoomType roomType){
        super(roomNumber, 0.0, roomType);
    }

    @Override
    public boolean isFree() {
        return true;
    }

    @Override
    public String toString() {
        return "FreeRoom{" +
                "roomNumber='" + getRoomNumber() + '\'' +
                ", roomPrice=$" + getRoomPrice() +
                ", roomType=" + getRoomType() +
                '}';

    }
}
