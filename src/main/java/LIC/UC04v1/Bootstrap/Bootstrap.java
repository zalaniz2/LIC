package LIC.UC04v1.Bootstrap;

import LIC.UC04v1.controllers.Location;
import LIC.UC04v1.controllers.Specialty;
import LIC.UC04v1.controllers.TimeSlot;
import LIC.UC04v1.model.Clerkship;
import LIC.UC04v1.model.Doctor;
import LIC.UC04v1.model.Student;
import LIC.UC04v1.repositories.ClerkshipRepository;
import LIC.UC04v1.repositories.DoctorRepository;
import LIC.UC04v1.repositories.StudentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;

@Slf4j
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
            doc.setLocation(values[5]);
            doc.setLocationInText(convertLocation(values[5]));
            doc.setNumberOfDaysAvail();
            doc.setSpecialty(values[3]);

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
//            Clerkship clerk1 = new Clerkship();
//            Clerkship clerk2 = new Clerkship();
//            Clerkship clerk3 = new Clerkship();
//            Clerkship clerk4 = new Clerkship();
//            Clerkship clerk5 = new Clerkship();
//            Clerkship clerk6 = new Clerkship();
//
//            clerk1.setTitle("Neurology");
//            clerk1.setStudent(stu);
//            clerk1.setStudentName(stu.getName());
//            clerk1.setTime(values[3]);
//            clerk1.setLocation("Dallas");
//
//            clerk2.setTitle("Family Medicine");
//            clerk2.setTime(values[4]);
//            clerk2.setStudent(stu);
//            clerk2.setStudentName(stu.getName());
//            clerk2.setLocation("Plano");
//
//            clerk3.setTitle("Internal Medicine");
//            clerk3.setTime(values[5]);
//            clerk3.setStudent(stu);
//            clerk3.setStudentName(stu.getName());
//            clerk3.setLocation("Fort Worth");
//
//            clerk4.setTitle("Surgery");
//            clerk4.setStudent(stu);
//            clerk4.setStudentName(stu.getName());
//            clerk4.setTime(values[6]);
//            clerk4.setLocation("Dallas");
//
//            clerk5.setTitle("OBGYN");
//            clerk5.setTime(values[7]);
//            clerk5.setStudent(stu);
//            clerk5.setStudentName(stu.getName());
//            clerk5.setLocation("Plano");
//
//            clerk6.setTitle("Phychiatry");
//            clerk6.setTime(values[8]);
//            clerk6.setStudent(stu);
//            clerk6.setStudentName(stu.getName());
//            clerk6.setLocation("Fort Worth");
//
//            stu.addClerkship(clerk1.getTitle(),clerk1);
//            stu.addClerkship(clerk2.getTitle(),clerk2);
//            stu.addClerkship(clerk3.getTitle(),clerk3);
//            stu.addClerkship(clerk4.getTitle(),clerk4);
//            stu.addClerkship(clerk5.getTitle(),clerk5);
//            stu.addClerkship(clerk6.getTitle(),clerk6);

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

    private Location convertLocation(String location) {
        switch (location) {
            case "Fort Worth": return Location.FortWorth;
            case "Denton": return Location.Denton;
            case "Dallas": return Location.Dallas;
            case "Keller/South Lake/Alliance": return Location.KellerSouthLakeAlliance;
            case "Arlington": return Location.Arlington;
            case "Mansfield": return Location.Mansfield;
        }
        return null;
    }
}