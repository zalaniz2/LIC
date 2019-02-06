package LIC.UC04v1.controllers;


import LIC.UC04v1.model.Clerkship;
import LIC.UC04v1.model.Doctor;
import LIC.UC04v1.model.Student;
import LIC.UC04v1.repositories.ClerkshipRepository;
import LIC.UC04v1.repositories.DoctorRepository;
import LIC.UC04v1.repositories.StudentRepository;
import org.springframework.stereotype.Controller;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.util.*;

@Controller
public class ExportController{

    private DoctorRepository doctorRepository;
    private StudentRepository studentRepository;
    private ClerkshipRepository clerkshipRepository;
    private List<Object> studentList;

    public ExportController(DoctorRepository doctorRepository, StudentRepository studentRepository, ClerkshipRepository clerkshipRepository){
        this.doctorRepository = doctorRepository;
        this.studentRepository = studentRepository;
        this.clerkshipRepository = clerkshipRepository;

    }

    @RequestMapping(value = "/export")
    public String home(){
        return "export";
    }

    @RequestMapping(value = "/downloadDoctorScheduleCSV")
    public void downloadDoctorCSV(HttpServletResponse response1) throws IOException {

        String csvFileName = "doctorsSchedule.csv";
        System.out.println(response1);

        response1.setContentType("text/csv"); //set content type of the response so that jQuery knows what it can expect


        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
        response1.setHeader(headerKey, headerValue);


        ArrayList<Doctor> listDoctors = new ArrayList<>();

        for(Doctor doc: doctorRepository.findAll()) {
            listDoctors.add(doc);
        }


        // uses the Super CSV API to generate CSV data from the model data
        ICsvBeanWriter csvWriter = null;
        try {
            csvWriter = new CsvBeanWriter(response1.getWriter(), CsvPreference.STANDARD_PREFERENCE);

            String[] header = {"ID", "Clerkship", "Name", "Email", "Profession", "Available"}; //must match field names in model (model must have getters for field)

            csvWriter.writeHeader(header);

            for (Doctor aDoc : listDoctors) {
                csvWriter.write(aDoc, header);
            }
        }
        finally {
            if(csvWriter != null) {
                csvWriter.close();
            }
        }

    }

    @RequestMapping(value = "/downloadStudentScheduleCSV")
    public void downloadStudentCSV(HttpServletResponse response2) throws IOException {

        String csvFileName = "studentsSchedule.csv";

        response2.setContentType("text/csv");

        // creates mock data
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"",
                csvFileName);
        response2.setHeader(headerKey, headerValue);


        ArrayList<Student> listStudents = new ArrayList<>();
        ArrayList<Clerkship> clerkList = new ArrayList<>();

        for(Student stu: studentRepository.findAll()) {
            String Name = stu.getName();
            Map<String, Clerkship> clerkships = stu.getClerkships();
            for(String key: clerkships.keySet()){
                clerkList.add(clerkships.get(key));
            }
            //String Neurology = clerkships.get("Neuro").getTime();
            //String Family_Medicine = clerkships.get("IM").getTime();
            //String Internal_Medicine = clerkships.get("FM").getTime();
            //String s[] = {Name,Neurology,Family_Medicine,Internal_Medicine};
            //stuList.addAll(Arrays.asList(s));
            //listStudents.add(stu);
        }


        // uses the Super CSV API to generate CSV data from the model data
        ICsvBeanWriter csvWriter = new CsvBeanWriter(response2.getWriter(),
                CsvPreference.STANDARD_PREFERENCE);

        String[] header = {"Student Name", "Title", "Time", "Location"};
        String[] fieldHead = {"studentName", "Title", "Time",  "Location"};

        csvWriter.writeHeader(header);

        for (Clerkship clerk : clerkList) {
            csvWriter.write(clerk, fieldHead);
        }

        csvWriter.close();
    }

}