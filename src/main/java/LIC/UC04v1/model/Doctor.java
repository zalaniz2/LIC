package LIC.UC04v1.model;

import lombok.*;

import javax.persistence.*;

@Data
@Entity
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    //maybe change to a collection of clerkships so they can change it in the future
    //have a restraint that kayla can edit the number of clerkships a doctor can have
    @OneToOne
    private Clerkship clerkship;
    private String name;
    private String email;
    private String profession;
    private String Location;
    private String available;


    public String getLocation() { return Location; }

    public void setLocation(String location) { Location = location; }
    public String isAvailable() {
        return available;
    }

    public Doctor(){
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

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }

    public Doctor(String name){
        this.name = name;
    }

    public Doctor(int id) {
        this.id = id;
    }
}
