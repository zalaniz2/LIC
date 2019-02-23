package LIC.UC04v1.controllers;

import LIC.UC04v1.model.Doctor;
import LIC.UC04v1.model.Student;
import LIC.UC04v1.repositories.DoctorRepository;
import LIC.UC04v1.repositories.StudentRepository;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/*
    SOME NOTES ON THIS CLASS
        This class pulls in data from an excel file. It saves the first column as a doctor/student name
        and the second column as the doctor/student email. There are currently no checks in place to
        make sure the excel file is formatted correctly. Additionally, blank rows may be added to the
        database. We'll have to fix this and do extensive testing, but it works at a low fidelity!
    If you have questions about anything, let me (Katie) know. I'll come back and clean up/comment better soon.
 */

@Controller
public class EmailsController {

    private DoctorRepository doctorRepository;
    private StudentRepository studentRepository;

    public EmailsController(DoctorRepository doctorRepository, StudentRepository studentRepository){
        this.doctorRepository = doctorRepository;
        this.studentRepository = studentRepository;
    }

    @GetMapping(path = "/send-emails")
    public String getImports(Model model){
        return "SendEmails";
    }

    @RequestMapping(path = "/send-emails/{type}")
    public String docEmails(Model model, @PathVariable String type){
        return "";
    }
}
