package LIC.UC04v1.model;

import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.sql.Time;
import java.util.Date;

@Entity
public class Clerkship {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @ManyToOne//(cascade = CascadeType.ALL)
    //@JoinColumn(name = "Student_ID123", referencedColumnName = "ID")
    private Student student;
    @OneToOne
    private Doctor doctor;
    private String Title;
    private String Location;
    private String Time; //(day) used for testing purposes and should be deleted
    private Date date;
    private java.sql.Time startTime;
    private java.sql.Time endTime;
    private String studentName;
    private String Description;

    public String getDescription() { return Description; }

    public void setDescription(String description) { Description = description; }

    public String getTime() { return Time; }

    public void setTime(String time) { Time = time; }

    public Date getDate() { return date; }

    public void setDate(Date date) { this.date = date; }

    public java.sql.Time getStartTime() { return startTime; }

    public void setStartTime(java.sql.Time startTime) { this.startTime = startTime; }

    public java.sql.Time getEndTime() { return endTime; }

    public void setEndTime(java.sql.Time endTime) { this.endTime = endTime; }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

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
