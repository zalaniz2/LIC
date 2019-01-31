package LIC.UC04v1.controllers;


import LIC.UC04v1.repositories.DoctorRepository;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DoctorController {

/*    private DoctorRepository doctorRepository;

    public DoctorController(DoctorRepository doctorRepository){
        this.doctorRepository = doctorRepository;
    }



    @RequestMapping("/")
    public String getDoctors(Model model){
        model.addAttribute( "doctors", doctorRepository.findAll());

        return "index";
    }*/
}
