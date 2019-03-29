package LIC.UC04v1.repositories;

import LIC.UC04v1.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

public interface RoleRepository extends CrudRepository<Role, Integer> {
    Role findByRole(String role);

}