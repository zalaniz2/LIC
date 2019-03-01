package LIC.UC04v1.model;

import LIC.UC04v1.controllers.Location;
import LIC.UC04v1.controllers.Specialty;
import LIC.UC04v1.controllers.TimeSlot;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Comparator;

@Data
@Entity
public class Doctor {

    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    @Id
    private String id;
    //maybe change to a collection of clerkships so they can change it in the future
    //have a restraint that kayla can edit the number of clerkships a doctor can have
    @OneToOne
    private Clerkship clerkship;
    private String name;
    private String email;
    //private String specialty;
    private boolean available = true;
    private String availabilities;
    private int numberOfDaysAvail;
    private Specialty specialty;
    //private String location;
    private Location location;
    private int numStu = 1;

    public int getNumStu() {
        return numStu;
    }

    public void setNumStu(int numStu) {
        this.numStu = numStu;
    }

    public Doctor() {}

    public Specialty getSpecialty() {
        return specialty;
    }

    public void setSpecialty(Specialty specialty) {
        this.specialty = specialty;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setNumberOfDaysAvail(int numberOfDaysAvail) {
        this.numberOfDaysAvail = numberOfDaysAvail;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getAvailabilities() {
        return availabilities;
    }

    public void setAvailabilities(String availabilities) {

        this.availabilities = availabilities;
        setNumberOfDaysAvail();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Clerkship getClerkship() {
        return clerkship;
    }

    public void setClerkship(Clerkship clerkship) {
        this.clerkship = clerkship;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Doctor(String name){
        this.name = name;
    }

    public int getNumberOfDaysAvail() {
        return numberOfDaysAvail;
    }

    public void setNumberOfDaysAvail() {
        int a = 0;
        for (int i = 0; i <availabilities.length();i++){
            if (availabilities.charAt(i)=='1'){
                a++;
            }
        }
        this.numberOfDaysAvail = a;
    }

}

