package LIC.UC04v1.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "role_id")
    private int id;
    //@ManyToMany(cascade = CascadeType.ALL)
    @Column(name = "role")
    private String role;
}