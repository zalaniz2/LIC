package LIC.UC04v1.model;

import lombok.*;

import javax.persistence.*;

@Data
@Entity
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @OneToOne
    private Clerkship clerkship;
    private String name;
    private String email;
    private String profession;
    private String Location;
    private String available;
    private Boolean MM;


    private Boolean MA;
    private Boolean TM;
    private Boolean TA;
    private Boolean WM;
    private Boolean WA;
    private Boolean RM;
    private Boolean RA;
    private Boolean FM;
    private Boolean FA;
    private Boolean AM;
    private Boolean AA;
    private Boolean UM;
    private Boolean UA;

    public String getLocation() { return Location; }

    public void setLocation(String location) { Location = location; }
    public String isAvailable() {
        return available;
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
