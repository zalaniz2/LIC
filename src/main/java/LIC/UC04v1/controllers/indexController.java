package LIC.UC04v1.controllers;

import LIC.UC04v1.model.Doctor;
import LIC.UC04v1.model.Clerkship;
import LIC.UC04v1.model.Student;
import LIC.UC04v1.model.sortDoctorByAvailDates;
import LIC.UC04v1.repositories.DoctorRepository;
import LIC.UC04v1.repositories.ClerkshipRepository;
import LIC.UC04v1.repositories.StudentRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.print.Doc;
import java.util.*;


@Controller
public class indexController {

    private DoctorRepository doctorRepository;
    private ClerkshipRepository clerkshipRepository;
    private StudentRepository studentRepository;
    private MiscMethods misc = new MiscMethods();

    public indexController(DoctorRepository doctorRepository, ClerkshipRepository clerkshipRepository, StudentRepository studentRepository) {
        this.doctorRepository = doctorRepository;
        this.clerkshipRepository = clerkshipRepository;
        this.studentRepository = studentRepository;
    }

    public int[] getAvailabilities(String profession, String location) {

        int count = 0;
        int counts[] = new int[24];

        for (Doctor doc : doctorRepository.findAll()) {

            if ((doc.getSpecialty().equals(profession)) && doc.getLocation().equals(location)) {

                for (int i = 0; i < 24; i++) {

                    if (doc.getAvailabilities().charAt(i) == '1' && doc.isAvailable()) {
                        counts[i] = counts[i] + 1;
                    }
                }
                count++;
            }
        }

        return counts;

    }

    public ArrayList<Integer> addScheduleDays(Schedule s) {

        int day = 0;
        ArrayList<Integer> days = new ArrayList<Integer>();

        for (int i = 0; i < 7; i++) {

            day = getClerkshipDay(i, s);
            days.add(day);

        }

        return days;

    }


    public int getClerkshipDay(int num, Schedule s) {

        int day = 0;

        switch (num) {
            case 0:
                day = Integer.parseInt(s.getSurgery());
                break;
            case 1:
                day = Integer.parseInt(s.getFamilymedicine());
                break;
            case 2:
                day = Integer.parseInt(s.getInternalmedicine());
                break;
            case 3:
                day = Integer.parseInt(s.getNeurology());
                break;
            case 4:
                day = Integer.parseInt(s.getObgyn());
                break;
            case 5:
                day = Integer.parseInt(s.getPediatrics());
                break;
            case 6:
                day = Integer.parseInt(s.getPsychiatry());
                break;
            default:
                day = -1;
                break;
        }

        return day;

    }

    public String getClerkshipLocation(int num, Schedule s) {

        String loc;

        switch (num) {
            case 0:
                loc = s.getSurgeryLocation();
                break;
            case 1:
                loc = s.getFamilymedicineLocation();
                break;
            case 2:
                loc = s.getInternalmedicineLocation();
                break;
            case 3:
                loc = s.getNeurologyLocation();
                break;
            case 4:
                loc = s.getObgynLocation();
                break;
            case 5:
                loc = s.getPediatricsLocation();
                break;
            case 6:
                loc = s.getPsychiatryLocation();
                break;
            default:
                loc = null;
                break;
        }

        return loc;

    }

    public String getSpecialty(int num) {

        String prof = "";
        switch (num) {
            case 0:
                prof = "Surgery";
                break;
            case 1:
                prof = "Family Medicine";
                break;
            case 2:
                prof = "Internal Medicine";
                break;
            case 3:
                prof = "Neurology";
                break;
            case 4:
                prof = "OBGYN";
                break;
            case 5:
                prof = "Pediatrics";
                break;
            case 6:
                prof = "Psychiatry";
                break;
            default:
                prof = "error";
                break;
        }

        return prof;
    }




    @RequestMapping(path = "/{stuID}")
    public String neuro(Model model, @PathVariable String stuID){
        Student stu = studentRepository.findById(stuID).orElse(null);
        if (stu!=null) {
            model.addAttribute("stu", stu);
        }
        return "index";
    }



    /*
    @RequestMapping(path = "/")
    public String home() {

        return "index";
    }
    */




    @GetMapping(path = "/admin")
    public String admin() {

        System.out.println("Admin page.");
        return "admin";

    }


    @RequestMapping(value = "/getdocs", method = RequestMethod.POST)

    public @ResponseBody
    int[] getDocs(@RequestBody Profession p) {

        String profession = p.getprofession();
        String location = p.getLocation();
        System.out.println(profession);
        System.out.println(p.getLocation());


        return getAvailabilities(profession, location);
    }


    @RequestMapping(value = "/sendschedule", method = RequestMethod.POST)

    public @ResponseBody
    boolean sendSchedule(@RequestBody Schedule s) {

        s.schedToString();
        System.out.println(s.getNeurologyLocation() + "neuro location");
        System.out.println(s.getObgynLocation() + "obgyn location");
        System.out.println(s.getFamilymedicineLocation() + "fm location");
        System.out.println(s.getInternalmedicineLocation() + "im location");
        System.out.println(s.getSurgeryLocation() + "surg location");
        System.out.println(s.getPsychiatryLocation() + "psych location");
        System.out.println(s.getPediatricsLocation() + "pedi location");



        Student stu = studentRepository.findById(s.getId()).orElse(null);
        List<Doctor> docs;
        Map<String, Clerkship> stuSched = new HashMap<>();
        for( int i = 0; i<7; i++){
            String spe = getSpecialty(i);
            Specialty specialty = misc.toSpecialty(spe);
            String loc = getClerkshipLocation(i, s);
            docs = doctorRepository.findBySpecialtyInTextAndAvailableAndLocation(specialty, true, loc);

            //sorting stuffs
            for (Doctor doc: docs) {
                doc.setNumberOfDaysAvail();
            }
            Collections.sort(docs, new sortDoctorByAvailDates());
            //end sorting stuffs



            for (Doctor doc: docs) {
                if (doc.getAvailabilities().charAt(getClerkshipDay(i,s ))=='1'){
                    Clerkship clerk = new Clerkship();
                    clerk.setStudent(stu);
                    clerk.setDoctor(doc);
                    clerk.setTitle(spe);
                    clerk.setTime(misc.toTimeSlot(getClerkshipDay(i,s)));
                    clerk.setTimeInt1(getClerkshipDay(i,s));
                    clerk.setSpecialty(specialty);
                    clerk.setLocation(doc.getLocation());
                    if (specialty==Specialty.FamilyMedicine||specialty==Specialty.Pediatrics||specialty==Specialty.Surgery||specialty==Specialty.InternalMedicine) {
                        clerk.setTime2(misc.getOtherTime(misc.toTimeSlot(getClerkshipDay(i,s))));
                        clerk.setTimeInt2(getClerkshipDay(i,s)-12);
                        clerk.setDay(getClerkshipDay(i,s)-12);
                    } else {
                        clerk.setDay(getClerkshipDay(i,s));
                    }
                    clerkshipRepository.save(clerk);
                    doc.setClerkship(clerk);
                    doc.setAvailable(false);
                    doctorRepository.save(doc);
                    stuSched.put(spe,clerk);
                    break;
                }
            }

        }
        stu.setClerkships(stuSched);
        studentRepository.save(stu);

        if (stuSched.size()!=7) {
            System.out.println("fail at student # of specialty: "+stuSched.size());
        }
        else{
            System.out.println("student scehdule success");

        }



        //createStudent();

        return true;


    }

}



class Profession{

    private String profession;
    private String location;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getprofession(){
        return profession;
    }

    public void setprofession(String profession){
        this.profession = profession;
    }

}

class Schedule{
    private String familymedicineLocation;
    private String internalmedicineLocation;
    private String neurologyLocation;
    private String obgynLocation;
    private String pediatricsLocation;
    private String psychiatryLocation;
    private String surgeryLocation;
    private String surgery;
    private String familymedicine;
    private String id;
    private String internalmedicine;
    private String neurology;
    private String obgyn;
    private String pediatrics;
    private String psychiatry;

    public String getSurgeryLocation() {
        return surgeryLocation;
    }

    public void setSurgeryLocation(String surgeryLocation) {
        this.surgeryLocation = surgeryLocation;
    }

    public String getFamilymedicineLocation() {
        return familymedicineLocation;
    }

    public void setFamilymedicineLocation(String familymedicineLocation) {
        this.familymedicineLocation = familymedicineLocation;
    }

    public String getInternalmedicineLocation() {
        return internalmedicineLocation;
    }

    public void setInternalmedicineLocation(String internalmedicineLocation) {
        this.internalmedicineLocation = internalmedicineLocation;
    }

    public String getNeurologyLocation() {
        return neurologyLocation;
    }

    public void setNeurologyLocation(String neurologyLocation) {
        this.neurologyLocation = neurologyLocation;
    }

    public String getObgynLocation() {
        return obgynLocation;
    }

    public void setObgynLocation(String obgynLocation) {
        this.obgynLocation = obgynLocation;
    }

    public String getPediatricsLocation() {
        return pediatricsLocation;
    }

    public void setPediatricsLocation(String pediatricsLocation) {
        this.pediatricsLocation = pediatricsLocation;
    }

    public String getPsychiatryLocation() {
        return psychiatryLocation;
    }

    public void setPsychiatryLocation(String psychiatryLocation) {
        this.psychiatryLocation = psychiatryLocation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSurgery() {
        return surgery;
    }

    public void setSurgery(String surgery) {
        this.surgery = surgery;
    }

    public String getFamilymedicine() {
        return familymedicine;
    }

    public void setFamilymedicine(String familymedicine) {
        this.familymedicine = familymedicine;
    }

    public String getInternalmedicine() {
        return internalmedicine;
    }

    public void setInternalmedicine(String internalmedicine) {
        this.internalmedicine = internalmedicine;
    }

    public String getNeurology() {
        return neurology;
    }

    public void setNeurology(String neurology) {
        this.neurology = neurology;
    }

    public String getObgyn() {
        return obgyn;
    }

    public void setObgyn(String obgyn) {
        this.obgyn = obgyn;
    }

    public String getPediatrics() {
        return pediatrics;
    }

    public void setPediatrics(String pediatrics) {
        this.pediatrics = pediatrics;
    }

    public String getPsychiatry() {
        return psychiatry;
    }

    public void setPsychiatry(String psychiatry) {
        this.psychiatry = psychiatry;
    }
    public void schedToString(){

        System.out.println("Surgery on day " + getSurgery());
        System.out.println("Family Medicine on day " + getFamilymedicine());
        System.out.println("Internal Medicine on day " + getInternalmedicine());
        System.out.println("Neurology on day " + getNeurology());
        System.out.println("OBGYN on day " + getObgyn());
        System.out.println("Pediatrics on day " + getPediatrics());
        System.out.println("Psychiatry on day " + getPsychiatry());
        System.out.println("ID: " + getId());


    }


}


