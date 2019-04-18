package LIC.UC04v1.controllers;

import LIC.UC04v1.model.Doctor;
import LIC.UC04v1.model.Clerkship;
import LIC.UC04v1.model.Student;
import LIC.UC04v1.model.sortDoctorByAvailDates;
import LIC.UC04v1.repositories.DoctorRepository;
import LIC.UC04v1.repositories.ClerkshipRepository;
import LIC.UC04v1.repositories.StudentRepository;
import LIC.UC04v1.services.StudentService;

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
    private final StudentService studentService;


    public indexController(StudentService studentService, DoctorRepository doctorRepository, ClerkshipRepository clerkshipRepository, StudentRepository studentRepository) {
        this.doctorRepository = doctorRepository;
        this.clerkshipRepository = clerkshipRepository;
        this.studentRepository = studentRepository;
        this.studentService = studentService;
    }

    public int[] getAvailabilities(String profession, String location) {

        int count = 0;
        int counts[] = new int[24];

        for (Doctor doc : doctorRepository.findAll()) {

            if ((doc.getSpecialty()==misc.convertSpecialty(profession)) && doc.getLocation()==misc.convertLocation(location) && doc.getHasPhase1()) {

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

    public int[] getSingleAvailabilities(Doctor d) {

        int count = 0;
        int counts[] = new int[24];

        for (int i = 0; i < 24; i++) {

            if (d.getAvailabilities().charAt(i) == '1' && d.isAvailable()) {
                        counts[i] = counts[i] + 1;
            }
            count++;

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




    @RequestMapping(path = "/student/{stuID}")
    public String neuro(Model model, @PathVariable String stuID){
        Student stu = studentRepository.findById(stuID).orElse(null);
        if(stu == null){
            stu = studentService.findById(stuID);
        }
        if (stu!=null) {
            model.addAttribute("stu", stu);
        }
        return "index";
    }


    @GetMapping(path = "/admin")
    public String admin() {

        System.out.println("Admin page.");
        return "admin";

    }

    @GetMapping(path = "/success")
    public String success() {

        return "success";

    }

    @GetMapping(path = "/finished")
    public String finished() {

        return "finished";

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

    @RequestMapping(value = "/poavail", method = RequestMethod.POST)

    public @ResponseBody
    int[] getSingleDoc(@RequestBody PhaseOne p) {

        Student s = studentRepository.findById(p.getId()).orElse(null);
        Doctor sd = s.getPhase1Doc();
        System.out.println("Sending availabilities for doctor " + sd.getName());

        return getSingleAvailabilities(sd);


    }

    @RequestMapping(value = "/checkstu", method = RequestMethod.POST)

    public @ResponseBody
    boolean checkStudent(@RequestBody PhaseOne p) {

        Student s = studentRepository.findById(p.getId()).orElse(null);


        return s.isHasSchedule();

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

        List<Clerkship> clerks = new ArrayList<Clerkship>();
        List<Doctor> dlst = new ArrayList<Doctor>();



        Student stu = studentRepository.findById(s.getId()).orElse(null);
        Doctor pd = stu.getPhase1Doc();

        List<Doctor> docs;
        Map<String, Clerkship> stuSched = new HashMap<>();


        for( int i = 0; i<7; i++){


            String spe = getSpecialty(i);
            Specialty specialty = misc.toSpecialty(spe);

            if (specialty==pd.getSpecialty()){

                Clerkship clerk = new Clerkship();
                String availabilities = pd.getAvailabilities();
                int day = getClerkshipDay(i,s);
                clerk.setStudent(stu);
                clerk.setDoctor(pd);
                clerk.setTitle(spe);
                clerk.setTime(misc.toTimeSlot(day));
                clerk.setSpecialty(specialty);
                clerk.setLocation(pd.getLocation());

                clerk.setTime2(misc.getOtherTime(misc.toTimeSlot(getClerkshipDay(i,s))));
                clerk.setDay(day-12);
                availabilities = availabilities.substring(0,day-12)+"0"+availabilities.substring(day-11);
                availabilities = availabilities.substring(0,day)+"0"+availabilities.substring(day+1);

                clerks.add(clerk);
                pd.addClerkship(clerk);
                pd.setAvailabilities(availabilities);
                pd.setAvailabilities(pd.getAvailabilities());

                pd.setHasStu(pd.getHasStu() + 1);
                if (pd.getNumStu()==pd.getHasStu()){
                    pd.setAvailable(false);
                }

                pd.setHasPhase1(true);
                dlst.add(pd);
                stuSched.put(spe,clerk);

                continue;
            }

            Location loc = misc.convertLocation(getClerkshipLocation(i, s));
            docs = doctorRepository.findBySpecialtyAndAvailableAndLocation(specialty, true, loc);

            //sorting stuffs

            Collections.sort(docs, new sortDoctorByAvailDates());
            //end sorting stuffs


            for (Doctor doc: docs) {

                if (doc.getAvailabilities().charAt(getClerkshipDay(i,s ))=='1'){

                    Clerkship clerk = new Clerkship();
                    String availabilities = doc.getAvailabilities();
                    int day = getClerkshipDay(i,s);
                    clerk.setStudent(stu);
                    clerk.setDoctor(doc);
                    clerk.setTitle(spe);
                    clerk.setTime(misc.toTimeSlot(day));
                    clerk.setSpecialty(specialty);
                    clerk.setLocation(doc.getLocation());

                    if (specialty==Specialty.FamilyMedicine||specialty==Specialty.Pediatrics||specialty==Specialty.Surgery||specialty==Specialty.InternalMedicine) {

                        //flag check
                        if (doc.getHasPhase1()) {
                            clerk.setTime2(misc.getOtherTime(misc.toTimeSlot(getClerkshipDay(i, s))));
                            clerk.setDay(day - 12);
                            availabilities = availabilities.substring(0, day - 12) + "0" + availabilities.substring(day - 11);
                            availabilities = availabilities.substring(0, day) + "0" + availabilities.substring(day+1);
                        }
                        else{
                            continue;
                        }



                    }
                    else {
                        clerk.setDay(day);
                        availabilities = availabilities.substring(0,day)+"0"+availabilities.substring(day+1);

                    }

                    clerks.add(clerk);
                    doc.addClerkship(clerk);
                    doc.setAvailabilities(availabilities);
                    doc.setAvailabilities(doc.getAvailabilities());

                    doc.setHasStu(doc.getHasStu() + 1);
                    if (doc.getNumStu()==doc.getHasStu()){
                        doc.setAvailable(false);
                    }

                    dlst.add(doc);
                    stuSched.put(spe,clerk);
                    break;
                }
            }

        }


        System.out.println(stuSched.size());

        if (clerks.size()!=7) {
            return false;
        }

        for( int i = 0; i<clerks.size(); i++){


            clerkshipRepository.save(clerks.get(i));
            doctorRepository.save(dlst.get(i));

        }

        stu.setClerkships(stuSched);
        stu.setHasSchedule(true);
        studentRepository.save(stu);

        return true;

    }

    @RequestMapping(value = "/checkdoc", method = RequestMethod.POST)

    public @ResponseBody
    Specialty checkDoc(@RequestBody PhaseOne p) {


        Student sd = studentRepository.findById(p.getId()).orElse(null);

        Doctor td = sd.getPhase1Doc();

        return td.getSpecialty();


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

class PhaseOne{

    private String id;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}



