package LIC.UC04v1.model;

import LIC.UC04v1.controllers.Location;
import LIC.UC04v1.controllers.Specialty;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Doctor {

    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    @Id
    private String id;
    //maybe change to a collection of clerkships so they can change it in the future
    //have a restraint that kayla can edit the number of clerkships a doctor can have
    @OneToMany(mappedBy = "doctor", cascade=CascadeType.ALL)
    private List<Clerkship> clerkship = new ArrayList<Clerkship>();

    private String name;
    private String email;
    private boolean available = true;
    private String availabilities;
    private int numberOfDaysAvail;
    private Specialty specialty;
    private Location location;
    private int numStu = 1;
    private int hasStu = 0;
    @OneToOne
    private Student phase1Stu;
    private Boolean hasPhase1 = false;

    public Student getPhase1Stu() {
        return phase1Stu;
    }

    public void setPhase1Stu(Student phase1Stu) {
        this.phase1Stu = phase1Stu;
    }

    public Boolean getHasPhase1() {
        return hasPhase1;
    }

    public void setHasPhase1(Boolean hasPhase1) {
        this.hasPhase1 = hasPhase1;
    }

    public int getHasStu() {
        return hasStu;
    }

    public void setHasStu(int hasStu) {
        this.hasStu = hasStu;
    }

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

    public void addClerkship(Clerkship clerk) {
        clerkship.add(clerk);
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

    public List<Clerkship> getClerkship() {
        return clerkship;
    }

    public void setClerkship(List<Clerkship> clerkship) {
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

