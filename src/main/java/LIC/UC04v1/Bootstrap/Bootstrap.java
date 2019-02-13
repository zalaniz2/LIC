package LIC.UC04v1.Bootstrap;

import LIC.UC04v1.controllers.Specialty;
import LIC.UC04v1.controllers.TimeSlot;
import LIC.UC04v1.model.Doctor;
import LIC.UC04v1.model.Student;
import LIC.UC04v1.repositories.ClerkshipRepository;
import LIC.UC04v1.repositories.DoctorRepository;
import LIC.UC04v1.repositories.StudentRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;

@Component
public class Bootstrap implements ApplicationListener<ContextRefreshedEvent> {
    private ClerkshipRepository clerkshipRepository;
    private DoctorRepository doctorRepository;
    private StudentRepository studentRepository;


    @java.lang.Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        try {
            initData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Bootstrap(ClerkshipRepository clerkshipRepository, DoctorRepository doctorRepository, StudentRepository studentRepository) {
        this.clerkshipRepository = clerkshipRepository;
        this.doctorRepository = doctorRepository;
        this.studentRepository = studentRepository;
    }
    private void initData() throws IOException {
        String fileName = "doctors.csv";

        ClassLoader classLoader = super.getClass().getClassLoader();

        File file = new File(classLoader.getResource(fileName).getFile());

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String st;
        br.readLine();
        String[] values;
        while ((st = br.readLine()) != null){
            values = st.split(",");
            Doctor doc = new Doctor();
            doc.setName(values[0]+" "+values[1]);
            doc.setEmail(values[2]);
            doc.setSpecialty(values[3]);
            doc.setAvailabilities(values[4]);
            doc.setSpecialtyInText(convertSpecialty(values[3]));
            doctorRepository.save(doc);
        }
        fileName = "student.csv";

        classLoader = super.getClass().getClassLoader();

        file = new File(classLoader.getResource(fileName).getFile());

        br = null;
        try {
            br = new BufferedReader(new FileReader(file));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        br.readLine();
        while ((st = br.readLine()) != null){
            values = st.split(",");
            Student stu = new Student();
            stu.setName(values[0]+" "+values[1]);
            stu.setEmail(values[2]);
            studentRepository.save(stu);
        }
    }


    private Specialty convertSpecialty(String specialty) {
        switch (specialty) {
            case "Neurology": return Specialty.Neurology;
            case "Family Medicine": return Specialty.FamilyMedicine;
            case "Internal Medicine": return Specialty.InternalMedicine;
            case "Surgery": return Specialty.Surgery;
            case "OBGYN": return Specialty.OBGYN;
            case "Pediatrics": return Specialty.Pediatrics;
            case "Psychiatry": return Specialty.Psychiatry;
        }
        return null;
    }
}