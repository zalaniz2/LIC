package LIC.UC04v1.services;

import LIC.UC04v1.model.Role;
import LIC.UC04v1.model.User;
import LIC.UC04v1.repositories.RoleRepository;
import LIC.UC04v1.repositories.UserRepository;
import LIC.UC04v1.services.UserService;
import LIC.UC04v1.services.security.EncryptionService;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This is only used for Spring security
 *
 */

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private EncryptionService encryptionService;
    private RoleService roleService;

    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setEncryptionService(EncryptionService encryptionService) {
        this.encryptionService = encryptionService;
    }

    @Override
    public List<?> listAll() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }

    @Override
    public Optional<User> getById(Integer id) {
        return userRepository.findById(id);
    }

    @Override
    public User saveOrUpdate(User user) {

        Role role = roleService.getById(2).get(); //ADMIN role
        List<Role> roles = user.getRoles();
        boolean contains = false;

        for(Role rol: roles){
            if(rol.getRole().equals("ADMIN")){contains = true;}
        }

       if(contains == false) {
           user.addRole(role);
       }
        if(user.getPassword() != null){
            user.setEncryptedPassword(encryptionService.encryptString(user.getPassword()));
        }
        return userRepository.save(user);
    }

    public void delete(Integer id) {
        userRepository.deleteById(id);
    }

    @Override
    public User findByUserName(String name) {
        return userRepository.findByUsername(name);
    }
}
