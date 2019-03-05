package LIC.UC04v1.repositories;

import LIC.UC04v1.controllers.Location;
import LIC.UC04v1.controllers.Specialty;
import LIC.UC04v1.model.Doctor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DoctorRepository extends CrudRepository<Doctor, String> {
    List<Doctor> findBySpecialtyAndAvailable(Specialty specialty, boolean avail);
    List<Doctor> findBySpecialtyAndAvailableAndLocation(Specialty specialty, boolean avail, Location location);
}
