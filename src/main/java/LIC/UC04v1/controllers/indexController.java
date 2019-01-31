package LIC.UC04v1.controllers;

import LIC.UC04v1.model.Doctor;
import LIC.UC04v1.model.Student;
import LIC.UC04v1.repositories.DoctorRepository;
import LIC.UC04v1.repositories.StudentRepository;
import org.jboss.jandex.IndexReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
@Scope("request")
public class indexController {
    public String que = "";

//Test 1
    private DoctorRepository doctorRepository;
    private StudentRepository studentRepository;

    public indexController(DoctorRepository doctorRepository, StudentRepository studentRepository){
        this.doctorRepository = doctorRepository;
        this.studentRepository = studentRepository;
    }

    @GetMapping(path = "/{id}")
    public String getDoctors(Model model,@PathVariable String id){
        String stuID = id;
       /* int count = 0;
        ArrayList<Doctor> AvailDoctors = new ArrayList<Doctor>();
        for(Doctor doc: doctorRepository.findAll()){
            if(doc.getId()==3)doc.setAvailable(false);
            if(doc.isAvailable() && doc.getProfession().equals(que)){
                count++;
                AvailDoctors.add(doc);
            }
        }
        model.addAttribute("doctors", AvailDoctors);
        model.addAttribute("count", count);*/

        return "redirect:/" +stuID+"/Neurology";
    }

    @RequestMapping(path = "/{stuID}/{profession}")
    public String neuro(Model model, @PathVariable String stuID, @PathVariable String profession){
        Student stu = studentRepository.findById(stuID).orElse(null);
        if (stu!=null) {
            model.addAttribute("stu", stu);
        }

        int count = 0;
        ArrayList<Doctor> AvailNeuroDocs = new ArrayList<Doctor>();
        int counts[] = new int[28];
        for(Doctor doc: doctorRepository.findAll()){
            if((doc.getProfession().equals(profession))){
                for(int i = 0; i<28;i++){
                    if(doc.isAvailable().charAt(i) == '1'){
                        counts[i] = counts[i]+1;
                    }
                }
                count++;
                AvailNeuroDocs.add(doc);
            }
        }
        model.addAttribute("prof", profession);
        model.addAttribute("count", counts);

        return "index";
    }



    @RequestMapping(path = "/joke")
    public String joke(){
        System.out.println("joke");
        return "index";
    }
}
