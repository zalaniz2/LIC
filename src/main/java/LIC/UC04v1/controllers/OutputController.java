package LIC.UC04v1.controllers;

import LIC.UC04v1.repositories.ClerkshipRepository;
import LIC.UC04v1.repositories.DoctorRepository;
import LIC.UC04v1.repositories.StudentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

@Controller
public class OutputController {
    private DoctorRepository doctorRepository;
    private StudentRepository studentRepository;
    private ClerkshipRepository clerkshipRepository;
    private ArrayList<String> specialties;
    public OutputController(DoctorRepository doctorRepository, StudentRepository studentRepository, ClerkshipRepository clerkshipRepository){
        this.doctorRepository = doctorRepository;
        this.studentRepository = studentRepository;
        this.clerkshipRepository = clerkshipRepository;
    }
    @RequestMapping(path = "/output", method = RequestMethod.GET)
    public void brute(HttpServletResponse response) {
        String fileName = "schedule.csv";
        try {

            response.flushBuffer();
        } catch (IOException ex) {
            System.out.println("Error writing file to output stream. Filename was "+ fileName);
            throw new RuntimeException("IOError writing file to output stream");
        }

    }

}
