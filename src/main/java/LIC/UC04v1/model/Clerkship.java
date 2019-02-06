package LIC.UC04v1.model;

import org.hibernate.annotations.Cascade;

import javax.persistence.*;

@Entity
public class Clerkship {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @ManyToOne(cascade = CascadeType.ALL)
    //@JoinColumn(name = "Student_ID123", referencedColumnName = "ID")
    private Student student;
    @OneToOne
    private Doctor doctor;
    private String Time;
    private String Title;
    private String Location;

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    private String studentName;

    public String getLocation() { return Location; }

    public void setLocation(String location) { Location = location; }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public int getId() {
        return id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Clerkship(){}

    public Clerkship(int id) {
        this.id = id;
    }

    public String getStudentName(){
        return this.student.getName();
    }

}
