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
public class
indexController {

    private DoctorRepository doctorRepository;
    private ClerkshipRepository clerkshipRepository;
    private StudentRepository studentRepository;


    public indexController(DoctorRepository doctorRepository, ClerkshipRepository clerkshipRepository, StudentRepository studentRepository){
        this.doctorRepository = doctorRepository;
        this.clerkshipRepository = clerkshipRepository;
        this.studentRepository = studentRepository;
    }

    public int[] getAvailabilities(String profession){

        int count = 0;
        int counts[] = new int[24];

        for(Doctor doc: doctorRepository.findAll()){

            if((doc.getSpecialty().equals(profession))){

                for(int i = 0; i<24;i++){

                    if(doc.getAvailabilities().charAt(i) == '1' && doc.isAvailable()){
                        counts[i] = counts[i] + 1;
                    }
                }
                count++;
            }
        }

        return counts;

    }




    public ArrayList<Integer> addScheduleDays(Schedule s){

        int day = 0;
        ArrayList<Integer> days = new ArrayList<Integer>();

        for( int i = 0; i<7; i++){

            day = getClerkshipDay(i, s);
            days.add(day);

        }

        return days;

    }


    public int getClerkshipDay(int num, Schedule s){

        int day = 0;

        switch(num){
            case 0: day = Integer.parseInt(s.getSurgery()); break;
            case 1: day = Integer.parseInt(s.getFamilymedicine()); break;
            case 2: day = Integer.parseInt(s.getInternalmedicine()); break;
            case 3: day = Integer.parseInt(s.getNeurology()); break;
            case 4: day = Integer.parseInt(s.getObgyn()); break;
            case 5: day = Integer.parseInt(s.getPediatrics()); break;
            case 6: day = Integer.parseInt(s.getPsychiatry()); break;
            default: day = -1; break;
        }

        return day;

    }

    public String getSpecialty(int num){

        String prof = "";

        switch(num){
            case 0: prof = "Surgery"; break;
            case 1: prof = "Family Medicine"; break;
            case 2: prof = "Internal Medicine"; break;
            case 3: prof = "Neurology"; break;
            case 4: prof = "OBGYN"; break;
            case 5: prof = "Pediatrics"; break;
            case 6: prof = "Psychiatry"; break;
            default: prof = "error"; break;
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



    @GetMapping(path = "/admin")
    public String admin(){

        System.out.println("Admin page.");
        return "admin";

    }


    @RequestMapping(value="/getdocs", method=RequestMethod.POST)

    public @ResponseBody
    int[] getDocs(@RequestBody Profession p) {

            String profession = p.getprofession();
            System.out.println(profession);


            return getAvailabilities(profession);
    }


    @RequestMapping(value="/sendschedule", method=RequestMethod.POST)

    public @ResponseBody
    boolean sendSchedule(@RequestBody Schedule s) {
        Student stu = studentRepository.findById(s.getId()).orElse(null);
        List<Doctor> docs;
        Map<String, Clerkship> stuSched = new HashMap<>();
        for( int i = 0; i<7; i++){

            docs = doctorRepository.findBySpecialtyAndAvailable(getSpecialty(i), true);

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
                    clerk.setDay(getClerkshipDay(i,s));

                    clerkshipRepository.save(clerk);
                    doc.setClerkship(clerk);
                    doc.setAvailable(false);
                    doctorRepository.save(doc);
                    stuSched.put(getSpecialty(i),clerk);
                    break;
                }
            }

        }
        stu.setClerkships(stuSched);
        studentRepository.save(stu);

        if (stuSched.size()!=7) {
            System.out.println("fail at student ");
        }


        s.schedToString();

       // createStudent();

        return true;

    }


}

class Profession{

    private String profession;

    public String getprofession(){
        return profession;
    }

    public void setprofession(String profession){
        this.profession = profession;
    }

}

class Schedule{
    private String surgery;
    private String familymedicine;
    private String internalmedicine;
    private String neurology;
    private String obgyn;
    private String pediatrics;
    private String psychiatry;
    private String id;

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


