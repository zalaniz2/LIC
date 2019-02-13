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

import javax.servlet.http.HttpServletRequest;
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
        int counts[] = new int[20];
        for(Doctor doc: doctorRepository.findAll()){
            if((doc.getSpecialty().equals(profession))){
                for(int i = 0; i<20;i++){
                    if(doc.getAvailabilities().charAt(i) == '1'){
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

    @RequestMapping(value="/",method=RequestMethod.POST)

    public  @ResponseBody String save(@RequestBody Search search) {

        System.out.println("testing");
        String pName = search.getpName();
        String lName = search.getlName();
        System.out.println(lName + pName);
        // your logic next
        return "submit";
    }


}
