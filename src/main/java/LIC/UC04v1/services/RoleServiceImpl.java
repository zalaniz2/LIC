package LIC.UC04v1.services;


import LIC.UC04v1.model.Role;
import LIC.UC04v1.model.User;
import LIC.UC04v1.repositories.RoleRepository;
import LIC.UC04v1.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by bingyang.wei on 5/10/2017.
 */
@Service
public class RoleServiceImpl implements RoleService{

    private RoleRepository roleRepository;

    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<?> listAll() {
        List<Role> roles = new ArrayList<>();
        roleRepository.findAll().forEach(roles::add);
        return roles;
    }

    @Override
    public Optional<Role> getById(Integer id) {
        return roleRepository.findById(id);
    }

    @Override
    public Role saveOrUpdate(Role domainObject) {
        return roleRepository.save(domainObject);
    }

    public void delete(Integer id) {
        roleRepository.deleteById(id);
    }

}
