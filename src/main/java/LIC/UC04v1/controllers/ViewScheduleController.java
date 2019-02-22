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

    @RequestMapping(value = "/schedule", method = RequestMethod.GET)
    public String home() { return "viewSchedules"; }

    @RequestMapping(value="/getStudents", method = RequestMethod.POST)
    public @ResponseBody
    String getStudents(){
        System.out.println("Made it");
        return "Barbra S";

    }
}
