
package campus.model;

public class TimetableEntry {
    private int id;
    private String subject;
    private String faculty;
    private String day;
    private String timeSlot;
    private String roomNumber;

    public TimetableEntry(int id, String subject, String faculty,
            String day, String timeSlot, String roomNumber) {
        this.id = id;
        this.subject = subject;
        this.faculty = faculty;
        this.day = day;
        this.timeSlot = timeSlot;
        this.roomNumber = roomNumber;
    }

    public int getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public String getFaculty() {
        return faculty;
    }

    public String getDay() {
        return day;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setId(int id) {
        this.id = id;
    }
}
