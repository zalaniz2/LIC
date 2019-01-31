package LIC.UC04v1.controllers;

import LIC.UC04v1.model.Doctor;
import LIC.UC04v1.repositories.DoctorRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.print.Doc;
import java.util.ArrayList;

@Controller
public class testViewController {

    private String specialty;
    private DoctorRepository doctorRepository;
    private ArrayList<Doctor> AvailDoctors = new ArrayList<Doctor>();
    private int docCount;
    private ArrayList<String> curSchedule = new ArrayList<String>();



    public testViewController(DoctorRepository doctorRepository){
        this.doctorRepository = doctorRepository;
    }

    public void getDoctors(){

        docCount = 0;

        for (Doctor doc : doctorRepository.findAll()) {

            if (doc.getProfession().equals(specialty)) {
                docCount++;
                AvailDoctors.add(doc);
            }
        }

    }


    @GetMapping(path = "/test")
    public String index(){

        System.out.println("Main page.");
        return "testView";

    }

    @RequestMapping("/specialty")
    public String specialty(Model model, @RequestParam("id") String spec){

        System.out.println("got here after /schedule");

        specialty = spec;

        getDoctors();

        System.out.println("Count for " + specialty + " doctors is " + docCount);

        model.addAttribute("doctors", AvailDoctors);
        model.addAttribute("count", docCount);
        model.addAttribute("value", specialty);
        model.addAttribute("schedule", curSchedule);

        return "testView";


    }

    @RequestMapping(value="/schedule", method=RequestMethod.POST)
    public String schedule(@RequestParam("id") String gotSpecialty, @RequestParam("time") String gotTime){
        System.out.println("got schedule");

        System.out.println(gotSpecialty);
        System.out.println(gotTime);


        curSchedule.add(gotSpecialty);
        curSchedule.add(gotTime);


        return "index";

    }
}
