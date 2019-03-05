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
public class test{

    private DoctorRepository doctorRepository;
    private ClerkshipRepository clerkshipRepository;
    private StudentRepository studentRepository;

    public test(DoctorRepository doctorRepository, ClerkshipRepository clerkshipRepository, StudentRepository studentRepository) {
        this.doctorRepository = doctorRepository;
        this.clerkshipRepository = clerkshipRepository;
        this.studentRepository = studentRepository;
    }



    @RequestMapping(path = "/adminView")
    public String admin() {

        return "test";
    }

    @RequestMapping(path = "/grabStudents")
    public @ResponseBody
    List<Clerkship> getStudents(@RequestBody LoadView lv) {

       Map<String, Clerkship> x = new HashMap<>();
       List<Clerkship> clerkList = new ArrayList<>();


        for( Student stu : studentRepository.findAll() ){
           x = stu.getClerkships();

           for(String key: x.keySet()){
               Clerkship clerk = x.get(key);
               clerkList.add(clerk);
           }
           break;
       }


        return clerkList;

    }



}

class StudentSchedules{

    private String name;
    private String id;
    private String email;
    private List<Doctor> docList = new ArrayList<>();
    private List<String> profList = new ArrayList<>();
    private List<String> dayList = new ArrayList<>();




}

class LoadView{

    private String grab;

    public String getGrab() {
        return grab;
    }

    public void setGrab(String grab) {
        this.grab = grab;
    }

}
