package LIC.UC04v1.Bootstrap;

import LIC.UC04v1.controllers.Location;
import LIC.UC04v1.controllers.MiscMethods;
import LIC.UC04v1.controllers.Specialty;
import LIC.UC04v1.controllers.TimeSlot;
import LIC.UC04v1.model.*;
import LIC.UC04v1.repositories.*;
import LIC.UC04v1.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Component
@Profile("dev")
public class Bootstrap implements ApplicationListener<ContextRefreshedEvent> {
    private ClerkshipRepository clerkshipRepository;
    private DoctorRepository doctorRepository;
    private StudentRepository studentRepository;
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private RoleRepository roleRepository;
    private MiscMethods misc;

    @java.lang.Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        try {
            initData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Autowired
    public Bootstrap(ClerkshipRepository clerkshipRepository, DoctorRepository doctorRepository, StudentRepository studentRepository,UserRepository userRepository,
                     RoleRepository roleRepository,
                     BCryptPasswordEncoder bCryptPasswordEncoder) {
        System.out.println("default bootstraping data");
        this.clerkshipRepository = clerkshipRepository;
        this.doctorRepository = doctorRepository;
        this.studentRepository = studentRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
        misc = new MiscMethods();
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
            doc.setName(values[0]);
            doc.setEmail(values[1]);
            doc.setAvailabilities(values[3]);
            doc.setSpecialty(misc.convertSpecialty(values[2]));
            doc.setLocation(misc.convertLocation(values[4]));
            doc.setNumStu(Integer.parseInt(values[5]));

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
            stu.setName(values[0]);
            stu.setEmail(values[1]);
            List<Doctor> phase1 = doctorRepository.findByEmail(values[2]);
            Doctor doc = phase1.get(0);

            stu.setPhase1Doc(doc);

            studentRepository.save(stu);
            doc.setPhase1Stu(stu);
            doc.setHasPhase1(false);
            doctorRepository.save(phase1.get(0));
        }
    }

    private void createAdmin(){
        User admin = new User();
        admin.setName("Super");
        admin.setLastName("Admin");
        admin.setActive(1);
        admin.setEmail("admin@superadmin.tcu");
        Role adminRole = roleRepository.findByRole("ADMIN");
      //  admin.setRoles(new HashSet<Role>(Arrays.asList(adminRole)));
        admin.setPassword(bCryptPasswordEncoder.encode("Admin123"));
        userRepository.save(admin);
    }

}