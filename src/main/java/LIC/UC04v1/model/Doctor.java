package LIC.UC04v1.model;

import LIC.UC04v1.controllers.Specialty;
import LIC.UC04v1.controllers.TimeSlot;

import javax.persistence.*;
import java.util.ArrayList;

@Entity
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @OneToOne
    private Clerkship clerkship;
    private String name;
    private String email;
    private String specialty;
    private boolean available = true;
    private String availabilities;
    private Specialty specialtyInText;

    public Specialty getSpecialtyInText() {
        return specialtyInText;
    }

    public void setSpecialtyInText(Specialty specialtyInText) {
        this.specialtyInText = specialtyInText;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getAvailabilities() {
        return availabilities;
    }

    public void setAvailabilities(String availabilities) {
        this.availabilities = availabilities;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public Doctor(){

    }

    public Doctor(String name){
        this.name = name;
    }

    public Doctor(int id) {
        this.id = id;
    }
}
