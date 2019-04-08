package LIC.UC04v1.controllers;

import LIC.UC04v1.model.*;
import LIC.UC04v1.repositories.DoctorRepository;
import LIC.UC04v1.repositories.ClerkshipRepository;
import LIC.UC04v1.repositories.StudentRepository;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequestMapping("/admin")
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

        return "viewSchedules1";
    }

    @RequestMapping(path = "/grabDocs", method = RequestMethod.POST)
    public @ResponseBody
    List<DocSchedules> getDoctors(@RequestBody LoadView lv) {

        List<DocSchedules> docScheds = new ArrayList<>();
        List<Doctor> docSort = new ArrayList<>();
        for (Doctor doc: doctorRepository.findAll()){
            if(doc.getHasStu() == 1){
                docSort.add(doc);
            }

        }
        Collections.sort(docSort,new DoctorNameComparator());

        for(Doctor doc: docSort){
            List<Clerkship> clerkships = new ArrayList<>();
            List<String> clerkList = new ArrayList<>();
            List<String> dayList = new ArrayList<>();
            List<String> timeList = new ArrayList<>();
            List<String> week1List = new ArrayList<>();
            List<String> week2List = new ArrayList<>();
            List<String> stuNameList = new ArrayList<>();

            clerkships = doc.getClerkship();

            DocSchedules sched = new DocSchedules();
            sched.setName(doc.getName());
            sched.setId(doc.getId());
            sched.setSpeciality(doc.getSpecialty().name());
            sched.setEmail(doc.getEmail());

            for(Clerkship clerk: clerkships){
                String week1 = " ";
                String week2 = " ";
                stuNameList.add(clerk.getStudentName());
                dayList.add(clerk.getTime());
                timeList.add(clerk.getStartTime());
                if(clerk.getDay()<12){
                    week1 = "X";
                }else{ week2 = "X";}


                if(clerk.getTitle().equals("Surgery") || clerk.getTitle().equals("Pediatrics") || clerk.getTitle().equals("Family Medicine") || clerk.getTitle().equals("Internal Medicine")){
                    week2 = "X";
                }

                week1List.add(week1);
                week2List.add(week2);
            }

            sched.setStuNameList(stuNameList);
            sched.setDayList(dayList);
            sched.setTimeList(timeList);
            sched.setWeek1List(week1List);
            sched.setWeek2List(week2List);

            docScheds.add(sched);

        }

        return docScheds;
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
            List<String> week1List = new ArrayList<>();
            List<String> week2List = new ArrayList<>();


           x = stu.getClerkships();

           StudentSchedules sched =  new StudentSchedules();
           sched.setName(stu.getName());
           sched.setId(stu.getId());
           sched.setEmail(stu.getEmail());



            for(String key: x.keySet()){
                String week1 = " ";
                String week2 = " ";
               Clerkship clerk = x.get(key);
               clerkList.add(clerk.getTitle());
               docList.add(clerk.getDoctorName());
               dayList.add(clerk.getTime());
               timeList.add(clerk.getStartTime());
                if(clerk.getDay()<12){
                    week1 = "X";
                }else{ week2 = "X";}


                if(clerk.getTitle().equals("Surgery") || clerk.getTitle().equals("Pediatrics") || clerk.getTitle().equals("Family Medicine") || clerk.getTitle().equals("Internal Medicine")){
                    week2 = "X";
                    //clerkList.add(clerk.getTitle());
                    //docList.add(clerk.getDoctorName());
                    //dayList.add(clerk.getTime());
                    //timeList.add(clerk.getStartTime());
                }
                week1List.add(week1);
                week2List.add(week2);
           }

            sched.setDocList(docList);
            sched.setDayList(dayList);
            sched.setProfList(clerkList);
            sched.setTimeList(timeList);
            sched.setWeek1List(week1List);
            sched.setWeek2List(week2List);

            stuScheds.add(sched);
       }

        return stuScheds;
    }
}

class DocSchedules {

    public List<String> getStuNameList() {
        return stuNameList;
    }

    public void setStuNameList(List<String> stuNameList) {
        this.stuNameList = stuNameList;
    }

    List<String> stuNameList = new ArrayList<>();

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

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public List<String> getDayList() {
        return dayList;
    }

    public void setDayList(List<String> dayList) {
        this.dayList = dayList;
    }

    public List<String> getTimeList() {
        return timeList;
    }

    public void setTimeList(List<String> timeList) {
        this.timeList = timeList;
    }


    private String name;
    private String id;
    private String email;
    private String speciality;
    private List<String> dayList = new ArrayList<>();
    private List<String> timeList = new ArrayList<>();

    public List<String> getWeek1List() {
        return week1List;
    }

    public void setWeek1List(List<String> week1List) {
        this.week1List = week1List;
    }

    public List<String> getWeek2List() {
        return week2List;
    }

    public void setWeek2List(List<String> week2List) {
        this.week2List = week2List;
    }

    List<String> week1List = new ArrayList<>();
    List<String> week2List = new ArrayList<>();

}


class StudentSchedules{

    private String name;
    private String id;
    private String email;
    private List<String> docList = new ArrayList<>();
    private List<String> profList = new ArrayList<>();
    private List<String> dayList = new ArrayList<>();
    private List<String> timeList = new ArrayList<>();
    private List<String> week1List = new ArrayList<>();

    public List<String> getWeek2List() {
        return week2List;
    }

    public void setWeek2List(List<String> week2List) {
        this.week2List = week2List;
    }

    private List<String> week2List = new ArrayList<>();

    public List<String> getTimeList() {
        return timeList;
    }

    public void setTimeList(List<String> timeList) {
        this.timeList = timeList;
    }

    public List<String> getWeek1List() {
        return week1List;
    }

    public void setWeek1List(List<String> week1List) {
        this.week1List = week1List;
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
