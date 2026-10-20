
package campus.model;

public class Attendance {
    private int id;
    private String studentId;
    private String studentName;
    private String subject;
    private String date;
    private String status;

    public Attendance(int id, String studentId, String studentName,
            String subject, String date, String status) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.subject = subject;
        this.date = date;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getSubject() {
        return subject;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public void setId(int id) {
        this.id = id;
    }
}
