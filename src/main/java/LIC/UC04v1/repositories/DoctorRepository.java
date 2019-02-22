package LIC.UC04v1.repositories;

import LIC.UC04v1.controllers.Specialty;
import LIC.UC04v1.model.Doctor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DoctorRepository extends CrudRepository<Doctor, Integer> {
    List<Doctor> findBySpecialtyInTextAndAvailable(Specialty specialty, boolean avail);
    List<Doctor> findBySpecialtyInTextAndAvailableAndLocation(Specialty specialty, boolean avail, String location);
}
