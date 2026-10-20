
package campus.model;

public class Classroom {
    private String roomNumber;
    private String building;
    private int capacity;
    private String roomType;

    public Classroom(String roomNumber, String building,
            int capacity, String roomType) {
        this.roomNumber = roomNumber;
        this.building = building;
        this.capacity = capacity;
        this.roomType = roomType;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getBuilding() {
        return building;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }
}
