package LIC.UC04v1.controllers;


import LIC.UC04v1.domain.doctorForm;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class DoctorController {

    @ModelAttribute("multiCheckboxAllValues")
    public String[] getMultiCheckboxAllValues() {
        return new String[] {
                "Monday", "Tuesday", "Wednesday", "Thursday",
                "Friday", "Saturday", "Sunday"
        };
    }
/*    private DoctorRepository doctorRepository;

    public DoctorController(DoctorRepository doctorRepository){
        this.doctorRepository = doctorRepository;
    }



    @RequestMapping("/")
    public String getDoctors(Model model){
        model.addAttribute( "doctors", doctorRepository.findAll());

        return "index";
    }*/
    @RequestMapping("new")
    public String docForm(Model model){
        model.addAttribute("doctor", new doctorForm());

        return "DoctorForm";
    }

    @PostMapping
    @RequestMapping("thankDoctor")
    public String recieveDoctorInfo(@ModelAttribute doctorForm command){
        System.out.println(""+command.MM+command.Location);

        return "redirect:/new";

    }
}
