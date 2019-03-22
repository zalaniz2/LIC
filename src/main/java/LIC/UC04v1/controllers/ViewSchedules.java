package LIC.UC04v1.controllers;

import LIC.UC04v1.model.*;
import LIC.UC04v1.repositories.DoctorRepository;
import LIC.UC04v1.repositories.ClerkshipRepository;
import LIC.UC04v1.repositories.StudentRepository;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class ViewSchedules{

    private DoctorRepository doctorRepository;
    private ClerkshipRepository clerkshipRepository;
    private StudentRepository studentRepository;

    public ViewSchedules(DoctorRepository doctorRepository, ClerkshipRepository clerkshipRepository, StudentRepository studentRepository) {
        this.doctorRepository = doctorRepository;
        this.clerkshipRepository = clerkshipRepository;
        this.studentRepository = studentRepository;
    }



    @RequestMapping(path = "/adminView")
    public String admin() {

        return "viewSchedules";
    }

    @RequestMapping(path = "/grabStudents", method = RequestMethod.POST)
    public @ResponseBody
    List<StudentSchedules> getStudents(@RequestBody LoadView lv) {


        List<StudentSchedules> stuScheds = new ArrayList<>();
       System.out.println(lv.getGrab());

       List<Student> stuSort = new ArrayList<>();
       for (Student stu: studentRepository.findAll()){
           stuSort.add(stu);
       }
       Collections.sort(stuSort,new StudentNameComparator());

        for( Student stu : stuSort ){
            Map<String, Clerkship> x = new HashMap<>();
            List<String> clerkList = new ArrayList<>();
            List<String> docList = new ArrayList<>();
            List<String> dayList = new ArrayList<>();
            List<String> timeList = new ArrayList<>();
            List<String> weekList = new ArrayList<>();



           x = stu.getClerkships();

           StudentSchedules sched =  new StudentSchedules();
           sched.setName(stu.getName());
           sched.setId(stu.getId());
           sched.setEmail(stu.getEmail());



            for(String key: x.keySet()){
                String week = "";
                String week2 = "Week 2";
               Clerkship clerk = x.get(key);
               clerkList.add(clerk.getTitle());
               docList.add(clerk.getDoctorName());
               dayList.add(clerk.getTime());
               timeList.add(clerk.getStartTime());
                if(clerk.getDay()<12){
                    week = "Week 1";
                }else{ week = "Week 2";}
               weekList.add(week);

                if(clerk.getTitle().equals("Surgery") || clerk.getTitle().equals("Pediatrics") || clerk.getTitle().equals("Family Medicine") || clerk.getTitle().equals("Internal Medicine")){
                    clerkList.add(clerk.getTitle());
                    docList.add(clerk.getDoctorName());
                    dayList.add(clerk.getTime());
                    timeList.add(clerk.getStartTime());
                    weekList.add(week2);
                }
           }

            sched.setDocList(docList);
            sched.setDayList(dayList);
            sched.setProfList(clerkList);
            sched.setTimeList(timeList);
            sched.setWeekList(weekList);

            stuScheds.add(sched);
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
    private List<String> timeList = new ArrayList<>();
    private List<String> weekList = new ArrayList<>();

    public List<String> getTimeList() {
        return timeList;
    }

    public void setTimeList(List<String> timeList) {
        this.timeList = timeList;
    }

    public List<String> getWeekList() {
        return weekList;
    }

    public void setWeekList(List<String> weekList) {
        this.weekList = weekList;
    }



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
