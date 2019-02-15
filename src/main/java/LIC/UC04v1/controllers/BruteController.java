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

                List<Doctor> docs = doctorRepository.findBySpecialtyInTextAndAvailable(specialty,true);
                for (Doctor doc: docs) {
                    doc.setNumberOfDaysAvail();
                }
                Collections.sort(docs, new sortDoctorByAvailDates());

                for (Doctor doc: docs) {
                    System.out.println(doc.getNumberOfDaysAvail());
                }
                for (Doctor doc: docs) {

                    ArrayList<TimeSlot> docAvail = convertAvailabilities(doc.getAvailabilities());

                    //test
                    for (TimeSlot time : docAvail){
                        System.out.println(time.name());
                    }
                    System.out.println("---------------");


                    TimeSlot time;
                    if ((time = compareSchedule(studentSched, docAvail, need))!=null){
                        if (need ==1){
                            studentSched.add(time);
                            doc.setAvailable(false);

                            Clerkship clerk = new Clerkship();

                            clerk.setDoctor(doc);
                            clerk.setStudent(students.get(i));
                            clerk.setTime(time);
                            clerk.setSpecialty(specialty);

                            clerks.put(clerk.getSpecialty().name(),clerk);

                            clerkshipRepository.save(clerk);
                            doc.setClerkship(clerk);
                            doctorRepository.save(doc);
                            break;
                        }
                        else {
                            studentSched.add(time);
                            studentSched.add(getOtherTime(time));

                            doc.setAvailable(false);

                            Clerkship clerk = new Clerkship();
                            clerk.setDoctor(doc);
                            clerk.setStudent(students.get(i));
                            clerk.setTime(time);
                            clerk.setTime2(getOtherTime(time));
                            clerk.setSpecialty(specialty);

                            clerks.put(clerk.getSpecialty().name(),clerk);
                            clerkshipRepository.save(clerk);
                            doc.setClerkship(clerk);
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
                TimeSlot otherTime = getOtherTime(docTime);
                if (!studentSched.contains(docTime) && !studentSched.contains(otherTime)) {
                    return docTime;
                }
            }
        }
        return null;
    }

    private TimeSlot getOtherTime(TimeSlot time) {
        switch (time) {
            case FriA1: return TimeSlot.FriA2;
            case FriA2: return TimeSlot.FriA1;
            case ThuA1: return TimeSlot.ThuA2;
            case ThuA2: return TimeSlot.ThuA1;
            case FriM1: return TimeSlot.FriM2;
            case FriM2: return TimeSlot.FriM1;
            case MonA1: return TimeSlot.MonA2;
            case MonA2: return TimeSlot.MonA1;
            case MonM1: return TimeSlot.MonM2;
            case MonM2: return TimeSlot.MonM1;
            case ThuM1: return TimeSlot.ThuM2;
            case ThuM2: return TimeSlot.ThuM1;
            case TueA1: return TimeSlot.TueA2;
            case TueA2: return TimeSlot.TueA1;
            case TueM1: return TimeSlot.TueM2;
            case TueM2: return TimeSlot.TueM1;
            case WedA1: return TimeSlot.WedA2;
            case WedA2: return TimeSlot.WedA1;
            case WedM1: return TimeSlot.WedM2;
            case WedM2: return TimeSlot.WedM1;
            case SatA1: return TimeSlot.SatA2;
            case SatA2: return TimeSlot.SatA1;
            case SatM1: return TimeSlot.SatM2;
            case SatM2: return TimeSlot.SatM1;
        }
        return null;
    }
    private ArrayList<TimeSlot> convertAvailabilities(String avail){
        ArrayList<TimeSlot> returnVal = new ArrayList<TimeSlot>();
        for (int i =0; i <avail.length();i++) {
            if (avail.charAt(i)=='1') {
                switch (i) {
                    case 0: returnVal.add(TimeSlot.MonM1); break;
                    case 1: returnVal.add(TimeSlot.MonA1); break;
                    case 2: returnVal.add(TimeSlot.TueM1); break;
                    case 3: returnVal.add(TimeSlot.TueA1); break;
                    case 4: returnVal.add(TimeSlot.WedM1); break;
                    case 5: returnVal.add(TimeSlot.WedA1); break;
                    case 6: returnVal.add(TimeSlot.ThuM1); break;
                    case 7: returnVal.add(TimeSlot.ThuA1); break;
                    case 8: returnVal.add(TimeSlot.FriM1); break;
                    case 9: returnVal.add(TimeSlot.FriA1); break;
                    case 10: returnVal.add(TimeSlot.SatM1); break;
                    case 11: returnVal.add(TimeSlot.SatA1); break;
                    case 12: returnVal.add(TimeSlot.MonM2); break;
                    case 13: returnVal.add(TimeSlot.MonA2); break;
                    case 14: returnVal.add(TimeSlot.TueM2); break;
                    case 15: returnVal.add(TimeSlot.TueA2); break;
                    case 16: returnVal.add(TimeSlot.WedM2); break;
                    case 17: returnVal.add(TimeSlot.WedA2); break;
                    case 18: returnVal.add(TimeSlot.ThuM2); break;
                    case 19: returnVal.add(TimeSlot.ThuA2); break;
                    case 20: returnVal.add(TimeSlot.FriM2); break;
                    case 21: returnVal.add(TimeSlot.FriA2); break;
                    case 22: returnVal.add(TimeSlot.SatM2); break;
                    case 23: returnVal.add(TimeSlot.SatA2); break;

                }
            }
        }
        return returnVal;
    }
}
