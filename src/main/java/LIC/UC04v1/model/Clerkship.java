package LIC.UC04v1.model;

import LIC.UC04v1.controllers.Specialty;
import LIC.UC04v1.controllers.TimeSlot;

import javax.persistence.*;

@Entity
public class Clerkship {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @ManyToOne
    private Student student;
    @OneToOne
    private Doctor doctor;
    private TimeSlot time;
    private TimeSlot time2;
    private Specialty specialty;
    private int day;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public TimeSlot getTime() {
        return time;
    }

    public void setTime(TimeSlot time) {
        this.time = time;
    }
}
