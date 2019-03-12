package LIC.UC04v1.model;

import LIC.UC04v1.controllers.Location;
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
    private Location Location;
    private Date date;
    private String startTime;
    private String endTime;
    private String Description;
    private TimeSlot time;
    private TimeSlot time2;
    private Specialty specialty;
    private int day;
    private String eventType = "Clinic";

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getDescription() { return Description; }

    public void setDescription(String description) { Description = description; }

    public Date getDate() { return date; }

    public void setDate(Date date) { this.date = date; }


    public LIC.UC04v1.controllers.Location getLocation() {
        return Location;
    }

    public void setLocation(LIC.UC04v1.controllers.Location location) {
        Location = location;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public int getId() {
        return id;
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

    public String getDoctorName() { return this.doctor.getName();}

    public String getTime(){
        switch (day){
            case 0: return "Monday";
            case 1: return "Monday";
            case 2: return "Tuesday";
            case 3: return "Tuesday";
            case 4: return "Wednesday";
            case 5: return "Wednesday";
            case 6: return "Thursday";
            case 7: return "Thursday";
            case 8: return "Friday";
            case 9: return "Friday";
            case 10: return "Saturday";
            case 11: return "Saturday";
            case 12: return "Monday";
            case 13: return "Monday";
            case 14: return "Tuesday";
            case 15: return "Tuesday";
            case 16: return "Wednesday";
            case 17: return "Wednesday";
            case 18: return "Thursday";
            case 19: return "Thursday";
            case 20: return "Friday";
            case 21: return "Friday";
            case 22: return "Saturday";
            case 23: return "Saturday";
        }
        return "false";
    }

    public String getStartTime(){
        if(day%2 == 0){ this.startTime = "08:00:00"; }
        else{ this.startTime = "13:00:00"; }
        return this.startTime;
    }

    public String getEndTime(){
        if(day%2 == 0){ this.endTime= "12:00:00"; }
        else{ this.endTime= "17:00:00"; }
        return this.endTime;
    }

    public String getTimeWeek1(){ return "Week_1";}

    public String getTimeWeek2(){ return "Week_2";}

}
