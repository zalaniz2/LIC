package LIC.UC04v1.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

@Entity
public class Student {
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    @Id
    private String id;
    private String email;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Clerkship> getClerkships() {
        return clerkships;
    }

    public void setClerkships(List<Clerkship> clerkships) {
        this.clerkships = clerkships;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    @OneToMany(mappedBy = "student")
    private List<Clerkship> clerkships;
    private String Name;

    public Student(){}

}
