package LIC.UC04v1.services;

import LIC.UC04v1.model.Doctor;
import LIC.UC04v1.domain.doctorForm;
import java.util.Set;

public interface DoctorService {
    Set<Doctor> getDoctors();
    Doctor findById(int l);
    doctorForm saveDoctorForm(doctorForm docForm);
}
