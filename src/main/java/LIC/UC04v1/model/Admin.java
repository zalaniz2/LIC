package LIC.UC04v1.model;

import LIC.UC04v1.controllers.Location;
import LIC.UC04v1.controllers.Specialty;
import LIC.UC04v1.controllers.TimeSlot;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String stuFile;
    private String docFile;

    public String getStuFile() {
        return stuFile;
    }

    public int getID() {
        return id;
    }


    public void setStuFile(String stuFile) {
        this.stuFile = stuFile;
    }

    public String getDocFile() {
        return docFile;
    }

    public void setDocFile(String docFile) {
        this.docFile = docFile;
    }
}
