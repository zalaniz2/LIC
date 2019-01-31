package LIC.UC04v1.repositories;

import LIC.UC04v1.model.Student;
import org.springframework.data.repository.CrudRepository;

public interface StudentRepository extends CrudRepository<Student, String> {
}
