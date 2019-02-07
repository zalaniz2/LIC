package LIC.UC04v1.model;

import javax.persistence.*;

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

    public Boolean getMM() {
        return MM;
    }

    public void setMM(Boolean MM) {
        this.MM = MM;
    }

    public Boolean getMA() {
        return MA;
    }

    public void setMA(Boolean MA) {
        this.MA = MA;
    }

    public Boolean getTM() {
        return TM;
    }

    public void setTM(Boolean TM) {
        this.TM = TM;
    }

    public Boolean getTA() {
        return TA;
    }

    public void setTA(Boolean TA) {
        this.TA = TA;
    }

    public Boolean getWM() {
        return WM;
    }

    public void setWM(Boolean WM) {
        this.WM = WM;
    }

    public Boolean getWA() {
        return WA;
    }

    public void setWA(Boolean WA) {
        this.WA = WA;
    }

    public Boolean getRM() {
        return RM;
    }

    public void setRM(Boolean RM) {
        this.RM = RM;
    }

    public Boolean getRA() {
        return RA;
    }

    public void setRA(Boolean RA) {
        this.RA = RA;
    }

    public Boolean getFM() {
        return FM;
    }

    public void setFM(Boolean FM) {
        this.FM = FM;
    }

    public Boolean getFA() {
        return FA;
    }

    public void setFA(Boolean FA) {
        this.FA = FA;
    }

    public Boolean getAM() {
        return AM;
    }

    public void setAM(Boolean AM) {
        this.AM = AM;
    }

    public Boolean getAA() {
        return AA;
    }

    public void setAA(Boolean AA) {
        this.AA = AA;
    }

    public Boolean getUM() {
        return UM;
    }

    public void setUM(Boolean UM) {
        this.UM = UM;
    }

    public Boolean getUA() {
        return UA;
    }

    public void setUA(Boolean UA) {
        this.UA = UA;
    }
    public String getLocation() { return Location; }

    public void setLocation(String location) { Location = location; }
    public String isAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
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

    public Doctor(){

    }

    public Doctor(String name){
        this.name = name;
    }

    public Doctor(int id) {
        this.id = id;
    }
}
