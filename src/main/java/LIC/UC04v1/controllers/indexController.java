package LIC.UC04v1.controllers;

import LIC.UC04v1.model.Doctor;
import LIC.UC04v1.model.Clerkship;
import LIC.UC04v1.model.Student;
import LIC.UC04v1.repositories.DoctorRepository;
import LIC.UC04v1.repositories.ClerkshipRepository;
import LIC.UC04v1.repositories.StudentRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.List;

import java.util.Random;


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
        int counts[] = new int[20];

        for(Doctor doc: doctorRepository.findAll()){

            if((doc.getSpecialty().equals(profession))){

                for(int i = 0; i<20;i++){

                    if(doc.getAvailabilities().charAt(i) == '1' && doc.isAvailable()){
                        counts[i] = counts[i] + 1;
                    }
                }
                count++;
            }
        }

        return counts;

    }


    public ArrayList<Doctor> getDocWithProfession(String profession){

        ArrayList<Doctor> d = new ArrayList<Doctor>();

        for( Doctor docs: doctorRepository.findAll()){
            //loop through all doctors
            if(docs.getSpecialty().equals(profession)){
                d.add(docs);
            }
        }

        return d;

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

        /*
    public void createStudent(){

        Student stu = new Student();

        String name = "student" + id;
        stu.setName(name);

        student = stu;

        studentRepository.save(student);

    }
    */

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

    public String getClerkshipName(int num){

        String prof = "";

        switch(num){
            case 0: prof = "ER"; break;
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

    /*
    public boolean createClerkships(Schedule s){

        ArrayList<Integer> days = addScheduleDays(s);
        ArrayList<Doctor> doctors;
        ArrayList<Clerkship> stuClerks = new ArrayList<>();
        System.out.println(days.size());


        for( int i = 0; i<7; i++){

            String profession = getClerkshipName(i);
            System.out.println(profession);
            doctors = getDocWithProfession(profession);
            System.out.println(doctors.size());
            System.out.println("i = " + i);
            int day = days.get(i);

            for( int j = 0; j<doctors.size(); j++){
                Doctor d = doctors.get(j);
                if( d.getAvailability().charAt(day) == '1' && d.isAvailable()){

                    System.out.println("found a doc get out");
                    Clerkship c = new Clerkship();
                    c.setDoctor(d);
                    c.setStudent(student);
                    c.setTime(day);
                    clerkshipRepository.save(c);
                    stuClerks.add(c);
                    d.setClerkship(c);
                    d.setAvailable(false);
                    doctorRepository.save(d);
                    break;
                }
            }
            System.out.println("back to outer loop on i = " + i);

        }
        student.setClerkships(stuClerks);
        studentRepository.save(student);


        if( stuClerks.size() == 7){
            System.out.println("got all clerks");
        }
        else{
            System.out.println("didn't work");

        }
        id++;
        return true;

    }
    */


    @GetMapping(path = "/")
        public String index(){

        System.out.println("Main page.");
        return "index";

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

        /*
    @RequestMapping(value="/grabstudents", method=RequestMethod.POST)

    public @ResponseBody
    ArrayList<String> grabStudents() {

        System.out.println("Ajax call to get students for admin.");

        ArrayList<String> adminView = new ArrayList<String>();
        for( Student stu: studentRepository.findAll() ){

            adminView.add(stu.getId() + "");
            adminView.add(stu.getName());

            for( Clerkship clerk : clerkshipRepository.findAll()){
                adminView.add(clerk.getDoctor().getProfession());
                adminView.add(clerk.getDoctor().getName());
                adminView.add(clerk.getTime() + "");
            }

        }

       return adminView;
    }
    */


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

    }

}


