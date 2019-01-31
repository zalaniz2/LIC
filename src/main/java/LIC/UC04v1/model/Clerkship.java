package LIC.UC04v1.model;

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
    private String Time;


    public Clerkship(){}

    public Clerkship(int id) {
        this.id = id;
    }
}
