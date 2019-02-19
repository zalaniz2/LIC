package LIC.UC04v1.model;

import LIC.UC04v1.controllers.Specialty;
import LIC.UC04v1.controllers.TimeSlot;

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
    private Date date;
    private java.sql.Time startTime;
    private java.sql.Time endTime;
    private String studentName;
    private String Description;

    public String getDescription() { return Description; }

    public void setDescription(String description) { Description = description; }

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
    private TimeSlot time;
    private TimeSlot time2;
    private int timeInt1;
    private int timeInt2;
    private Specialty specialty;
    private int day;

    public int getTimeInt1() {
        return timeInt1;
    }

    public void setTimeInt1(int timeInt1) {
        this.timeInt1 = timeInt1;
    }

    public int getTimeInt2() {
        return timeInt2;
    }

    public void setTimeInt2(int timeInt2) {
        this.timeInt2 = timeInt2;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public Specialty getSpecialty() {
        return specialty;
    }

    public void setSpecialty(Specialty specialty) {
        this.specialty = specialty;
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

    public TimeSlot getTime2() {
        return time2;
    }

    public void setTime2(TimeSlot time2) {
        this.time2 = time2;
    }

    public Clerkship(int id) {
        this.id = id;
    }

    public void setTime(TimeSlot time) {
        this.time = time;
    }

    public String getStudentName(){
        return this.student.getName();
    }

}
