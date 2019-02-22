package LIC.UC04v1.controllers;

import LIC.UC04v1.model.Student;
import LIC.UC04v1.repositories.ClerkshipRepository;
import LIC.UC04v1.repositories.DoctorRepository;
import LIC.UC04v1.repositories.StudentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ViewScheduleController {

    private DoctorRepository doctorRepository;
    private StudentRepository studentRepository;
    private ClerkshipRepository clerkshipRepository;

    public ViewScheduleController(DoctorRepository doctorRepository, StudentRepository studentRepository, ClerkshipRepository clerkshipRepository) {
        this.doctorRepository = doctorRepository;
        this.studentRepository = studentRepository;
        this.clerkshipRepository = clerkshipRepository;

    }

    public List<Student> getAllStudents(){

        List<Student> all = new ArrayList<>();

        for(Student stu: studentRepository.findAll()){
                all.add(stu);

        }
        return all;
    }

    @RequestMapping(value = "/schedule", method = RequestMethod.GET)
    public String home() { return "viewSchedules"; }

    @RequestMapping(value="/getStudents", method = RequestMethod.POST)
    public @ResponseBody
    List<Student> getStudents(@RequestBody AdminView av ){

        if( av.getArgument().equals("student")){
            return getAllStudents();
        }
        return getAllStudents();


    }
}

class AdminView{

    private String argument;

    public String getArgument() {
        return argument;
    }

    public void setArgument(String argument) {
        this.argument = argument;
    }
}
