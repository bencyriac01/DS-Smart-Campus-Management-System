
package campus.model;

public class Student {
    private String registerNumber;
    private String name;
    private String department;
    private int semester;

    public Student(String registerNumber, String name,
            String department, int semester) {
        this.registerNumber = registerNumber;
        this.name = name;
        this.department = department;
        this.semester = semester;
    }

    public String getRegisterNumber() {
        return registerNumber;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public int getSemester() {
        return semester;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }
}
