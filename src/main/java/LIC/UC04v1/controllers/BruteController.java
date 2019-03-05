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
        return "brute";
    }

    @RequestMapping(path = "/bruteEx")
    public String start(Model model) {
        clearData();
        Random rand = new Random();
        studentCount = (int)studentRepository.count();
        ArrayList<Student> students = new ArrayList<Student>();
        studentRepository.findAll().forEach(students::add);

        //loop through all students
        for (int i = 0; i <studentCount; i++) {
            List<Specialty> specialties = new ArrayList (Arrays.asList(Specialty.values()));
            ArrayList<TimeSlot> studentSched = new ArrayList<TimeSlot>();
            Map<String,Clerkship> clerks = new HashMap<String, Clerkship>() ;
            //randomize through all specialties
            for (int z = 0; z< 7; z++) {
                int randomIndex = rand.nextInt(specialties.size());
                Specialty specialty = specialties.get(randomIndex);

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
                        if (need ==1){
                            studentSched.add(time);

                            doc.setHasStu(doc.getHasStu() + 1);
                            if (doc.getNumStu()==doc.getHasStu()){
                                doc.setAvailable(false);
                            }

                            Clerkship clerk = new Clerkship();

                            clerk.setDoctor(doc);
                            clerk.setStudent(students.get(i));
                            clerk.setTime(time);
                            clerk.setSpecialty(specialty);
                            clerk.setDay(time.ordinal());
                            clerk.setTitle(specialty.toString());
                            clerk.setLocation(doc.getLocation());

                            clerks.put(clerk.getSpecialty().toString(),clerk);

                            clerkshipRepository.save(clerk);
                            doc.addClerkship(clerk);
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
                            doctorRepository.save(doc);

                            break;
                        }
                    }

                }

                specialties.remove(randomIndex);
            }
            if (clerks.size()!=7) {
                System.out.println("fail at student "+i);
                return "brute";
            }
            students.get(i).setClerkships(clerks);
            students.get(i).setHasSchedule(true);
            studentRepository.save(students.get(i));
        }

        return "brute";
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
