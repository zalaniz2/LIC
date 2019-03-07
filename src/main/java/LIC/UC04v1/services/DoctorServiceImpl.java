package LIC.UC04v1.services;

import LIC.UC04v1.controllers.doctorForm;
import LIC.UC04v1.repositories.DoctorRepository;
import LIC.UC04v1.model.Doctor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class DoctorServiceImpl implements DoctorService{
    private final DoctorRepository doctorRepository;

    public DoctorServiceImpl(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }



    @Override
    public Set<Doctor> getDoctors(){

        Set<Doctor> doctorSet = new HashSet<>();
        doctorRepository.findAll().iterator().forEachRemaining(doctorSet::add);

        return doctorSet;
    }

    @Override
    public Doctor findById(String l){
        Optional<Doctor> doctorOptional = doctorRepository.findById(l);

        if(!doctorOptional.isPresent()){
            throw new RuntimeException("Doctor Not Found!");
        }


        return doctorOptional.get();
    }

    @Override
    @Transactional
    public doctorForm saveDoctorForm(doctorForm docForm) {
        Doctor detachedDoctor = findById(docForm.id);
        String availStr = "";
        if(docForm.MM) availStr += "1";
        else availStr += "0";
        if(docForm.MA) availStr += "1";
        else availStr += "0";
        if(docForm.TM) availStr += "1";
        else availStr += "0";
        if(docForm.TA) availStr += "1";
        else availStr += "0";
        if(docForm.WM) availStr += "1";
        else availStr += "0";
        if(docForm.WA) availStr += "1";
        else availStr += "0";
        if(docForm.RM) availStr += "1";
        else availStr += "0";
        if(docForm.RA) availStr += "1";
        else availStr += "0";
        if(docForm.FM) availStr += "1";
        else availStr += "0";
        if(docForm.FA) availStr += "1";
        else availStr += "0";
        if(docForm.AM) availStr += "1";
        else availStr += "0";
        if(docForm.AA) availStr += "1";
        else availStr += "0";
        availStr = availStr + availStr;
//        if(docForm.UM) availStr += "1";
//        else availStr += "0";
//        if(docForm.UA) availStr += "1";
//        else availStr += "0";
        availStr += availStr;
        detachedDoctor.setLocation(docForm.location);
        detachedDoctor.setAvailabilities(availStr);
        doctorRepository.save(detachedDoctor);
        return null;
    }
}
