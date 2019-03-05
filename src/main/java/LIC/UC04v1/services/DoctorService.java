package LIC.UC04v1.services;

import LIC.UC04v1.model.Doctor;
import LIC.UC04v1.controllers.doctorForm;
import java.util.Set;

public interface DoctorService {
    Set<Doctor> getDoctors();
    Doctor findById(String l);
    doctorForm saveDoctorForm(doctorForm docForm);
}
