package LIC.UC04v1.controllers;

import LIC.UC04v1.model.Clerkship;
import LIC.UC04v1.model.Doctor;
import LIC.UC04v1.model.Student;
import LIC.UC04v1.model.sortDoctorByAvailDates;
import LIC.UC04v1.repositories.ClerkshipRepository;
import LIC.UC04v1.repositories.DoctorRepository;
import LIC.UC04v1.repositories.StudentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.expression.Lists;

import javax.print.Doc;
import java.sql.Time;
import java.util.*;

@RequestMapping("/admin")
@Controller
public class BruteController {
    private DoctorRepository doctorRepository;
    private StudentRepository studentRepository;
    private ClerkshipRepository clerkshipRepository;
    private int studentCount;
    private ArrayList<String> specialties;
    private MiscMethods misc = new MiscMethods();
    public BruteController(DoctorRepository doctorRepository, StudentRepository studentRepository, ClerkshipRepository clerkshipRepository){
        this.doctorRepository = doctorRepository;
        this.studentRepository = studentRepository;
        this.clerkshipRepository = clerkshipRepository;
    }
    @RequestMapping(path = "/brute")
    public String brute(Model model) {
        return "brute1";
    }

    @RequestMapping(path = "/bruteEx")
    public String start(Model model) {
        clearData();
        Random rand = new Random();
        studentCount = (int)studentRepository.count();
        ArrayList<Student> students = new ArrayList<Student>();
        studentRepository.findAll().forEach(students::add);

        //phase1 doctors
        ArrayList<Clerkship> clerksPhase1 = new ArrayList<>();
        for (int i = 0; i<studentCount; i++) {
            Student stu = students.get(i);
            Doctor doc = stu.getPhase1Doc();
            Clerkship clerk = new Clerkship();
            clerk.setDoctor(doc);
            clerk.setStudent(stu);
            clerk.setLocation(doc.getLocation());
            Specialty specialty = doc.getSpecialty();
            clerk.setSpecialty(specialty);
            clerk.setTitle(doc.getSpecialty().toString());
            TimeSlot time = misc.convertAvailabilities(doc.getAvailabilities()).get(0);
            clerk.setTime(time);
            if (specialty==Specialty.FamilyMedicine||specialty==Specialty.Pediatrics||specialty==Specialty.Surgery||specialty==Specialty.InternalMedicine) {
                clerk.setTime2(misc.getOtherTime(time));
            }
            clerk.setDay(time.ordinal());
            clerkshipRepository.save(clerk);
            doc.setHasStu(doc.getHasStu()+1);
            if (doc.getNumStu()==doc.getHasStu()){
                doc.setAvailable(false);
            }
            String availabilities = doc.getAvailabilities();
            availabilities = availabilities.substring(0,time.ordinal())+"0"+availabilities.substring(time.ordinal()+1);
            if (specialty==Specialty.FamilyMedicine||specialty==Specialty.Pediatrics||specialty==Specialty.Surgery||specialty==Specialty.InternalMedicine) {
                availabilities = availabilities.substring(0,misc.getOtherTime(time).ordinal())+"0"+availabilities.substring(misc.getOtherTime(time).ordinal()+1);
            }
            doc.setAvailabilities(availabilities);
            doc.addClerkship(clerk);
            doctorRepository.save(doc);
            clerksPhase1.add(clerk);

        }

        //loop through all students
        for (int i = 0; i <studentCount; i++) {
            Student stu = students.get(i);

            List<Specialty> specialties = new ArrayList (Arrays.asList(Specialty.values()));

            ArrayList<TimeSlot> studentSched = new ArrayList<TimeSlot>();
            Map<String,Clerkship> clerks = new HashMap<String, Clerkship>() ;
            //randomize through all specialties
            for (int z = 0; z< 7; z++) {
                int randomIndex = rand.nextInt(specialties.size());
                Specialty specialty = specialties.get(randomIndex);
                if (specialty==clerksPhase1.get(i).getSpecialty()){
                    clerks.put(clerksPhase1.get(i).getSpecialty().toString(), clerksPhase1.get(i));
                    TimeSlot time = misc.toTimeSlot(clerksPhase1.get(i).getDay());
                    studentSched.add(time);
                    if (clerksPhase1.get(i).getTime2()!=null) {
                        studentSched.add(clerksPhase1.get(i).getTime2());
                    }
                    specialties.remove(randomIndex);
                    continue;
                }

                short need;
                if (specialty==Specialty.FamilyMedicine||specialty==Specialty.Pediatrics||specialty==Specialty.Surgery||specialty==Specialty.InternalMedicine) {
                    need = 2;
                }
                else need =1;

                List<Doctor> docs = doctorRepository.findBySpecialtyAndAvailable(specialty,true);
                Collections.sort(docs, new sortDoctorByAvailDates());

                for (Doctor doc: docs) {

                    ArrayList<TimeSlot> docAvail = misc.convertAvailabilities(doc.getAvailabilities());

                    TimeSlot time;
                    if ((time = compareSchedule(studentSched, docAvail, need))!=null){
                        int day1 = time.ordinal();
                        int day2 = misc.getOtherTime(time).ordinal();
                        String availabilities = doc.getAvailabilities();
                        if (need ==1){
                            studentSched.add(time);

                            doc.setHasStu(doc.getHasStu() + 1);
                            if (doc.getNumStu()==doc.getHasStu()){
                                doc.setAvailable(false);
                            }

                            Clerkship clerk = new Clerkship();

                            clerk.setDoctor(doc);
                            clerk.setStudent(stu);
                            clerk.setTime(time);
                            clerk.setSpecialty(specialty);
                            clerk.setDay(time.ordinal());
                            clerk.setTitle(specialty.toString());
                            clerk.setLocation(doc.getLocation());

                            clerks.put(clerk.getSpecialty().toString(),clerk);

                            clerkshipRepository.save(clerk);
                            doc.addClerkship(clerk);
                            availabilities = availabilities.substring(0,day1)+"0"+availabilities.substring(day1+1);
                            doc.setAvailabilities(availabilities);
                            doctorRepository.save(doc);
                            break;
                        }
                        else {
                            studentSched.add(time);
                            studentSched.add(misc.getOtherTime(time));

                            doc.setHasStu(doc.getHasStu() + 1);
                            if (doc.getNumStu()==doc.getHasStu()){
                                doc.setAvailable(false);
                            }

                            Clerkship clerk = new Clerkship();
                            clerk.setDoctor(doc);
                            clerk.setStudent(students.get(i));
                            clerk.setTime(time);
                            clerk.setTime2(misc.getOtherTime(time));
                            clerk.setSpecialty(specialty);
                            clerk.setDay(time.ordinal());
                            clerk.setTitle(specialty.toString());
                            clerk.setLocation(doc.getLocation());

                            clerks.put(clerk.getSpecialty().toString(),clerk);
                            clerkshipRepository.save(clerk);
                            doc.addClerkship(clerk);
                            availabilities = availabilities.substring(0,day1)+"0"+availabilities.substring(day1+1);
                            availabilities = availabilities.substring(0,day2)+"0"+availabilities.substring(day2+1);
                            doc.setAvailabilities(availabilities);
                            doctorRepository.save(doc);

                            break;
                        }
                    }

                }

                specialties.remove(randomIndex);
            }
            if (clerks.size()!=7) {
                System.out.println("fail at student "+i);
                System.out.println(clerks.size());
                return "brute1";
            }
            stu.setClerkships(clerks);
            stu.setHasSchedule(true);
            studentRepository.save(stu);
        }

        return "brute1";
    }

    private TimeSlot compareSchedule(ArrayList<TimeSlot> studentSched, ArrayList<TimeSlot> doctorSched, short need) {

        if (need ==1) {
            for (TimeSlot docTime: doctorSched) {
                if (!studentSched.contains(docTime)) {
                    return docTime;
                }
            }
        }
        else {
            for (TimeSlot docTime: doctorSched) {
                TimeSlot otherTime = misc.getOtherTime(docTime);
                if (!studentSched.contains(docTime) && !studentSched.contains(otherTime)) {
                    return docTime;
                }
            }
        }
        return null;
    }
    private void clearData() {
        for (Doctor doc : doctorRepository.findAll()) {
            doc.setAvailable(true);
            doctorRepository.save(doc);
        }
        for (Clerkship clerk: clerkshipRepository.findAll()) {
            Doctor doc = clerk.getDoctor();
            Student stu = clerk.getStudent();
            doc.setClerkship(null);
            stu.setClerkships(null);
            studentRepository.save(stu);
            doctorRepository.save(doc);
            clerkshipRepository.delete(clerk);
        }
    }

}