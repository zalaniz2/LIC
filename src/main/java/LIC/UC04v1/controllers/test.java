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

        return "zachView";
    }

    @RequestMapping(path = "/grabStudents", method = RequestMethod.POST)
    public @ResponseBody
    List<StudentSchedules> getStudents(@RequestBody LoadView lv) {

       Map<String, Clerkship> x = new HashMap<>();
       List<String> clerkList = new ArrayList<>();
        List<String> docList = new ArrayList<>();
        List<String> dayList = new ArrayList<>();

       List<StudentSchedules> stuScheds = new ArrayList<>();

       System.out.println(lv.getGrab());

        for( Student stu : studentRepository.findAll() ){
           x = stu.getClerkships();

           StudentSchedules sched =  new StudentSchedules();
           sched.setName(stu.getName());
           sched.setId(stu.getId());
           sched.setEmail(stu.getEmail());

           stuScheds.add(sched);

            for(String key: x.keySet()){
               Clerkship clerk = x.get(key);
               clerkList.add(clerk.getTitle());
               docList.add(clerk.getDoctorName());
               dayList.add(clerk.getTime());
           }

            sched.setDocList(docList);
            sched.setDayList(dayList);
            sched.setProfList(clerkList);
       }

        return stuScheds;
    }
}

class StudentSchedules{

    private String name;
    private String id;
    private String email;
    private List<String> docList = new ArrayList<>();
    private List<String> profList = new ArrayList<>();
    private List<String> dayList = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getDocList() {
        return docList;
    }

    public void setDocList(List<String> docList) {
        this.docList = docList;
    }

    public List<String> getProfList() {
        return profList;
    }

    public void setProfList(List<String> profList) {
        this.profList = profList;
    }

    public List<String> getDayList() {
        return dayList;
    }

    public void setDayList(List<String> dayList) {
        this.dayList = dayList;
    }



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
