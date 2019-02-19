package LIC.UC04v1.controllers;


import LIC.UC04v1.domain.doctorForm;
import LIC.UC04v1.model.Doctor;
import LIC.UC04v1.repositories.DoctorRepository;
import LIC.UC04v1.services.DoctorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
public class DoctorController {
    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }


    @ModelAttribute("multiCheckboxAllValues")
    public String[] getMultiCheckboxAllValues() {
        return new String[] {
                "Monday", "Tuesday", "Wednesday", "Thursday",
                "Friday", "Saturday", "Sunday"
        };
    }

    @RequestMapping("/doctor/{id}")
    public String docForm(Model model, @PathVariable int id){
        doctorForm docF = new doctorForm();
        docF.id = id;
        Doctor doc = doctorService.findById(id);
        if(doc.isAvailable().charAt(0)=='1') docF.MM = true;
        else  docF.MM = false;
        if(doc.getAvailable().charAt(1)=='1') docF.MA = true;
        else  docF.MA = false;
        if(doc.getAvailable().charAt(2)=='1') docF.TM = true;
        else  docF.TM = false;
        if(doc.getAvailable().charAt(3)=='1') docF.TA = true;
        else  docF.TA = false;
        if(doc.getAvailable().charAt(4)=='1') docF.WM = true;
        else  docF.WM = false;
        if(doc.getAvailable().charAt(5)=='1') docF.WA = true;
        else  docF.WA = false;
        if(doc.getAvailable().charAt(6)=='1') docF.RM = true;
        else  docF.RM = false;
        if(doc.getAvailable().charAt(7)=='1') docF.RA = true;
        else  docF.RA = false;
        if(doc.getAvailable().charAt(8)=='1') docF.FM = true;
        else  docF.FM = false;
        if(doc.getAvailable().charAt(9)=='1') docF.FA = true;
        else  docF.FA = false;
        if(doc.getAvailable().charAt(10)=='1') docF.AM = true;
        else  docF.AM = false;
        if(doc.getAvailable().charAt(11)=='1') docF.AA = true;
        else  docF.AA = false;
        if(doc.getAvailable().charAt(12)=='1') docF.UM = true;
        else  docF.UM = false;
        if(doc.getAvailable().charAt(13)=='1') docF.UA = true;
        else  docF.UA = false;



        model.addAttribute("doctor", docF);


        return "DoctorForm";
    }

    @RequestMapping("/student/{id}")
    public String stuForm(Model model, @PathVariable int id){
        model.addAttribute("doctor", new doctorForm());

        return "StudentForm";
    }

    @PostMapping
    @RequestMapping("thankDoctor")
    public void recieveDoctorInfo(@ModelAttribute doctorForm command){
        doctorService.saveDoctorForm(command);


    }
}
